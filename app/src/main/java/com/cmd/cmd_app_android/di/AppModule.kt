package com.cmd.cmd_app_android.di

import android.content.Context
import com.cmd.cmd_app_android.data.api.UserApi
import com.cmd.cmd_app_android.domain.repository.UserRepository
import com.cmd.cmd_app_android.data.utils.BASE_URL
import com.cmd.cmd_app_android.domain.repository.DatastoreRepository
import com.cmd.cmd_app_android.data.repository.UserRepositoryImpl
import com.cmd.cmd_app_android.domain.usecases.auth_use_cases.*
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton


//this is a module class that injects all the dependencies that last throughout the application
//lifecycle. this was achieved by using the singletonComponent class
//for more information on dependency injection visit https://developer.android.com/training/dependency-injection/hilt-android
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserApi(): UserApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor {
                val request = it.request()
                    .newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .build()
                it.proceed(request)
            }
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create()) //important
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(UserApi::class.java)

    }

    @Provides
    @Singleton
    fun provideRepository(userApi: UserApi): UserRepository {
        return UserRepositoryImpl(userApi)
    }

    @Provides
    @Singleton
    fun provideDatastore(@ApplicationContext context: Context): DatastoreRepository {
        return DatastoreRepository(context)
    }

    @Provides
    @Singleton
    fun provideUseCases(
        repository: UserRepository,
        datastoreRepository: DatastoreRepository
    ): UserUseCases {
        return UserUseCases(
            createUser = CreateUser(repository),
            updateUser = UpdateUser(repository),
            getUserById = GetUserById(repository),
            getUsers = GetUsers(repository),
            getUserByEmail = GetUserByEmail(repository),
            loginUser = LoginUser(repository),
            changePassword = ChangePassword(repository),
            verifyEmail = VerifyEmail(repository),
            saveUserToDatastore = SaveUserToDatastore(datastoreRepository),
            getUserInfoFromDatastore = GetUserInfoFromDatastore(datastoreRepository),
            logoutUser = LogoutUser(datastoreRepository),
            deleteUser = DeleteUser(repository)
        )
    }
}