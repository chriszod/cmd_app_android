package com.cmd.cmd_app_android.view.fragments.sign_up

sealed class SignupEvents {

    data class FirstNameTextChange(val value: String): SignupEvents()
    data class LastNameTextChange(val value: String): SignupEvents()
    data class PhoneNumberChange(val value: String): SignupEvents()
    data class EmailTextChange(val value: String): SignupEvents()
    object Signup: SignupEvents()
}