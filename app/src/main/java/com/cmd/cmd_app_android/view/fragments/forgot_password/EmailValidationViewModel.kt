package com.cmd.cmd_app_android.view.fragments.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmd.cmd_app_android.common.Resource
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.domain.usecases.auth_use_cases.UserUseCases
import com.cmd.cmd_app_android.view.utils.validateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailValidationViewModel @Inject constructor(
    private val useCases: UserUseCases
): ViewModel() {

    private val _emailValidationState = MutableStateFlow(EmailValidationState())
    val emailValidationState get() = _emailValidationState.asStateFlow()

    private val _uiState = Channel<UiEvents>()
    val uiState = _uiState.receiveAsFlow()

    fun execute(event: EmailValidationEvents) {
        viewModelScope.launch {
            when(event) {
                is EmailValidationEvents.EmailTextChange -> {
                    val validateEmail = validateEmail(event.value)
                    _emailValidationState.value = emailValidationState.value.copy(
                        value = event.value.trim(),
                        error = validateEmail.message,
                        valid = !validateEmail.error
                    )
                }
                EmailValidationEvents.ResetPassword -> {
                    resetPassword()
                }
            }
        }
    }

    private suspend fun resetPassword() {
        val state = emailValidationState.value
        val emailValidate = validateEmail(state.value)

        if(!emailValidate.error) {
            useCases.getUserByEmail(state.value).collectLatest {
                when(it) {
                    is Resource.Success -> {
                        val user = it.data ?: defaultUser
                        _emailValidationState.value = emailValidationState.value.copy(
                            loading = true,
                            user = user,
                        )
                        useCases.saveUserToDatastore(user.id!!, user.email!!, false)
                        _uiState.send(UiEvents.EmailValidationSuccess)

                    }
                    is Resource.Error -> {
                        _emailValidationState.value = emailValidationState.value.copy(
                            loading = false,
                            error = it.error ?: "An Unknown Error Occurred"
                        )
                    }
                    is Resource.Loading -> {
                        _emailValidationState.value = emailValidationState.value.copy(
                            loading = true,
                        )
                    }
                }
            }
        }
    }
}

sealed class UiEvents {
    object EmailValidationSuccess: UiEvents()
}