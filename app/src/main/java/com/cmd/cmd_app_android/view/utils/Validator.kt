package com.cmd.cmd_app_android.view.utils

import android.util.Patterns
import java.util.regex.Pattern

private const val regexPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}\$"
private const val regexPhoneNumber = "^([0](?=.*[0-9]).{10})|([+][234](?=.*[0-9]).{12}\$)"

fun validateConfirmPassword(retypePassword: String, password: String) : ErrorNotifier {
    if(password == retypePassword) return ErrorNotifier(false, "")
    return ErrorNotifier(true, "Password does not match")
}

fun validatePhone(phone: String) : ErrorNotifier {
    if(Pattern.compile(regexPhoneNumber).matcher(phone).matches()){
        return ErrorNotifier(false, "")
    }
    return ErrorNotifier(true, "Phone number not valid")
}

fun validateName(name: String) : ErrorNotifier {
    if(name.trim().isNotBlank()) {
        return ErrorNotifier(false, "")
    }
    return ErrorNotifier(true, "Please Input a valid name")
}

fun validateEmail(email: String) : ErrorNotifier {
    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()){
        return ErrorNotifier(false, "")
    }
    return ErrorNotifier(true, "Please Input a correct email")
}

fun validatePassword(password: String): ErrorNotifier {
    if (Pattern.compile(regexPassword).matcher(password).matches()){
        return ErrorNotifier(false, "")
    }
    return ErrorNotifier(true, "Password must have a minimum of 8 digits, contain an uppercase and a number")
}

fun errorStringFormatter(errors: List<String>) : String {
    val stringBuilder = StringBuilder()
    errors.forEach {
        stringBuilder.append("● $it\n")
    }
    return stringBuilder.toString()
}

data class ErrorNotifier(
    val error: Boolean,
    val message: String
)