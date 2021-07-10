package com.qingcheng.floatwindow

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.qingcheng.floatwindow.service.MyAccessibilityService
import com.qingcheng.floatwindow.util.PermissionUtil

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
            if (PermissionUtil.isOverlays(requireContext()))
                Toast.makeText(requireContext(), "请手动设置轻程的无障碍功能", Toast.LENGTH_SHORT)
                    .apply {
                        show()
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                PermissionUtil.requestAccessibilityPermission(requireContext())
                                cancel()
                            }, 500
                        )
                    }
            else PermissionUtil.requestOverlaysPermission(requireActivity())
        }
    }

    override fun onResume() {
        requireActivity().findViewById<SwitchCompat>(R.id.switch_service).apply {
            isChecked = MyAccessibilityService.isEnable
            if (isChecked) {
                text = "服务已开启"
                setBackgroundColor(requireContext().resources.getColor(R.color.purple_500, null))
            } else {
                setBackgroundColor(
                    requireContext().resources.getColor(
                        R.color.holo_orange_light,
                        null
                    )
                )
                text = "服务已关闭"
            }
        }
        super.onResume()
    }
}