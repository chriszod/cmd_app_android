package com.cmd.cmd_app_android.view.fragments.forgot_password

sealed class EmailValidationEvents {
    data class EmailTextChange(val value: String): EmailValidationEvents()
    object ResetPassword: EmailValidationEvents()
}