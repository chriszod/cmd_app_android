package com.cmd.cmd_app_android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmd.cmd_app_android.view.fragments.sign_up.SignupEvents
import com.cmd.cmd_app_android.view.fragments.sign_up.SignupState
import com.cmd.cmd_app_android.common.Resource
import com.cmd.cmd_app_android.data.models.UserDTO
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.domain.usecases.UserUseCases
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
    val uiEvents get() = _uiEvents.receiveAsFlow()

    fun execute(event: SignupEvents) {
        viewModelScope.launch {
            _signupState.value = signupState.value.copy(
                change = !signupState.value.change
            )
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
            val error = validateSignup()
            if(!error) {
                val user = defaultUser.copy(
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
                            val message = it.error ?: "Unknown Error Occurred, Please try again later"
                            _signupState.value = signupState.value.copy(
                                loading = false,
                                error = message
                            )
                            _uiEvents.send(SignupUiEvents.Error(message))
                        }
                        is Resource.Success -> {
                            val userDto = it.data ?: defaultUser
                            _signupState.value = signupState.value.copy(
                                loading = false,
                                user = userDto
                            )
                            _uiEvents.send(SignupUiEvents.Success(userDto))
                            useCases.saveUserToDatastore(userDto.id, userDto.email, userDto.isEmailVerified)
                        }
                    }
                }
            }
        } else {
            _uiEvents.send(SignupUiEvents.NoInternetConnection)
        }
    }

    private fun validateSignup(): Boolean {
        val state = signupState.value
        val email = validateEmail(state.email.value)
        val phone = validatePhone(state.phone.value)
        val firstName = validateName(state.firstName.value)
        val lastName = validateName(state.lastName.value)
        _signupState.value = state.copy(
            email = state.email.copy(
                valid = !email.error,
                errorMessage = email.message
            ),
            phone = state.phone.copy(
                valid = !phone.error,
                errorMessage = phone.message
            ),
            firstName = state.firstName.copy(
                valid = !firstName.error,
                errorMessage = firstName.message
            ),
            lastName = state.lastName.copy(
                valid = !lastName.error,
                errorMessage = lastName.message
            )
        )
        return email.error || phone.error || firstName.error || lastName.error
    }
}

sealed class SignupUiEvents {
    object NoInternetConnection : SignupUiEvents()
    data class Error(val error: String): SignupUiEvents()
    data class Success(val user: UserDTO): SignupUiEvents()
}