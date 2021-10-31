package com.cmd.cmd_app_android.view.fragments.otp

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.utils.gone
import com.cmd.cmd_app_android.view.utils.handleError
import com.cmd.cmd_app_android.view.utils.onChange
import com.cmd.cmd_app_android.view.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.FragmentOtpBinding

@AndroidEntryPoint
class OtpFragment: Fragment(R.layout.fragment_otp) {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OtpViewModel by viewModels()
    private val args: OtpFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentOtpBinding.bind(view)

        if(args.otp == null) {
            viewModel.execute(OtpEvents.GetOtp)
        } else {
            viewModel.execute(OtpEvents.PostOtp(args.otp!!))
        }

        viewModel.execute(OtpEvents.PostUser(args.user!!))

        binding.resendButton.setOnClickListener {
            viewModel.execute(OtpEvents.GetOtp)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiEvents.collect {
                when(it) {
                    UiEvents.OtpVerifiedSuccessfully -> {
                        findNavController().navigate(R.id.action_otpFragment_to_passwordFragment)
                    }
                    is UiEvents.GettingOtp -> {
                        binding.loadingLayout.root.visible()
                        binding.loadingLayout.textMessage.text = resources.getString(R.string.please_wait)
                    }
                    is UiEvents.OtpError -> {
                        binding.loadingLayout.root.gone()
                    }
                    is UiEvents.OtpSuccess -> {
                        binding.loadingLayout.root.gone()
                    }
                }
            }
        }

        binding.otpTextField.onChange {
            viewModel.execute(OtpEvents.ChangeOtpTextField(it))
        }

        binding.verifyButton.setOnClickListener {
            viewModel.execute(OtpEvents.VerifyOtp)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.otpState.collect {
                if (it.loading) {
                    binding.loading(requireContext())
                }
                if(it.user != defaultUser && !it.loading){
                    binding.success(requireContext())
                    binding.resendButton.isClickable = false
                }
                if(it.error.isNotBlank() && !it.loading){
                    binding.error(requireContext())
                    binding.resendButton.isClickable = true
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
        resendButton.isClickable = false
        resendButtonText.text = context.getString(R.string.please_wait)
        resendButton.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button_loading)
        verifyButtonText.gone()
        progressBar.visible()
        verifyButton.isClickable = false
        verifyButton.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button_loading)
    }

}

fun FragmentOtpBinding.success(context: Context) {
    this.apply {
        resendButton.isClickable = true
        resendButtonText.text = context.getString(R.string.resend)
        resendButton.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
        verifyButtonText.visible()
        progressBar.gone()
        verifyButton.isClickable = true
        verifyButton.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
    }

}

fun FragmentOtpBinding.error(context: Context) {
    this.apply {
        resendButton.isClickable = true
        resendButtonText.text = context.getString(R.string.resend)
        resendButton.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
        verifyButtonText.visible()
        progressBar.gone()
        verifyButton.isClickable = true
        verifyButton.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
    }
}