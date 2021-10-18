package com.cmd.cmd_app_android.view.fragments.verification

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.activities.MainActivity
import com.cmd.cmd_app_android.view.utils.handleError
import com.cmd.cmd_app_android.view.utils.navigateActivity
import com.cmd.cmd_app_android.view.utils.onChange
import com.cmd.cmd_app_android.viewmodel.VerificationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.FragmentPasswordBinding
import thecmdteam.cmd_app_android.databinding.FragmentSigninBinding

@AndroidEntryPoint
class PasswordFragment : Fragment(R.layout.fragment_password) {

    private var _binding: FragmentPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VerificationViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentPasswordBinding.bind(view)
        binding.passwordTextField.onChange {
            viewModel.execute(VerificationEvents.ChangePasswordTextField(it))
        }
        binding.confirmNewPasswordTextField.onChange {
            viewModel.execute(VerificationEvents.ChangeConfirmPasswordTextField(it))
        }

        binding.buttonContinue.setOnClickListener {
            viewModel.execute(VerificationEvents.Continue)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.verificationState.collect {
                if (it.loading) {
                    binding.loading(requireContext())
                }
                if(it.user != defaultUser && !it.loading && it.changedSuccessfully){
                    binding.success(requireContext())
                    navigateActivity(requireContext(), MainActivity())
                }
                if(it.error.isNotBlank() && !it.loading){
                    binding.error(requireContext())
                }
                binding.apply {
                    newPasswordError.handleError(it.password.errorMessage, it.password.valid)
                    confirmNewPasswordError.handleError(
                        it.confirmPassword.errorMessage,
                        it.confirmPassword.valid
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun FragmentPasswordBinding.loading(context: Context) {
    this.apply {
        continueButtonText.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        buttonContinue.isClickable = false
        buttonContinue.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button_loading)
    }

}

fun FragmentPasswordBinding.success(context: Context) {
    this.apply {
        continueButtonText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        buttonContinue.isClickable = true
        buttonContinue.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
    }

}

fun FragmentPasswordBinding.error(context: Context) {
    this.apply {
        continueButtonText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        buttonContinue.isClickable = true
        buttonContinue.background = AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
    }
}