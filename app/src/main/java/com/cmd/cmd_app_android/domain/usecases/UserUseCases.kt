package com.cmd.cmd_app_android.domain.usecases


//for implementing user use cases for better implementation
data class UserUseCases(
    val createUser: CreateUser,
    val updateUser: UpdateUser,
    val getUserById: GetUserById,
    val getUsers: GetUsers,
    val getUserByEmail: GetUserByEmail,
    val loginUser: LoginUser,
    val changePassword: ChangePassword,
    val verifyEmail: VerifyEmail,
    val saveUserToDatastore: SaveUserToDatastore,
    val getUserInfoFromDatastore: GetUserInfoFromDatastore
)
