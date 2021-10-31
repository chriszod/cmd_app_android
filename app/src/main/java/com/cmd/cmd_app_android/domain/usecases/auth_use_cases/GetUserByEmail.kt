package com.cmd.cmd_app_android.domain.usecases.auth_use_cases

import com.cmd.cmd_app_android.data.repository.UserRepository

class GetUserByEmail constructor(
    private val repository: UserRepository
) {

    suspend operator fun invoke(email: String) = repository.getUserByEmail(email)
}