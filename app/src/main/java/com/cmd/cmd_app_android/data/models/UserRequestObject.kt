package com.cmd.cmd_app_android.data.models

import com.google.gson.annotations.SerializedName

data class UserRequestObject (
    val email: String? = null,
    @SerializedName("fname")
    val firstName: String? = null,
    val imageUrl: String? = null,
    val isEmailVerified: Boolean? = null,
    @SerializedName("lname")
    val lastName: String? = null,
    val phone: String? = null,
    val techTrack: String? = null
)