package com.cmd.cmd_app_android.view.fragments

import android.service.autofill.Validators
import android.view.View
import androidx.fragment.app.Fragment
import com.cmd.cmd_app_android.view.activities.StarterActivity
import thecmdteam.cmd_app_android.R

class OnBoardingFragment:Fragment(R.layout.fragment_onboarding) {
    override fun onStart() {
        super.onStart()
        (activity as StarterActivity).window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE)

    }
}