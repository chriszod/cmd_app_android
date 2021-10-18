package com.cmd.cmd_app_android.view.fragments.verification

sealed class VerificationEvents {
    data class ChangeOtpTextField(val value: String): VerificationEvents()
    object VerifyOtp: VerificationEvents()
    data class ChangePasswordTextField(val value: String): VerificationEvents()
    data class ChangeConfirmPasswordTextField(val value: String): VerificationEvents()
    object Continue : VerificationEvents()
    object GetOtp: VerificationEvents()
    data class PostOtp(val otp: String): VerificationEvents()
}