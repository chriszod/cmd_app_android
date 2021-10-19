package com.cmd.cmd_app_android.domain.usecases

import com.cmd.cmd_app_android.data.repository.UserRepository

class ChangePassword constructor(
    private val repository: UserRepository
) {

    suspend operator fun invoke(userId: String, newPassword: String) = repository.changePassword(userId, newPassword)
}