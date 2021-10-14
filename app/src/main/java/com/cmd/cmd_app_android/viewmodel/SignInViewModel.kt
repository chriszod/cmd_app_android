package com.cmd.cmd_app_android.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.cmd.cmd_app_android.view.fragments.sign_in.SignInEvents
import com.cmd.cmd_app_android.view.fragments.sign_in.SignInState
import com.cmd.cmd_app_android.view.utils.checkConnectivity
import com.cmd.cmd_app_android.view.utils.validateEmail
import com.cmd.cmd_app_android.view.utils.validatePassword
import com.solid.cmd_app_android.common.Resource
import com.solid.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.domain.usecase.UserUseCases
import com.cmd.cmd_app_android.view.utils.validateSignIn
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
                            value = event.value,
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
            val validate = validateSignIn(signInState.value.emailState.value, signInState.value.passwordState.value)
            if(validate.error) {
                _signInState.value = signInState.value.copy(
                    error = validate.message,
                    loading = false
                )
            } else {
                useCases.getUserByEmail(signInState.value.emailState.value).collectLatest {
                    when (it) {
                        is Resource.Loading -> {
                            _signInState.value = signInState.value.copy(loading = true)
                        }
                        is Resource.Error -> {
                            _signInState.value = signInState.value.copy(
                                loading = false,
                                error = it.error ?: "Unknown Error Occurred"
                            )
                        }
                        is Resource.Success -> {
                            val user = it.data ?: defaultUser
                            if (user.password == signInState.value.passwordState.value) {
                                _signInState.value = signInState.value.copy(
                                    loading = false,
                                    user = it.data ?: defaultUser
                                )
                            }
                            else {
                                _signInState.value = signInState.value.copy(
                                    loading = false,
                                    error = "Wrong password or Email"
                                )
                            }
                        }
                    }
                }
            }
        } else {
            _uiState.send(SignInUiEvents.NoInternetConnection)
        }
    }
}

sealed class SignInUiEvents {
    object NoInternetConnection : SignInUiEvents()
}