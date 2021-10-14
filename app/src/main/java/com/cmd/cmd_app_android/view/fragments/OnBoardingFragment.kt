package com.cmd.cmd_app_android.view.fragments

import android.os.Bundle
import android.service.autofill.Validators
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cmd.cmd_app_android.view.activities.StarterActivity
import dagger.hilt.android.AndroidEntryPoint
import thecmdteam.cmd_app_android.R

@AndroidEntryPoint
class OnBoardingFragment:Fragment(R.layout.fragment_onboarding) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.button).setOnClickListener {
            findNavController().navigate(R.id.action_onBoardingFragment_to_signInFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as StarterActivity).window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

    }
}