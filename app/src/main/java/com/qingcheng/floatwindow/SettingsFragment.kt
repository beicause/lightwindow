package com.qingcheng.floatwindow

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.qingcheng.floatwindow.service.ForegroundService
import com.qingcheng.floatwindow.util.PermissionUtil
import com.qingcheng.floatwindow.util.SharedPreferencesUtil
import com.qingcheng.floatwindow.util.SmileSchoolTokenUtil

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = "settings"
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        clickRequestPermission("float") { PermissionUtil.requestOverlaysPermission(requireContext()) }
        clickRequestPermission("accessibility") { PermissionUtil.requestAccessibilityPermission(requireContext()) }
        clickRequestPermission("read"){PermissionUtil.requestReadPermission(requireContext())}

        findPreference<SwitchPreferenceCompat>("notice")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) requireContext().startForegroundService(
                Intent(
                    requireContext(),
                    ForegroundService::class.java
                )
            )
            else {
                requireContext().stopService(
                    Intent(
                        requireContext(),
                        ForegroundService::class.java
                    )
                )
            }
            return@setOnPreferenceChangeListener true
        }
//        findPreference<SwitchPreferenceCompat>("hide")?.setOnPreferenceChangeListener { _, newValue ->
//            val manager: ActivityManager =
//                requireContext().getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
//            if (newValue as Boolean) manager.appTasks.forEach { it.setExcludeFromRecents(true) }
//            else manager.appTasks.forEach { it.setExcludeFromRecents(false) }
//            return@setOnPreferenceChangeListener true
//        }
        findPreference<SwitchPreferenceCompat>("power")?.apply {
            setOnPreferenceClickListener {
                if (PermissionUtil.isIgnoreBattery(requireContext())) {
                    Toast.makeText(
                        requireContext(),
                        "请手动前往设置",
                        Toast.LENGTH_SHORT
                    ).show()
                    isChecked = true
                } else PermissionUtil.requestIgnoreBatteryPermission(requireContext())
                return@setOnPreferenceClickListener true
            }
        }
        findPreference<SwitchPreferenceCompat>("token")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                SmileSchoolTokenUtil.initToken(requireContext())
                if (SharedPreferencesUtil.getString(requireContext(), "token") != "")
                    Toast.makeText(
                        requireContext(),
                        "token:" + SharedPreferencesUtil.getString(requireContext(), "token"),
                        Toast.LENGTH_SHORT
                    ).show()
                else Toast.makeText(requireContext(), "获取失败", Toast.LENGTH_SHORT).show()
            } else SharedPreferencesUtil.put(requireContext(), "token", "")
            return@setOnPreferenceChangeListener true
        }

    }

    override fun onResume() {
        findPreference<SwitchPreferenceCompat>("notice")?.isChecked = ForegroundService.isEnable
        findPreference<SwitchPreferenceCompat>("float")?.isChecked = PermissionUtil.isOverlays(requireContext())
        findPreference<SwitchPreferenceCompat>("accessibility")?.isChecked = PermissionUtil.isAccessibility()
        findPreference<SwitchPreferenceCompat>("read")?.isChecked=PermissionUtil.isRead(requireContext())
        findPreference<SwitchPreferenceCompat>("power")?.isChecked = PermissionUtil.isIgnoreBattery(requireContext())

//        val manager: ActivityManager =
//            requireContext().getSystemService(Service.ACTIVITY_SERVICE) as ActivityManager
//        if (preferenceManager.sharedPreferences.getBoolean("hide", false)) {
//            findPreference<SwitchPreferenceCompat>("hide")?.isChecked = true
//            manager.appTasks.forEach { it.setExcludeFromRecents(true) }
//        } else {
//            findPreference<SwitchPreferenceCompat>("hide")?.isChecked = false
//            manager.appTasks.forEach { it.setExcludeFromRecents(false) }
//        }
        super.onResume()
    }

    private fun clickRequestPermission(key: String, request: () -> Unit) {
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
}