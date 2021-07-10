package com.qingcheng.floatwindow

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.qingcheng.floatwindow.service.ForegroundService

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (this.getSharedPreferences("settings",Context.MODE_PRIVATE).getBoolean("notice",true))startForegroundService(Intent(this,ForegroundService::class.java))
        val controller= (supportFragmentManager.findFragmentById(R.id.nav_host_fragment)as NavHostFragment).navController
        NavigationUI.setupActionBarWithNavController(this,controller)
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this,R.id.nav_host_fragment).navigateUp()
    }
}
