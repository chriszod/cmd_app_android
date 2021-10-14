package com.cmd.cmd_app_android.data.repository

import com.solid.cmd_app_android.common.Resource
import com.solid.cmd_app_android.data.models.UserDTO
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun createUser(user: UserDTO): Flow<Resource<UserDTO>>

    suspend fun getUsers(): Flow<Resource<List<UserDTO>>>

    suspend fun getUserById(id: String): Flow<Resource<UserDTO>>

    suspend fun deleteUser(id: String): Flow<Resource<String>>

    suspend fun updateUser(user: UserDTO): Flow<Resource<UserDTO>>

    suspend fun getUserByEmail(email: String): Flow<Resource<UserDTO>>
}