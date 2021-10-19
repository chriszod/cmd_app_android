package com.cmd.cmd_app_android.view.fragments.otp

import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.view.common.TextFieldState

data class OtpState(
    val user: UserDTO = defaultUser,
    val loading: Boolean = false,
    val error: String = "",
    val otp: TextFieldState = TextFieldState(),
    val otpResponse: String = ""
)