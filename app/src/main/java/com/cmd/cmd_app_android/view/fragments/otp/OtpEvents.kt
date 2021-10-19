package com.cmd.cmd_app_android.view.fragments.otp

sealed class OtpEvents {
    data class ChangeOtpTextField(val value: String): OtpEvents()
    object VerifyOtp: OtpEvents()
    object GetOtp: OtpEvents()
    data class PostOtp(val otp: String): OtpEvents()
    object GetUserByID: OtpEvents()
}