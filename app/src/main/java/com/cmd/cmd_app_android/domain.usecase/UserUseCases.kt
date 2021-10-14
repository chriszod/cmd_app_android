package com.cmd.cmd_app_android.domain.usecase


//for implementing user use cases for better implementation
data class UserUseCases(
    val createUser: CreateUser,
    val updateUser: UpdateUser,
    val getUserById: GetUserById,
    val getUsers: GetUsers,
    val getUserByEmail: GetUserByEmail
)
