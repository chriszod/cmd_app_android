package com.cmd.cmd_app_android.view.fragments.verification

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.utils.handleError
import com.cmd.cmd_app_android.view.utils.onChange
import com.cmd.cmd_app_android.viewmodel.SignupViewModel
import com.cmd.cmd_app_android.viewmodel.UiEvents
import com.cmd.cmd_app_android.viewmodel.VerificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.FragmentOtpBinding
import thecmdteam.cmd_app_android.databinding.FragmentPasswordBinding

@AndroidEntryPoint
class OtpFragment: Fragment(R.layout.fragment_otp) {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VerificationViewModel by viewModels()
    private val args: OtpFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentOtpBinding.bind(view)

        if(args.otp == null) {
            viewModel.execute(VerificationEvents.GetOtp)
        } else {
            viewModel.execute(VerificationEvents.PostOtp(args.otp!!))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiEvents.collect {
                when(it) {
                    is UiEvents.WrongOtp -> {
                        binding.otpErrorLayout.visibility = View.VISIBLE
                        binding.resendButton.setOnClickListener {
                            viewModel.execute(VerificationEvents.GetOtp)
                            binding.resendButton.isClickable = false
                        }
                    }
                    UiEvents.OtpVerifiedSuccessfully -> {
                        findNavController().navigate(R.id.action_otpFragment_to_passwordFragment)
                    }
                }
            }
        }

        binding.otpTextField.onChange {
            viewModel.execute(VerificationEvents.ChangeOtpTextField(it))
        }

        binding.verifyButton.setOnClickListener {
            viewModel.execute(VerificationEvents.VerifyOtp)
            Log.d("TAG", "onViewCreated: clicking")
        }

        lifecycleScope.launchWhenStarted {
            viewModel.verificationState.collect {
                if (it.loading) {
                    binding.loading(requireContext())
                }
                if(it.user != defaultUser && !it.loading){
                    binding.success(requireContext())
                    findNavController().navigate(R.id.action_otpFragment_to_passwordFragment)
                }
                if(it.error.isNotBlank() && !it.loading){
                    binding.error(requireContext())
                }
                binding.apply {
                    otpError.handleError(it.otp.errorMessage, it.otp.valid)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun FragmentOtpBinding.loading(context: Context) {
    this.apply {
        verificationText.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        verifyButton.isClickable = false
        verifyButton.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button_loading)
    }

}

fun FragmentOtpBinding.success(context: Context) {
    this.apply {
        verificationText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        verifyButton.isClickable = true
        verifyButton.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
    }

}

fun FragmentOtpBinding.error(context: Context) {
    this.apply {
        verificationText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        verifyButton.isClickable = true
        verifyButton.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
    }
}