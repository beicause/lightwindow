package com.qingcheng.floatwindow

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.qingcheng.floatwindow.service.ForegroundService
import com.qingcheng.floatwindow.util.CacheName.CACHE_EMAIL
import com.qingcheng.floatwindow.util.CacheName.CACHE_HFUT_TOKEN
import com.qingcheng.floatwindow.util.CacheName.CACHE_HFUT_USERNAME
import com.qingcheng.floatwindow.util.CacheName.CACHE_TOKEN
import com.qingcheng.floatwindow.util.CacheName.SP_NAME_SETTINGS
import com.qingcheng.floatwindow.util.NetworkRequest
import com.qingcheng.floatwindow.util.PermissionRequestUtil
import com.qingcheng.floatwindow.util.SharedPreferencesUtil
import okhttp3.*
import org.json.JSONObject

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = SP_NAME_SETTINGS
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        setClickRequestPermission("float") { PermissionRequestUtil.requestOverlaysPermission(requireContext()) }
        setClickRequestPermission("accessibility") {
            PermissionRequestUtil.requestAccessibilityPermission(
                requireContext()
            )
        }

        findPreference<SwitchPreferenceCompat>("power")?.apply {
            setOnPreferenceClickListener {
                if (PermissionRequestUtil.isIgnoreBattery(requireContext())) {
                    Toast.makeText(
                        requireContext(),
                        "请手动前往设置",
                        Toast.LENGTH_SHORT
                    ).show()
                    isChecked = true
                } else PermissionRequestUtil.requestIgnoreBatteryPermission(requireContext())
                return@setOnPreferenceClickListener true
            }
        }

        findPreference<SwitchPreferenceCompat>("school")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean) showSchoolLoginDialog()
                else showIdentifyDialog("确定清空登陆信息和载入的课表吗?", {
                    SharedPreferencesUtil.remove(requireContext(), CACHE_HFUT_TOKEN, CACHE_HFUT_USERNAME)
                    findPreference<SwitchPreferenceCompat>("school")?.summary = null
                }, {
                    findPreference<SwitchPreferenceCompat>("school")?.isChecked = true
                })
                return@setOnPreferenceChangeListener true
            }
        }
        findPreference<SwitchPreferenceCompat>("email")?.apply {
            setOnPreferenceChangeListener { _, newValue ->
                if (newValue as Boolean) showEmailLoginDialog()
                else showIdentifyDialog("确定解除绑定吗?", {
                    SharedPreferencesUtil.remove(requireContext(), CACHE_EMAIL, CACHE_TOKEN)
                    findPreference<SwitchPreferenceCompat>("email")?.summary = null
                }, {
                    findPreference<SwitchPreferenceCompat>("email")?.isChecked = true
                })
                return@setOnPreferenceChangeListener true
            }
        }
    }

    override fun onResume() {
        findPreference<SwitchPreferenceCompat>("notice")?.isChecked = ForegroundService.isEnable
        findPreference<SwitchPreferenceCompat>("float")?.isChecked =
            PermissionRequestUtil.isOverlays(requireContext())
        findPreference<SwitchPreferenceCompat>("accessibility")?.isChecked =
            PermissionRequestUtil.isAccessibility()
        findPreference<SwitchPreferenceCompat>("power")?.isChecked =
            PermissionRequestUtil.isIgnoreBattery(requireContext())
        findPreference<SwitchPreferenceCompat>("school")?.apply {
            if (SharedPreferencesUtil.contains(requireContext(), CACHE_HFUT_TOKEN)) {
                summary = "已绑定:合肥工业大学\n${
                    SharedPreferencesUtil.getString(
                        requireContext(),
                        CACHE_HFUT_USERNAME
                    )
                }"
                isChecked = true
            } else {
                isChecked = false
                summary = null
            }
        }
        findPreference<SwitchPreferenceCompat>("email")?.apply {
            if (SharedPreferencesUtil.contains(requireContext(), CACHE_TOKEN)) {
                summary = "已绑定邮箱:\n${
                    SharedPreferencesUtil.getString(
                        requireContext(),
                        CACHE_EMAIL
                    )
                }"
                isChecked = true
            } else {
                isChecked = false
                summary = null
            }
        }
        super.onResume()
    }

    private fun setClickRequestPermission(key: String, request: () -> Unit) {
        findPreference<SwitchPreferenceCompat>(key)?.setOnPreferenceClickListener {
            Toast.makeText(requireContext(), "请手动设置", Toast.LENGTH_SHORT).apply {
                show()
                Handler(Looper.getMainLooper()).postDelayed(
                    {
                        request()
                        cancel()
                    }, 500
                )
            }
            return@setOnPreferenceClickListener true
        }
    }

    private fun showSchoolLoginDialog() {
        val view = View.inflate(requireContext(), R.layout.school_login_dialog, null)
        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle("登陆")
            .setView(view)
            .setCancelable(false)
            .setPositiveButton("登陆") { _, _ -> }
            .setNegativeButton("取消") { _, _ ->
                findPreference<SwitchPreferenceCompat>("school")?.apply {
                    summary = null
                    isChecked = false
                }
            }
            .create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            when (view.findViewById<Spinner>(R.id.spinner_school).selectedItem) {
                "请选择" -> requireActivity().runOnUiThread {
                    view.findViewById<TextView>(R.id.tv_msg).text = "请选择学校"
                }

                "合肥工业大学" -> {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false
                    NetworkRequest.hfutLogin(
                        view.findViewById<EditText>(R.id.et_username).text.toString(),
                        view.findViewById<EditText>(R.id.et_password).text.toString(),
                        {
                            requireActivity().runOnUiThread {
                                view.findViewById<TextView>(R.id.tv_msg).text = "网络异常"
                            }
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = true
                        },
                        {
                            if (it.body != null) {
                                val res = JSONObject(it.body!!.string())
                                requireActivity().runOnUiThread {
                                    view.findViewById<TextView>(R.id.tv_msg).text =
                                        res.getString("msg")
                                }
                                if (res.getInt("code") == 200) {
                                    val token = res.getJSONObject("data").getString("token")
                                    val username = res.getJSONObject("data").getString("username")
                                    SharedPreferencesUtil.put(
                                        requireContext(),
                                        CACHE_HFUT_TOKEN,
                                        token
                                    )
                                    SharedPreferencesUtil.put(
                                        requireContext(),
                                        CACHE_HFUT_USERNAME,
                                        username
                                    )
                                    requireActivity().runOnUiThread {
                                        findPreference<SwitchPreferenceCompat>("school")?.summary =
                                            "已绑定:合肥工业大学\n${username}"
                                    }
                                    requireActivity().runOnUiThread {
                                        Toast.makeText(requireContext(), "登陆成功", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                    dialog.dismiss()
                                }
                            }
                            requireActivity().runOnUiThread {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = true
                            }
                        })
                }
            }
        }
    }

    private fun showEmailLoginDialog() {
        val view = View.inflate(requireContext(), R.layout.email_login_dialog, null)
        val dialog = AlertDialog.Builder(requireActivity())
            .setTitle("绑定邮箱")
            .setView(view)
            .setCancelable(false)
            .setPositiveButton("确定") { _, _ -> }
            .setNegativeButton("取消") { _, _ ->
                findPreference<SwitchPreferenceCompat>("email")?.apply {
                    summary = null
                    isChecked = false
                }
            }
            .create()
        dialog.show()
        view.findViewById<Button>(R.id.btn_login).apply {
            setOnClickListener {
                isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({ isEnabled = true }, 30000)
                NetworkRequest.sendEmail(view.findViewById<EditText>(R.id.et_email).text.toString(),
                    {
                        Toast.makeText(requireContext(), "网络异常", Toast.LENGTH_SHORT).show()
                    }, {
                        val msg=it.body?.let { res -> JSONObject(res.string()).getString("msg")}
                        requireActivity().runOnUiThread {
                            view.findViewById<TextView>(R.id.tv_msg).text = msg
                        }
                    })
            }
        }
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false
            NetworkRequest.login(
                view.findViewById<EditText>(R.id.et_email).text.toString(),
                view.findViewById<EditText>(R.id.et_code).text.toString(),
                {
                    requireActivity().runOnUiThread {
                        view.findViewById<TextView>(R.id.tv_msg).text = "网络异常"
                    }
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = true
                },
                {
                    if (it.body != null) {
                        val res = JSONObject(it.body!!.string())
                        requireActivity().runOnUiThread {
                            view.findViewById<TextView>(R.id.tv_msg).text =
                                res.getString("msg")
                        }
                        if (res.getInt("code") == 1) {
                            val token = res.getString("token")
                            val email = res.getString("email")
                            SharedPreferencesUtil.put(
                                requireContext(),
                                CACHE_TOKEN,
                                token
                            )
                            SharedPreferencesUtil.put(
                                requireContext(),
                                CACHE_EMAIL,
                                email
                            )
                            requireActivity().runOnUiThread {
                                findPreference<SwitchPreferenceCompat>("email")?.summary =
                                    "已绑定邮箱:\n${email}"
                            }
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "绑定成功", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            dialog.dismiss()
                        }
                    }
                    requireActivity().runOnUiThread {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = true
                    }
                })
        }
    }

    private fun showIdentifyDialog(msg: String, positive: () -> Unit, negative: () -> Unit) {
        val alertDialogBuild = AlertDialog.Builder(requireActivity())
        alertDialogBuild.setTitle("提示")
            .setMessage(msg)
            .setPositiveButton("确定") { _, _ ->
                positive()
            }
            .setNegativeButton("取消") { _, _ ->
                negative()
            }
            .setOnCancelListener {
                negative()
            }
            .create()
            .show()
    }
}