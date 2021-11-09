package com.cmd.cmd_app_android.view.fragments.otp

import com.cmd.cmd_app_android.data.models.UserDTO

sealed class OtpEvents {
    data class ChangeOtpTextField(val value: String): OtpEvents()
    object VerifyOtp: OtpEvents()
    object GetOtp: OtpEvents()
    data class PostOtp(val otp: String): OtpEvents()
    data class PostUser(val user: UserDTO): OtpEvents()
}