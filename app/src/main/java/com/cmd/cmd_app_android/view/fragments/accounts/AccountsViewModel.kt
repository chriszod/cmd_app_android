package com.cmd.cmd_app_android.view.fragments.accounts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmd.cmd_app_android.common.Resource
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.domain.usecases.auth_use_cases.UserUseCases
import com.cmd.cmd_app_android.view.utils.validateEmail
import com.cmd.cmd_app_android.view.utils.validateName
import com.cmd.cmd_app_android.view.utils.validatePhone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class AccountsViewModel @Inject constructor(
    private val useCases: UserUseCases
): ViewModel() {

    private val _state = MutableStateFlow(AccountState())
    val state get() = _state.asStateFlow()

    private val _profileState = MutableStateFlow(AccountState())
    val profileState get() = _profileState.asStateFlow()

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    private var isFirstLaunch = true

    init {
        if(isFirstLaunch) {
            execute(AccountEvents.GetUser)
            isFirstLaunch = false
        }
    }

    fun execute(event: AccountEvents) {
        viewModelScope.launch {
            when(event) {
                is AccountEvents.UpdateUser -> {
                    update()
                }
                AccountEvents.DeleteUser -> {
                    delete()
                }
                AccountEvents.Logout -> {
                    logout()
                }
                is AccountEvents.GetUser -> {
                    getUser()
                }
                is AccountEvents.FirstNameTextChange -> {
                    val validate = validateName(event.value)
                    _state.value = state.value.copy(
                        firstName = _state.value.firstName.copy(
                            value = event.value.trim(),
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is AccountEvents.LastNameTextChange -> {
                    val validate = validateName(event.value)
                    _state.value = state.value.copy(
                        lastName = state.value.lastName.copy(
                            value = event.value.trim(),
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is AccountEvents.PhoneNumberChange -> {
                    val validate = validatePhone(event.value)
                    _state.value = state.value.copy(
                        phone = state.value.phone.copy(
                            value = event.value.trim(),
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
            }
        }
    }

    private suspend fun delete() {
        useCases.deleteUser(state.value.user.id!!).collectLatest {
            when (it) {
                is Resource.Loading -> {
                    _state.value = state.value.copy(
                        loading = true
                    )
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        error = it.error ?: "Unknown Error Occurred",
                        loading = false
                    )
                }
                is Resource.Success -> {
                    _state.value = state.value.copy(
                        error = "",
                        loading = false,
                        user = defaultUser
                    )
                    _event.emit(UiEvent.DeletedSuccessfully)
                }
            }
        }
    }

    private suspend fun update() {
        Log.d("TAG", "update: ${state.value.user.toString()}")
        val data = state.value
        useCases.updateUser(
            data.user.copy(
                email = data.email.value,
                firstName = data.firstName.value,
                lastName = data.lastName.value,
                phone = data.phone.value,
            )
        ).collectLatest {
            when (it) {
                is Resource.Loading -> {
                    _profileState.value = profileState.value.copy(
                        loading = true
                    )
                }
                is Resource.Error -> {
                    Log.d("TAG", "update: ${it.error}")
                    _profileState.value = profileState.value.copy(
                        error = it.error ?: "Unknown Error Occurred",
                        loading = false
                    )
                }
                is Resource.Success -> {
                    _profileState.value = profileState.value.copy(
                        error = "",
                        loading = false,
                        user = it.data ?: defaultUser
                    )
                    _state.value = state.value.copy(
                        error = "",
                        loading = false,
                        user = it.data ?: defaultUser
                    )
                    _event.emit(UiEvent.UpdatedSuccessfully)
                }
            }
        }
    }

    private suspend fun logout() {
        val loggedOut = useCases.logoutUser()
        if(loggedOut) {
            _state.value = _state.value.copy(
                error = "",
                loading = false,
                user = defaultUser
            )
            _event.emit(UiEvent.LoggedOutSuccessfully)
        } else {
            _event.emit(UiEvent.LoggedOutError)
        }
    }

    private suspend fun getUser() {
        val data = useCases.getUserInfoFromDatastore().first()
        val userId = data["user_id"] as String
        useCases.getUserById(userId).collectLatest {
            when(it) {
                is Resource.Loading -> {
                    _state.value = state.value.copy(
                        loading = true
                    )
                }
                is Resource.Error -> {
                    _state.value = state.value.copy(
                        error = it.error ?: "Unknown Error Occurred",
                        loading = false
                    )
                }
                is Resource.Success -> {
                    val value = state.value
                    _state.value = value.copy(
                        error = "",
                        loading = false,
                        user = it.data ?: defaultUser,
                        email = value.email.copy(value = it.data?.email ?: ""),
                        firstName = value.firstName.copy(value = it.data?.firstName ?: ""),
                        lastName = value.email.copy(value = it.data?.lastName ?: ""),
                        phone = value.email.copy(value = it.data?.phone ?: ""),
                    )
                }
            }
        }
    }

}

sealed class UiEvent {
    object LoggedOutSuccessfully: UiEvent()
    object LoggedOutError: UiEvent()
    object UpdatedSuccessfully: UiEvent()
    object DeletedSuccessfully: UiEvent()
}