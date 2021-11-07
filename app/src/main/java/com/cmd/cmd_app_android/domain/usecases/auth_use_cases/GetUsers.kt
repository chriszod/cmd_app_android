package com.cmd.cmd_app_android.domain.usecases.auth_use_cases

import com.cmd.cmd_app_android.domain.repository.UserRepository

class GetUsers constructor(
    private val repository: UserRepository
) {

    suspend operator fun invoke() = repository.getUsers()
}