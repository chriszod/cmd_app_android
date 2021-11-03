package com.cmd.cmd_app_android.view.fragments.accounts

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.activities.MainActivity
import com.cmd.cmd_app_android.view.activities.StarterActivity
import com.cmd.cmd_app_android.view.utils.gone
import com.cmd.cmd_app_android.view.utils.makeAlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.FragmentAccountBinding

@AndroidEntryPoint
class AccountFragment : Fragment(R.layout.fragment_account) {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AccountsViewModel>()
    private lateinit var loadingDialog: AlertDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentAccountBinding.bind(view)

        viewModel.execute(AccountEvents.GetUser)

        loadingDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.layout_loading)
            .setCancelable(false)
            .create()

        binding.profileSettings.setOnClickListener {
            val action = AccountFragmentDirections.actionNavigationAccountToProfileSettingsFragment(viewModel.state.value.user)
            findNavController().navigate(action)
        }

        binding.logOut.setOnClickListener {
            makeDialog("Logout", "Are you sure you want to log out?") {
                viewModel.execute(AccountEvents.Logout)
            }.show()
        }

        binding.deleteAccount.setOnClickListener {
            makeDialog(
                "Delete Account",
                "Are you sure you want to delete your account? this action cannot be undone"
            ) {
                viewModel.execute(AccountEvents.DeleteUser)
            }.show()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collectLatest {
                if (it.loading) {
                    loadingDialog.show()
                }
                if (!it.loading && it.user != defaultUser) {
                    binding.success(requireContext(), it.user)
                    loadingDialog.dismiss()
                }
                if (!it.loading && it.error.isNotBlank()) {
                    loadingDialog.dismiss()
                    Snackbar.make(view, it.error, BaseTransientBottomBar.LENGTH_LONG).apply {
                        setAction("Try Again") {
                            viewModel.execute(AccountEvents.GetUser)
                        }
                    }.show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.event.collectLatest {
                when (it) {
                    UiEvent.DeletedSuccessfully -> {
                        Intent(requireContext(), StarterActivity::class.java).apply {
                            putExtra("is_from_main_activity", true)
                            flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }.also { i ->
                            requireContext().startActivity(i)
                        }
                    }
                    UiEvent.LoggedOutError -> {
                        Snackbar.make(
                            view,
                            "An Unknown Error Occurred",
                            BaseTransientBottomBar.LENGTH_LONG
                        ).apply {
                            setAction("Try Again") {
                                viewModel.execute(AccountEvents.Logout)
                            }
                        }.show()
                    }
                    UiEvent.LoggedOutSuccessfully -> {
                        Intent(requireContext(), StarterActivity::class.java).apply {
                            putExtra("is_from_main_activity", true)
                            flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }.also { i ->
                            requireContext().startActivity(i)
                        }
                    }
                }
            }
        }
    }

    private fun makeDialog(title: String, message: String, action: () -> Unit): AlertDialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                "Ok"
            ) { _, _ -> action() }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun FragmentAccountBinding.success(context: Context, user: UserDTO) {
    this.apply {
        textName.text =
            StringBuilder().append(user.firstName).append(" ").append(user.lastName).toString()
        Glide.with(context)
            .load(user.imageUrl)
            .centerCrop()
            .into(userImage)
    }
}