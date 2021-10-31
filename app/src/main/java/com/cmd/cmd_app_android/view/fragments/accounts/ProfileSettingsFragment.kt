package com.cmd.cmd_app_android.view.fragments.accounts

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.utils.onChange
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.FragmentProfileSettingsBinding

@AndroidEntryPoint
class ProfileSettingsFragment: BottomSheetDialogFragment() {

    private var _binding: FragmentProfileSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressDialog: MaterialAlertDialogBuilder
    
    private val viewModel by viewModels<AccountsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentProfileSettingsBinding.bind(view)

        progressDialog = MaterialAlertDialogBuilder(requireContext())
            .setCancelable(false)
            .setView(R.layout.layout_loading)

        binding.firstNameTextField.onChange {
            viewModel.execute(AccountEvents.FirstNameTextChange(it))
        }

        binding.lastNameTextField.onChange {
            viewModel.execute(AccountEvents.LastNameTextChange(it))
        }

        binding.phoneTextField.onChange {
            viewModel.execute(AccountEvents.PhoneNumberChange(it))
        }

        binding.emailTextField.onChange {
            viewModel.execute(AccountEvents.EmailTextChange(it))
        }

        binding.buttonUpdate.setOnClickListener {
            viewModel.execute(AccountEvents.UpdateUser)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collectLatest {
                binding.success(requireContext(), it.user)
            }
        }
    }
}

fun FragmentProfileSettingsBinding.loading(context: Context) {
    this.apply {
        updateButtonText.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        buttonChangeEmail.isClickable = false
        buttonUpdate.isClickable = false
        buttonUpdate.background =
            AppCompatResources.getDrawable(context, R.drawable.background_auth_button_loading)
    }

}

fun FragmentProfileSettingsBinding.success(context: Context, user: UserDTO) {
    this.apply {
        updateButtonText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        buttonUpdate.isClickable = true
        buttonChangeEmail.isClickable = true
        buttonUpdate.background =
            AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
        emailTextField.setText(user.email)
        phoneTextField.setText(user.phone)
        firstNameTextField.setText(user.firstName)
        lastNameTextField.setText(user.lastName)
    }

}

fun FragmentProfileSettingsBinding.error(context: Context) {
    this.apply {
        updateButtonText.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        buttonUpdate.isClickable = true
        buttonChangeEmail.isClickable = true
        buttonUpdate.background =
            AppCompatResources.getDrawable(context, R.drawable.background_auth_button)
    }
}