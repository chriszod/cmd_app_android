package com.cmd.cmd_app_android.domain.usecases.auth_use_cases

import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.repository.UserRepository

class UpdateUser constructor(
    private val repository: UserRepository
) {

    suspend operator fun invoke(user: UserDTO) = repository.updateUser(user)
}