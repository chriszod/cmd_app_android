package com.cmd.cmd_app_android.view.fragments.sign_in

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cmd.cmd_app_android.view.utils.handleError
import com.cmd.cmd_app_android.view.utils.onChange
import com.cmd.cmd_app_android.viewmodel.SignInUiEvents
import com.cmd.cmd_app_android.viewmodel.SignInViewModel
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.solid.cmd_app_android.data.models.defaultUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.FragmentSigninBinding

@AndroidEntryPoint
class SignInFragment: Fragment(R.layout.fragment_signin) {

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
                if(!it.loading && it.error.isNotBlank()) {
                    binding.error(requireContext(), view, it.error)
                }
                if (it.user != defaultUser && !it.loading) {
                    Log.d("TAG", "onViewCreated: ${it.user}")
                    Snackbar.make(view, it.user.firstName, LENGTH_LONG).show()
                }
                binding.apply {
                    emailErrorText.handleError(it.emailState.errorMessage, it.emailState.valid)
                    passwordErrorText.handleError(it.passwordState.errorMessage, it.passwordState.valid)
                }
            }
        }
        
        binding.emailTextField.onChange { 
            viewModel.execute(SignInEvents.EmailTextChange(it))
        }

        binding.passwordTextField.onChange {
            viewModel.execute(SignInEvents.PasswordTextChange(it))
            Log.d("TAG", "onViewCreated: $it")
        }

        binding.buttonSignin.setOnClickListener {
            viewModel.execute(SignInEvents.SignInButtonClicked)
        }

        binding.signUpText.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_signUpFragment)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                when(it) {
                    is SignInUiEvents.NoInternetConnection -> {
                        Snackbar.make(view, "No Internet Connection", LENGTH_LONG).show()
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
        buttonSignin.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button_loading)
    }

}

fun FragmentSigninBinding.success(context: Context) {
    this.apply {
        signupButtonText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        buttonSignin.isClickable = true
        buttonSignin.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
    }

}

fun FragmentSigninBinding.error(context: Context, view: View, error: String) {
    this.apply {
        signupButtonText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        buttonSignin.isClickable = true
        buttonSignin.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
    }
    Snackbar.make(view, error, LENGTH_LONG).show()
}