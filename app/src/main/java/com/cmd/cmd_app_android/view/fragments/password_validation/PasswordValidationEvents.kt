package com.cmd.cmd_app_android.view.fragments.password_validation

sealed class PasswordValidationEvents {
    data class ChangePasswordTextField(val value: String): PasswordValidationEvents()
    data class ChangeConfirmPasswordTextField(val value: String): PasswordValidationEvents()
    object Continue: PasswordValidationEvents()
}