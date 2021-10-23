package com.cmd.cmd_app_android.view.fragments.forgot_password

import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.utils.ErrorNotifier

data class EmailValidationState(
    val value: String = "",
    val loading: Boolean = false,
    val error: String = "",
    val valid: Boolean = true,
    val user: UserDTO = defaultUser
)