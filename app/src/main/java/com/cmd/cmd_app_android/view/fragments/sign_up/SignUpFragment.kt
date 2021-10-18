package com.cmd.cmd_app_android.view.fragments.sign_up

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cmd.cmd_app_android.view.utils.handleError
import com.cmd.cmd_app_android.view.utils.onChange
import com.cmd.cmd_app_android.viewmodel.SignupViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.utils.NO_INTERNET_CONNECTION
import com.cmd.cmd_app_android.view.utils.makeAlertDialog
import com.cmd.cmd_app_android.viewmodel.SignupUiEvents
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.FragmentSignupBinding

@AndroidEntryPoint
class SignUpFragment: Fragment(R.layout.fragment_signup) {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignupViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSignupBinding.bind(view)

        lifecycleScope.launchWhenStarted {
            viewModel.uiEvents.collect {
                when(it){
                    SignupUiEvents.NoInternetConnection -> {
                        makeAlertDialog(requireContext(), NO_INTERNET_CONNECTION).setTitle("Network").create().show()
                    }
                    is SignupUiEvents.Error -> {
                        makeAlertDialog(requireContext(), it.error).setTitle("Error").create().show()
                    }
                }
            }
        }

        binding.firstNameTextField.onChange {
            viewModel.execute(SignupEvents.FirstNameTextChange(it))
        }

        binding.lastNameTextField.onChange {
            viewModel.execute(SignupEvents.LastNameTextChange(it))
        }

        binding.phoneTextField.onChange {
            viewModel.execute(SignupEvents.PhoneNumberChange(it))
        }

        binding.emailTextField.onChange {
            viewModel.execute(SignupEvents.EmailTextChange(it))
        }

        binding.buttonSignup.setOnClickListener {
            viewModel.execute(SignupEvents.Signup)
        }

        binding.signInText.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.signupState.collect {
                if (it.loading) {
                    binding.loading(requireContext())
                }
                if(!it.loading && it.error.isNotBlank()) {
                    binding.error(requireContext())
                }
                if (it.user != defaultUser && !it.loading) {
                    binding.success(requireContext())
                    val action = SignUpFragmentDirections.actionSignUpFragmentToOtpFragment(it.user.otp)
                    findNavController().navigate(action)
                }
                binding.apply {
                    emailErrorText.handleError(it.email.errorMessage, it.email.valid)
                    firstNameErrorText.handleError(it.firstName.errorMessage, it.firstName.valid)
                    lastNameErrorText.handleError(it.lastName.errorMessage, it.lastName.valid)
                    phoneErrorText.handleError(it.phone.errorMessage, it.phone.valid)
                }
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun FragmentSignupBinding.loading(context: Context) {
    this.apply {
        signupButtonText.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        buttonSignup.isClickable = false
        signInText.isClickable = false
        buttonSignup.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button_loading)
    }

}

fun FragmentSignupBinding.success(context: Context) {
    this.apply {
        signInText.isClickable = true
        signupButtonText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        buttonSignup.isClickable = true
        buttonSignup.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)

    }

}

fun FragmentSignupBinding.error(context: Context) {
    this.apply {
        signInText.isClickable = true
        signupButtonText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        buttonSignup.isClickable = true
        buttonSignup.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
    }
}

