package com.cmd.cmd_app_android.domain.usecases.auth_use_cases

import com.cmd.cmd_app_android.domain.repository.DatastoreRepository

class SaveUserToDatastore constructor(
    private val repository: DatastoreRepository
) {

    suspend operator fun invoke(userId: String, email: String, isEmailVerified: Boolean) = repository.saveUserState(userId, email, isEmailVerified)
}