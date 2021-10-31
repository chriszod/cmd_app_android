package com.cmd.cmd_app_android.domain.usecases.auth_use_cases

import com.cmd.cmd_app_android.data.repository.UserRepository

class VerifyEmail constructor(
    private val repository: UserRepository
) {

    suspend operator fun invoke(email: String) = repository.verifyEmail(email)
}