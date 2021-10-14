package com.cmd.cmd_app_android.view.activities

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.ActivityStarterBinding

@AndroidEntryPoint
class StarterActivity:AppCompatActivity() {
    private lateinit var binding: ActivityStarterBinding
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityStarterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navhostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navhostFragment.navController
    }
}