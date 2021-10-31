package com.cmd.cmd_app_android.domain.usecases.auth_use_cases

import android.util.Log
import com.cmd.cmd_app_android.common.Resource
import com.cmd.cmd_app_android.data.repository.UserRepository
import com.cmd.cmd_app_android.domain.repository.DatastoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

class LogoutUser constructor(
    private val datastoreRepository: DatastoreRepository,
) {

    suspend operator fun invoke(): Boolean {
        return try {
            datastoreRepository.saveUserState(
                userId = "",
                email = "",
                isEmailVerified = false
            )
            true
        }catch (e: Exception) {
            Log.d("TAG", "invoke: ${e.message}")
            false
        }
    }
}