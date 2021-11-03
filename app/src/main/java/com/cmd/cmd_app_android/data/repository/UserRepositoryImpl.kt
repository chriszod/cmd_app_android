package com.cmd.cmd_app_android.data.repository

import android.util.Log
import com.cmd.cmd_app_android.common.Resource
import com.cmd.cmd_app_android.data.api.UserApi
import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.models.userDTOtoRequestObject
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

class UserRepositoryImpl constructor(
    private val api: UserApi
) : UserRepository {

    override suspend fun verifyEmail(email: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.verifyEmail(email)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success<String>(it))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error<String>(e.message))
        }
    }

    override suspend fun loginUser(email: String, password: String): Flow<Resource<UserDTO>> =
        flow {
            emit(Resource.Loading())
            try {
                val response = api.loginUser(email, password)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(Resource.Success<UserDTO>(it))
                    }
                } else {
                    emit(Resource.Error<UserDTO>("Wrong credentials"))
                }
            } catch (e: Exception) {
                Log.d("TAG", "loginUser: ${e.message}")
                emit(Resource.Error<UserDTO>(e.message))
            }
        }

    override suspend fun changePassword(
        userId: String,
        newPassword: String
    ): Flow<Resource<UserDTO>> =
        flow {
            emit(Resource.Loading())
            try {
                val response = api.changePassword(userId, newPassword)
                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(Resource.Success<UserDTO>(it))
                    }
                }
            } catch (e: Exception) {
                emit(Resource.Error<UserDTO>(e.message))
            }
        }

    override suspend fun createUser(user: UserDTO): Flow<Resource<UserDTO>> = flow {
        emit(Resource.Loading())
        try {
            val userRequestObject = user.userDTOtoRequestObject()
            val response = api.createUser(userRequestObject)
            if (response.code() == 422) {
                Log.d("TAG", "createUser: ${response.code()}")
                response.body()?.let {
                    emit(Resource.Success<UserDTO>(it))
                }
            }
            if (response.isSuccessful) {
                response.body()?.let {
                    if(it.id == null) {
                        emit(Resource.Error<UserDTO>("User Already Exists"))
                    } else {
                        emit(Resource.Success<UserDTO>(it))
                    }
                }
            } else if (response.code() == 422) {
                Log.d("TAG", "createUser: $response")
                response.body()?.let {
                    emit(Resource.Success<UserDTO>(it))
                }
            } else {
                emit(Resource.Error<UserDTO>(response.message()))
            }
        } catch (e: Exception) {
            Log.d("TAG", "createUser: ${e.message}")
            emit(Resource.Error<UserDTO>(e.message))
        }
    }

    override suspend fun getUsers(): Flow<Resource<List<UserDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getUsers()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success<List<UserDTO>>(it))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error<List<UserDTO>>(e.message))
        }
    }

    override suspend fun getUserById(id: String): Flow<Resource<UserDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getUserById(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success<UserDTO>(it))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error<UserDTO>(e.message))
        }
    }

    override suspend fun deleteUser(id: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.deleteUser(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success<String>(it))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error<String>(e.message))
        }
    }

    override suspend fun updateUser(user: UserDTO): Flow<Resource<UserDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.updateUser(user, user.id!!)
            if (response.isSuccessful) {
                response.body()?.let {
                    Log.d("TAG", "updateUser: $it")
                    emit(Resource.Success<UserDTO>(it))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error<UserDTO>(e.message))
        }
    }

    override suspend fun getUserByEmail(email: String): Flow<Resource<UserDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getUserByEmail(email)
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success<UserDTO>(it))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error<UserDTO>(e.message))
            Log.d("TAG", "getUserByEmail: ${e.message}")
        }
    }

}