package com.cmd.cmd_app_android.domain.usecases.auth_use_cases

import com.cmd.cmd_app_android.domain.repository.UserRepository

class GetUserById constructor(
    private val repository: UserRepository
) {

    suspend operator fun invoke(id: String) = repository.getUserById(id)
}