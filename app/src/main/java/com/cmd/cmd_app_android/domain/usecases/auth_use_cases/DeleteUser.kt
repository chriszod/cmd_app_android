package com.cmd.cmd_app_android.domain.usecases.auth_use_cases

import com.cmd.cmd_app_android.data.repository.UserRepository

class DeleteUser constructor(
    private val repository: UserRepository
) {

    suspend operator fun invoke(id: String) = repository.deleteUser(id)
}