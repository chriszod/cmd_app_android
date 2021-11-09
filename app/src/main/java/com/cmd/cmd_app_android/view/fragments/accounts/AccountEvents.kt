package com.cmd.cmd_app_android.view.fragments.accounts

import com.cmd.cmd_app_android.data.models.UserDTO

sealed class AccountEvents {
    object Logout: AccountEvents()
    object UpdateUser: AccountEvents()
    object DeleteUser: AccountEvents()
    object GetUser: AccountEvents()
    data class FirstNameTextChange(val value: String): AccountEvents()
    data class LastNameTextChange(val value: String): AccountEvents()
    data class PhoneNumberChange(val value: String): AccountEvents()
    data class PostUser(val user: UserDTO) : AccountEvents()
}
