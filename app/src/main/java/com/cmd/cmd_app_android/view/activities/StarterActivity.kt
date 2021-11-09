package com.cmd.cmd_app_android.view.activities

import android.os.Bundle
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
        val navGraph = navController.navInflater.inflate(R.navigation.starter_graph)
        val isFromMainActivity = intent.getBooleanExtra("is_from_main_activity", false)
        if(isFromMainActivity) {
            navGraph.startDestination = R.id.signInFragment
        } else {
            navGraph.startDestination = R.id.splashFragment
        }
        navController.graph = navGraph
    }
}