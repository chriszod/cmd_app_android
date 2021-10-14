package com.cmd.cmd_app_android.view.fragments.sign_in

import com.cmd.cmd_app_android.view.common.TextFieldState
import com.solid.cmd_app_android.data.models.UserDTO
import com.solid.cmd_app_android.data.models.defaultUser

data class SignInState(
    val emailState: TextFieldState = TextFieldState(),
    val passwordState: TextFieldState = TextFieldState(),
    val loading: Boolean = false,
    val error: String = "",
    val user: UserDTO = defaultUser
)