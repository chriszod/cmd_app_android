package com.cmd.cmd_app_android.view.fragments.sign_in

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cmd.cmd_app_android.viewmodel.SignInUiEvents
import com.cmd.cmd_app_android.viewmodel.SignInViewModel
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.activities.MainActivity
import com.cmd.cmd_app_android.view.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.FragmentSigninBinding

@AndroidEntryPoint
class SignInFragment : Fragment(R.layout.fragment_signin) {

    private var _binding: FragmentSigninBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignInViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSigninBinding.bind(view)

        lifecycleScope.launchWhenStarted {
            viewModel.signInState.collectLatest {
                if (it.loading) {
                    binding.loading(requireContext())
                }
                if (!it.loading && it.error.isNotBlank()) {
                    binding.error(requireContext())
                }
                if (it.user != defaultUser && !it.loading) {
                    binding.success(requireContext())
                    Intent(requireContext(), MainActivity::class.java).apply {
                        flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }.also {
                        requireContext().startActivity(it)
                    }
                }
                binding.apply {
                    emailErrorText.handleError(it.emailState.errorMessage, it.emailState.valid)
                    passwordErrorText.handleError(
                        it.passwordState.errorMessage,
                        it.passwordState.valid
                    )
                }
            }
        }

        binding.emailTextField.onChange {
            viewModel.execute(SignInEvents.EmailTextChange(it))
        }

        binding.passwordTextField.onChange {
            viewModel.execute(SignInEvents.PasswordTextChange(it))
        }

        binding.buttonSignin.setOnClickListener {
            viewModel.execute(SignInEvents.SignInButtonClicked)
        }

        binding.signUpText.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                when (it) {
                    is SignInUiEvents.NoInternetConnection -> {
                        makeAlertDialog(
                            requireContext(),
                            NO_INTERNET_CONNECTION
                        ).setTitle("Internet Error").create().show()
                    }
                    is SignInUiEvents.Error -> {
                        makeAlertDialog(requireContext(), it.error).setTitle("Error").create()
                            .show()
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

fun FragmentSigninBinding.loading(context: Context) {
    this.apply {
        signupButtonText.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        buttonSignin.isClickable = false
        buttonSignin.background =
            AppCompatResources.getDrawable(context, R.drawable.background_auth_button_loading)
    }

}

fun FragmentSigninBinding.success(context: Context) {
    this.apply {
        signupButtonText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        buttonSignin.isClickable = true
        buttonSignin.background =
            AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
    }

}

fun FragmentSigninBinding.error(context: Context) {
    this.apply {
        signupButtonText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        buttonSignin.isClickable = true
        buttonSignin.background =
            AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
    }
}