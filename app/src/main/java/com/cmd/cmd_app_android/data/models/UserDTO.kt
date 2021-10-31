package com.cmd.cmd_app_android.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserDTO(
    val email: String,
    @SerializedName("fname")
    val firstName: String,
    val id: String,
    val imageUrl: String,
    val isEmailVerified: Boolean,
    @SerializedName("lname")
    val lastName: String,
    val otp: String? = "",
    val phone: String,
    val techTrack: String
): Serializable

fun UserDTO.userDTOtoRequestObject(): UserRequestObject {
    return UserRequestObject(
        email = this.email,
        firstName = this.firstName,
        imageUrl = this.imageUrl,
        isEmailVerified = this.isEmailVerified,
        lastName = lastName,
        phone = phone,
        techTrack = techTrack
    )
}

val defaultUser = UserDTO("", "", "", "", false, "", "", "","",)