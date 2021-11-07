package com.cmd.cmd_app_android.domain.usecases.auth_use_cases

import com.cmd.cmd_app_android.domain.repository.UserRepository

class LoginUser constructor(
    private val repository: UserRepository
) {

    suspend operator fun invoke(email: String, password: String) = repository.loginUser(email, password)
}