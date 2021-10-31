package com.cmd.cmd_app_android.view.fragments.password_validation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmd.cmd_app_android.common.Resource
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.domain.usecases.auth_use_cases.UserUseCases
import com.cmd.cmd_app_android.view.utils.validateConfirmPassword
import com.cmd.cmd_app_android.view.utils.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordValidationViewModel @Inject constructor(
    private val useCases: UserUseCases,
    application: Application
) : AndroidViewModel(application) {

    private val _passwordValidationState = MutableStateFlow(PasswordValidationState())
    val passwordValidationState: StateFlow<PasswordValidationState> get() = _passwordValidationState

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun execute(event: PasswordValidationEvents) {
        viewModelScope.launch {
            when(event) {
                is PasswordValidationEvents.ChangePasswordTextField -> {
                    val validate = validatePassword(event.value)
                    _passwordValidationState.value = passwordValidationState.value.copy(
                        password = passwordValidationState.value.password.copy(
                            value = event.value,
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is PasswordValidationEvents.ChangeConfirmPasswordTextField -> {
                    val validate = validateConfirmPassword(event.value, passwordValidationState.value.password.value)
                    _passwordValidationState.value = passwordValidationState.value.copy(
                        confirmPassword = passwordValidationState.value.confirmPassword.copy(
                            value = event.value,
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                PasswordValidationEvents.Continue -> {
                    changePassword()
                }
            }
        }
    }

    private suspend fun changePassword() {

        val state = passwordValidationState.value
        val password = validatePassword(state.password.value)
        val confirmPassword = validateConfirmPassword(state.confirmPassword.value, state.password.value)

        if(!(password.error || confirmPassword.error)) {
            val data = useCases.getUserInfoFromDatastore().first()
            val id = data["user_id"] as String
            useCases.changePassword(id, state.password.value).collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        _passwordValidationState.value = passwordValidationState.value.copy(loading = true)
                    }
                    is Resource.Error -> {
                        _passwordValidationState.value = passwordValidationState.value.copy(
                            loading = false,
                            error = it.error ?: "Unknown Error Occurred"
                        )
                    }
                    is Resource.Success -> {
                        val user = it.data ?: defaultUser
                        _passwordValidationState.value = passwordValidationState.value.copy(
                            loading = false,
                            user = user,
                        )
                        _uiEvents.send(UiEvents.ChangedSuccessfully)
                    }
                }
            }
        }
    }

}

sealed class UiEvents {
    object ChangedSuccessfully: UiEvents()
}