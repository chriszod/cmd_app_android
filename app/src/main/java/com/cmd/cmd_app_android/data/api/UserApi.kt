package com.solid.cmd_app_android.data.api

import com.solid.cmd_app_android.data.models.UserDTO
import retrofit2.Response
import retrofit2.http.*

interface UserApi {

    @POST("users")
    suspend fun createUser(@Body user: UserDTO): Response<UserDTO>

    @GET("users")
    suspend fun getUsers(): Response<List<UserDTO>>

    @GET("users/{userId}")
    suspend fun getUserById(@Path("userId") id: String): Response<UserDTO>

    @PUT("users/{userId}")
    suspend fun updateUser(@Body user: UserDTO, @Path("userId") id: String): Response<UserDTO>

    @DELETE("users/{userId}")
    suspend fun deleteUser(@Path("userId") id: String): Response<String>

    @GET("users/byEmail/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): Response<UserDTO>
}