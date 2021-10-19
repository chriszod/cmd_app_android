package com.cmd.cmd_app_android.view.fragments.sign_in

sealed class SignInEvents {
    data class EmailTextChange(val value: String): SignInEvents()
    data class PasswordTextChange(val value: String): SignInEvents()
    object SignInButtonClicked: SignInEvents()
}
