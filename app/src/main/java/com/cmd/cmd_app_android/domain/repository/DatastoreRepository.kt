package com.cmd.cmd_app_android.domain.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import okio.IOException

class DatastoreRepository constructor(
    private val context: Context
) {
    private val Context.datastore by preferencesDataStore("user_datastore")
    val userPreferences = context.datastore.data
        .catch { exception ->
            Log.d("TAG", "${exception.message}: ")
            if(exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map {
            val email = it[PreferencesKeys.EMAIL] ?: ""
            val isEmailVerified = it[PreferencesKeys.IS_EMAIL_VERIFIED] ?: false
            val userId = it[PreferencesKeys.USER_ID] ?: ""
            mapOf("user_id" to userId, "email" to email, "is_email_verified" to isEmailVerified)
        }

    suspend fun saveUserState(userId: String, email: String, isEmailVerified: Boolean) {
        context.datastore.edit {
            it[PreferencesKeys.EMAIL] = email
            it[PreferencesKeys.IS_EMAIL_VERIFIED] = isEmailVerified
            it[PreferencesKeys.USER_ID] = userId
        }
    }

    private object PreferencesKeys {
        val EMAIL = stringPreferencesKey("email")
        val IS_EMAIL_VERIFIED = booleanPreferencesKey("is_email_verified")
        val USER_ID = stringPreferencesKey("user_id")
    }
}