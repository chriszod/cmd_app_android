package com.cmd.cmd_app_android.domain.usecases.auth_use_cases

import com.cmd.cmd_app_android.common.Resource
import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class CreateUser constructor(
    private val repository: UserRepository
) {

    suspend operator fun invoke(user: UserDTO): Flow<Resource<UserDTO>> = repository.createUser(user)
}