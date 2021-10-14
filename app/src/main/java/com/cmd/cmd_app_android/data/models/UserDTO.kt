package com.solid.cmd_app_android.data.models

import com.google.gson.annotations.SerializedName

data class UserDTO(
    val email: String,
    @SerializedName("fname")
    val firstName: String,
    val id: String? = null,
    @SerializedName("lname")
    val lastName: String,
    val password: String? = null,
    val phone: String
)

val defaultUser = UserDTO("", "", "", "", "", "")