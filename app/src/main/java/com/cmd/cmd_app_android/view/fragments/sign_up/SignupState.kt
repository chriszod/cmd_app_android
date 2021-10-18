package com.cmd.cmd_app_android.view.fragments.sign_up

import com.cmd.cmd_app_android.view.common.TextFieldState
import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.models.defaultUser

data class SignupState(
    val email: TextFieldState = TextFieldState(),
    val firstName: TextFieldState = TextFieldState(),
    val lastName: TextFieldState = TextFieldState(),
    val phone: TextFieldState = TextFieldState(),
    val loading: Boolean = false,
    val error: String = "",
    val user: UserDTO = defaultUser,
    val change: Boolean = false
)