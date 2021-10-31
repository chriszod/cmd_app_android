package com.cmd.cmd_app_android.view.fragments.accounts

sealed class AccountEvents {
    object Logout: AccountEvents()
    object UpdateUser: AccountEvents()
    object DeleteUser: AccountEvents()
    object GetUser: AccountEvents()
    data class FirstNameTextChange(val value: String): AccountEvents()
    data class LastNameTextChange(val value: String): AccountEvents()
    data class PhoneNumberChange(val value: String): AccountEvents()
    data class EmailTextChange(val value: String): AccountEvents()
}
