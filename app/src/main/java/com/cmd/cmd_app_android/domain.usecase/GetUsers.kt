package com.cmd.cmd_app_android.domain.usecase

import com.cmd.cmd_app_android.data.repository.UserRepository

class GetUsers constructor(
    private val repository: UserRepository
) {

    suspend operator fun invoke() = repository.getUsers()
}