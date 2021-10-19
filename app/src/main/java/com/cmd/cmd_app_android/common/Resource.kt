package com.cmd.cmd_app_android.common

sealed class Resource<T>(
    val data: T? = null,
    val error: String? = null
) {
    class Loading<T>(): Resource<T>()
    class Error<T>(error: String? = null): Resource<T>(error = error)
    class Success<T>(data: T? = null): Resource<T>(data)
}
