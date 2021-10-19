package com.cmd.cmd_app_android.view.fragments.password_validation

import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.common.TextFieldState

data class PasswordValidationState(
    val user: UserDTO = defaultUser,
    val loading: Boolean = false,
    val error: String = "",
    val password: TextFieldState = TextFieldState(),
    val confirmPassword: TextFieldState = TextFieldState(),
)