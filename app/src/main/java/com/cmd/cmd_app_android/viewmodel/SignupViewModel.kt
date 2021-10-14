package com.cmd.cmd_app_android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmd.cmd_app_android.view.fragments.sign_up.SignupEvents
import com.cmd.cmd_app_android.view.fragments.sign_up.SignupState
import com.solid.cmd_app_android.common.Resource
import com.solid.cmd_app_android.data.models.UserDTO
import com.solid.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.domain.usecase.UserUseCases
import com.cmd.cmd_app_android.ui.utils.*
import com.cmd.cmd_app_android.view.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    application: Application,
    private val useCases: UserUseCases
): AndroidViewModel(application) {

    private val _signupState = MutableStateFlow(SignupState())
    val signupState: StateFlow<SignupState> get() = _signupState

    private val _uiEvents: Channel<SignupUiEvents> = Channel()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun execute(event: SignupEvents) {
        viewModelScope.launch {
            when(event) {
                is SignupEvents.EmailTextChange -> {
                    val validate = validateEmail(event.value)
                    _signupState.value = signupState.value.copy(
                        email = signupState.value.email.copy(
                            value = event.value,
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is SignupEvents.FirstNameTextChange -> {
                    val validate = validateName(event.value)
                    _signupState.value = signupState.value.copy(
                        firstName = signupState.value.firstName.copy(
                            value = event.value,
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is SignupEvents.LastNameTextChange -> {
                    val validate = validateName(event.value)
                    _signupState.value = signupState.value.copy(
                        lastName = signupState.value.lastName.copy(
                            value = event.value,
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is SignupEvents.PhoneNumberChange -> {
                    val validate = validatePhone(event.value)
                    _signupState.value = signupState.value.copy(
                        phone = signupState.value.phone.copy(
                            value = event.value,
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is SignupEvents.Signup -> {
                    signup()
                }
            }
        }
    }
    private suspend fun signup() {
        if (getApplication<Application>().checkConnectivity()) {
            val validate = validateSignup(
                signupState.value.firstName.value,
                signupState.value.lastName.value,
                signupState.value.phone.value,
                signupState.value.email.value,
            )
            if(validate.error) {
                _signupState.value = signupState.value.copy(
                    error = validate.message,
                    loading = false
                )
            } else {
                val user = UserDTO(
                    email = signupState.value.email.value,
                    firstName = signupState.value.firstName.value,
                    lastName = signupState.value.lastName.value,
                    phone = signupState.value.phone.value,
                )
                useCases.createUser(user).collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            _signupState.value = signupState.value.copy(loading = true)
                        }
                        is Resource.Error -> {
                            _signupState.value = signupState.value.copy(
                                loading = false,
                                error = it.error ?: "Unknown Error Occurred"
                            )
                        }
                        is Resource.Success -> {
                            _signupState.value = signupState.value.copy(
                                loading = false,
                                user = it.data ?: defaultUser
                            )
                        }
                    }
                }
            }
        } else {
            _uiEvents.send(SignupUiEvents.NoInternetConnection)
        }
    }
}

sealed class SignupUiEvents {
    object NoInternetConnection : SignupUiEvents()
}