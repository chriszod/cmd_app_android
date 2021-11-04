package com.cmd.cmd_app_android.data.api

import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.models.UserRequestObject
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @POST("createUser")
    suspend fun createUser(@Body user: UserRequestObject): Response<UserDTO>

    @GET("getAllUsers")
    suspend fun getUsers(): Response<List<UserDTO>>

    @GET("getUserById/{userId}")
    suspend fun getUserById(@Path("userId") id: String): Response<UserDTO>

    @PUT("updateUser/{userId}")
    suspend fun updateUser(@Body user: UserDTO, @Path("userId") id: String): Response<UserDTO>

    @DELETE("deleteUser/{userId}")
    suspend fun deleteUser(@Path("userId") id: String): Response<String>

    @GET("getUserByEmail/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): Response<UserDTO>

    @POST("loginUserByEmail/{email}")
    suspend fun loginUser(@Path("email") email: String, @Body password: String): Response<UserDTO>

    @POST("setUserPassword/{userId}")
    suspend fun changePassword(@Path("userId") userId: String, @Body newPassword: String): Response<UserDTO>

    @POST("verifyUserEmail")
    suspend fun verifyEmail(@Body email: String): Response<String>
}