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
import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.activities.MainActivity
import com.cmd.cmd_app_android.view.activities.StarterActivity
import com.cmd.cmd_app_android.view.utils.gone
import com.cmd.cmd_app_android.view.utils.makeAlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import thecmdteam.cmd_app_android.R
import thecmdteam.cmd_app_android.databinding.FragmentAccountBinding

@AndroidEntryPoint
class AccountFragment: Fragment(R.layout.fragment_account) {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<AccountsViewModel>()
    private lateinit var alertDialog: AlertDialog
    private lateinit var loadingDialog: AlertDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentAccountBinding.bind(view)
        alertDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Logout")
            .setMessage("Do you wish to log out?")
            .setPositiveButton("Ok"
            ) { dialog, p1 -> viewModel.execute(AccountEvents.Logout) }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()

        loadingDialog = MaterialAlertDialogBuilder(requireContext())
            .setView(R.layout.layout_loading)
            .setCancelable(false)
            .create()

        binding.profileSettings.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_account_to_profileSettingsFragment)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collectLatest {
                if(it.loading) {
                    loadingDialog.show()
                }
                if (!it.loading && it.error.isBlank()) {
                    loadingDialog.dismiss()
                }
                if (!it.loading && it.error.isNotBlank()){
                    loadingDialog.dismiss()
                    Toast.makeText(requireContext(), it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun FragmentAccountBinding.loading(context: Context) {
    this.apply {

    }

}

fun FragmentAccountBinding.success(context: Context, user: UserDTO) {
    this.apply {
        textName.text = StringBuilder().append(user.firstName).append(" ").append(user.lastName).toString()
    }

}

fun FragmentAccountBinding.error(context: Context, error: String) {
    this.apply {
        makeAlertDialog(context, "An Unknown Error Occurred, Unable to load data, $error")
    }
}