package com.cmd.cmd_app_android.view.fragments.accounts

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

    private val _event = MutableSharedFlow<UiEvent>()
    val event = _event.asSharedFlow()

    init {
        execute(AccountEvents.GetUser)
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
                is AccountEvents.EmailTextChange -> {
                    val validate = validateEmail(event.value)
                    _state.value = _state.value.copy(
                        email = _state.value.email.copy(
                            value = event.value.trim(),
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is AccountEvents.FirstNameTextChange -> {
                    val validate = validateName(event.value)
                    _state.value = _state.value.copy(
                        firstName = _state.value.firstName.copy(
                            value = event.value.trim(),
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is AccountEvents.LastNameTextChange -> {
                    val validate = validateName(event.value)
                    _state.value = _state.value.copy(
                        lastName = _state.value.lastName.copy(
                            value = event.value.trim(),
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is AccountEvents.PhoneNumberChange -> {
                    val validate = validatePhone(event.value)
                    _state.value = _state.value.copy(
                        phone = _state.value.phone.copy(
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
        useCases.deleteUser(state.value.user.id).collectLatest {
            when (it) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        loading = true
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = it.error ?: "Unknown Error Occurred",
                        loading = false
                    )
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
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
        val user = _state.value
        useCases.updateUser(
            user.user.copy(
                email = user.email.value,
                firstName = user.firstName.value,
                lastName = user.lastName.value,
                phone = user.phone.value,
            )
        ).collectLatest {
            when (it) {
                is Resource.Loading -> {
                    _state.value = _state.value.copy(
                        loading = true
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = it.error ?: "Unknown Error Occurred",
                        loading = false
                    )
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
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
                    _state.value = _state.value.copy(
                        loading = true
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        error = it.error ?: "Unknown Error Occurred",
                        loading = false
                    )
                }
                is Resource.Success -> {
                    _state.value = _state.value.copy(
                        error = "",
                        loading = false,
                        user = it.data ?: defaultUser
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