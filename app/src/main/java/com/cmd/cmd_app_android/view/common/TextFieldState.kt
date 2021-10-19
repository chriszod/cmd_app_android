package com.cmd.cmd_app_android.view.common

data class TextFieldState(
    val value: String = "",
    val valid: Boolean = false,
    val disable: Boolean = false,
    val errorMessage: String = ""
)
