package com.cmd.cmd_app_android.domain.repository

import com.cmd.cmd_app_android.common.Resource
import com.cmd.cmd_app_android.data.models.UserDTO
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body

interface UserRepository {

    suspend fun createUser(user: UserDTO): Flow<Resource<UserDTO>>

    suspend fun getUsers(): Flow<Resource<List<UserDTO>>>

    suspend fun getUserById(id: String): Flow<Resource<UserDTO>>

    suspend fun deleteUser(id: String): Flow<Resource<String>>

    suspend fun updateUser(user: UserDTO): Flow<Resource<UserDTO>>

    suspend fun getUserByEmail(email: String): Flow<Resource<UserDTO>>

    suspend fun loginUser(email: String, password: String): Flow<Resource<UserDTO>>

    suspend fun changePassword(userId: String, newPassword: String): Flow<Resource<UserDTO>>

    suspend fun verifyEmail(email: String): Flow<Resource<String>>
}