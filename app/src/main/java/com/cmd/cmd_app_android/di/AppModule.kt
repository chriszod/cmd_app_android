package com.solid.cmd_app_android.di

import com.cmd.cmd_app_android.domain.usecase.*
import com.solid.cmd_app_android.data.api.UserApi
import com.cmd.cmd_app_android.data.repository.UserRepository
import com.solid.cmd_app_android.data.utils.BASE_URL
import com.solid.cmd_app_android.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
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
    fun provideUseCases(repository: UserRepository): UserUseCases {
        return UserUseCases(
            createUser = CreateUser(repository),
            updateUser = UpdateUser(repository),
            getUserById = GetUserById(repository),
            getUsers = GetUsers(repository),
            getUserByEmail = GetUserByEmail(repository)
        )
    }
}