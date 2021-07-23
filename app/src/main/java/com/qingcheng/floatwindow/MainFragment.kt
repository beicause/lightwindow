package com.qingcheng.floatwindow

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.qingcheng.floatwindow.service.MainAccessibilityService
import com.qingcheng.floatwindow.util.PermissionRequestUtil
import com.qingcheng.floatwindow.view.GuideView
import com.qingcheng.floatwindow.view.ViewManager

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onStart() {
        super.onStart()
        val controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        requireActivity().findViewById<Button>(R.id.btn_settings).setOnClickListener {
            controller.navigate(R.id.action_MainFragment_to_settingsFragment)
        }
        requireActivity().findViewById<Button>(R.id.btn_doc).setOnClickListener {
            controller.navigate(R.id.action_MainFragment_to_guideFragment)
        }
        requireActivity().findViewById<SwitchCompat>(R.id.switch_service).setOnClickListener {
            if (PermissionRequestUtil.isOverlays(requireContext()))
                Toast.makeText(requireContext(), "请手动设置轻程的无障碍功能", Toast.LENGTH_SHORT)
                    .apply {
                        show()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                PermissionRequestUtil.requestAccessibilityPermission(requireContext())
                                cancel()
                            }, 500
                        )
                    }
            else PermissionRequestUtil.requestOverlaysPermissionDialog(requireActivity())
        }

        requireActivity().findViewById<ImageView>(R.id.iv_big_band).setOnClickListener {
            ViewManager.get(GuideView::class).apply {
                if (!isAddToWindow){
                    addToWindow()
                    (this as GuideView).textViewUtil.printNextText()
                }
                else removeFromWindow()
            }
        }
    }

    override fun onResume() {
        requireActivity().findViewById<SwitchCompat>(R.id.switch_service).apply {
            isChecked = MainAccessibilityService.isEnable
            if (isChecked) {
                text = "服务已开启"
                setBackgroundColor(
                    requireContext().resources.getColor(
                        R.color.purple_500,
                        null
                    )
                )
            } else {
                setBackgroundColor(
                    requireContext().resources.getColor(
                        R.color.anti_purple,
                        null
                    )
                )
                text = "服务已关闭"
            }
        }
        super.onResume()
    }

    override fun onPause() {
        ViewManager.get(GuideView::class).removeFromWindow()
        super.onPause()
    }
}