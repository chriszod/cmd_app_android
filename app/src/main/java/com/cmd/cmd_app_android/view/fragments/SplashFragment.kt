package com.cmd.cmd_app_android.view.fragments

import android.os.Bundle
import android.service.autofill.Validators.or
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cmd.cmd_app_android.domain.repository.DatastoreRepository
import com.cmd.cmd_app_android.domain.usecases.GetUserInfoFromDatastore
import com.cmd.cmd_app_android.domain.usecases.UserUseCases
import com.cmd.cmd_app_android.view.activities.MainActivity
import com.cmd.cmd_app_android.view.activities.StarterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.FragmentSplashBinding
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash){
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenStarted{
            delay(3000L)
            findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment)

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onStart() {
        super.onStart()
        (activity as StarterActivity).window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        or View.SYSTEM_UI_FLAG_FULLSCREEN
        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

    }
}

