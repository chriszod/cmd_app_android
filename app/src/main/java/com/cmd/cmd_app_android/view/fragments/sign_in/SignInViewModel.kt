package com.cmd.cmd_app_android.view.fragments.sign_in

import android.app.Application
import androidx.lifecycle.*
import com.cmd.cmd_app_android.common.Resource
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.domain.usecases.auth_use_cases.UserUseCases
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
class SignInViewModel @Inject constructor(
    private val useCases: UserUseCases,
    application: Application
) : AndroidViewModel(application) {

    private val _signInState: MutableStateFlow<SignInState> = MutableStateFlow(SignInState())
    val signInState: StateFlow<SignInState> get() = _signInState

    private val _uiState: Channel<SignInUiEvents> = Channel()
    val uiState get() = _uiState.receiveAsFlow()

    fun execute(event: SignInEvents) {
        viewModelScope.launch {
            when (event) {
                is SignInEvents.EmailTextChange -> {
                    val validate = validateEmail(event.value)
                    _signInState.value = signInState.value.copy(
                        emailState = signInState.value.emailState.copy(
                            value = event.value.trim(),
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is SignInEvents.PasswordTextChange -> {
                    val validate = validatePassword(event.value)
                    _signInState.value = signInState.value.copy(
                        passwordState = signInState.value.passwordState.copy(
                            value = event.value,
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                SignInEvents.SignInButtonClicked -> {
                    signIn()
                }
            }
        }
    }

    private suspend fun signIn() {
        if (getApplication<Application>().checkConnectivity()) {
            val error = validateSignIn()
            if(!error) {
                useCases.loginUser(
                    signInState.value.emailState.value,
                    signInState.value.passwordState.value
                ).collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            _signInState.value = signInState.value.copy(loading = true)
                        }
                        is Resource.Error -> {
                            val message = it.error ?: "Unknown Error Occurred, Please try again later"
                            _signInState.value = signInState.value.copy(
                                loading = false,
                                error = message
                            )
                            _uiState.send(SignInUiEvents.Error(message))
                        }
                        is Resource.Success -> {
                            val user = it.data ?: defaultUser
                            _signInState.value = signInState.value.copy(
                                loading = false,
                                user = user
                            )
                            useCases.saveUserToDatastore(user.id, user.email, user.isEmailVerified)
                        }
                    }
                }
            }
        } else {
            _uiState.send(SignInUiEvents.NoInternetConnection)
        }
    }

    private fun validateSignIn(): Boolean {
        val state = signInState.value
        val email = validateEmail(state.emailState.value)
        val password = validatePassword(state.passwordState.value)
        _signInState.value = state.copy(
            emailState = state.emailState.copy(
                valid = !email.error,
                errorMessage = email.message
            ),
            passwordState = state.passwordState.copy(
                valid = !password.error,
                errorMessage = password.message
            )
        )
        return email.error || password.error
    }
}

sealed class SignInUiEvents {
    object NoInternetConnection : SignInUiEvents()
    data class Error(val error: String): SignInUiEvents()
}