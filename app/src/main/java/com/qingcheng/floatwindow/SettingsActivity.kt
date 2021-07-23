package com.qingcheng.floatwindow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val controller= (supportFragmentManager.findFragmentById(R.id.nav_host_fragment)as NavHostFragment).navController
        NavigationUI.setupActionBarWithNavController(this,controller)
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this,R.id.nav_host_fragment).navigateUp()
    }
}