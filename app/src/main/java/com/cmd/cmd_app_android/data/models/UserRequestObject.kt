package com.cmd.cmd_app_android.data.models

import com.google.gson.annotations.SerializedName

data class UserRequestObject (
    val email: String,
    @SerializedName("fname")
    val firstName: String,
    val imageUrl: String,
    val isEmailVerified: Boolean,
    @SerializedName("lname")
    val lastName: String,
    val phone: String,
    val techTrack: String
)