package com.cmd.cmd_app_android.view.fragments.accounts

import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.common.TextFieldState

data class AccountState(
    val user: UserDTO = defaultUser,
    val loading: Boolean = false,
    val error: String = "jason",
    val email: TextFieldState = TextFieldState(),
    val firstName: TextFieldState = TextFieldState(),
    val lastName: TextFieldState = TextFieldState(),
    val phone: TextFieldState = TextFieldState(),
)
