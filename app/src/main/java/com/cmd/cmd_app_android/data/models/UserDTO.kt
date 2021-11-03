package com.cmd.cmd_app_android.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserDTO(
    val email: String? = null,
    @SerializedName("fname")
    val firstName: String? = null,
    val id: String? = null,
    val imageUrl: String? = null,
    val isEmailVerified: Boolean? = null,
    @SerializedName("lname")
    val lastName: String? = null,
    val otp: String? = null,
    val phone: String? = null,
    val techTrack: String? = null
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