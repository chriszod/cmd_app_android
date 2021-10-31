package com.cmd.cmd_app_android.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cmd.cmd_app_android.domain.repository.DatastoreRepository
import com.cmd.cmd_app_android.view.activities.MainActivity
import com.cmd.cmd_app_android.view.activities.StarterActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.FragmentSplashBinding
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash){
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    
    @Inject
    lateinit var repository: DatastoreRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launchWhenStarted{
            val data = repository.userPreferences.first()
            if (!(data["is_email_verified"] as Boolean) && (data["email"] as String).isNotBlank()) {
                delay(200L)
                findNavController().navigate(R.id.action_splashFragment_to_emailValidationFragment)
            }
            if((data["is_email_verified"] as Boolean) && (data["user_id"] as String) != ""){
                delay(3000L)
                Intent(requireContext(), MainActivity::class.java).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }.also {
                    requireContext().startActivity(it)
                }
            }
            if((data["user_id"] as String).isBlank() && (data["email"] as String).isBlank()){
                delay(3000L)
                findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment)
            }
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

