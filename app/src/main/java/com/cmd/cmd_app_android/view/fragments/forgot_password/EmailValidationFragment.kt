package com.cmd.cmd_app_android.view.fragments.forgot_password

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.utils.handleError
import com.cmd.cmd_app_android.view.utils.onChange
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.FragmentEmailValidationBinding

@AndroidEntryPoint
class EmailValidationFragment: Fragment(R.layout.fragment_email_validation) {

    private val viewModel: EmailValidationViewModel by viewModels()
    private var _binding: FragmentEmailValidationBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentEmailValidationBinding.bind(view)

        binding.emailTextField.onChange {
            viewModel.execute(EmailValidationEvents.EmailTextChange(it))
        }

        binding.buttonResetPassword.setOnClickListener {
            viewModel.execute(EmailValidationEvents.ResetPassword)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                when(it) {
                    is UiState.Success -> {
                        val action = EmailValidationFragmentDirections.actionEmailValidationFragmentToOtpFragment(null)
                        findNavController().navigate(action)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.emailValidationState.collect {
                if (it.loading) {
                    binding.loading(requireContext())
                }
                if(it.user != defaultUser && !it.loading){
                    binding.success(requireContext())
                }
                if(it.error.isNotBlank() && !it.loading){
                    binding.error(requireContext())
                }
                binding.apply {
                    emailErrorText.handleError(it.error, it.valid)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun FragmentEmailValidationBinding.loading(context: Context) {
    this.apply {
        buttonResetPassword.isClickable = false
        buttonResetPassword.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button_loading)
        resetPasswordText.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

}

fun FragmentEmailValidationBinding.success(context: Context) {
    this.apply {
        buttonResetPassword.isClickable = true
        buttonResetPassword.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
        resetPasswordText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

}

fun FragmentEmailValidationBinding.error(context: Context) {
    this.apply {
        buttonResetPassword.isClickable = true
        buttonResetPassword.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
        resetPasswordText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }
}