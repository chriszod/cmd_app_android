package com.cmd.cmd_app_android.domain.usecases.auth_use_cases

import com.cmd.cmd_app_android.domain.repository.DatastoreRepository

class GetUserInfoFromDatastore constructor(
    private val repository: DatastoreRepository
) {

    suspend operator fun invoke() = repository.userPreferences
}