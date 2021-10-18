package com.cmd.cmd_app_android.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmd.cmd_app_android.common.Resource
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.domain.usecases.UserUseCases
import com.cmd.cmd_app_android.view.fragments.verification.VerificationEvents
import com.cmd.cmd_app_android.view.fragments.verification.VerificationState
import com.cmd.cmd_app_android.view.utils.validateConfirmPassword
import com.cmd.cmd_app_android.view.utils.validatePassword
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerificationViewModel @Inject constructor(
    application: Application,
    private val useCases: UserUseCases
): AndroidViewModel(application) {

    private val _verificationState = MutableStateFlow(VerificationState())
    val verificationState: StateFlow<VerificationState> get() = _verificationState

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    fun execute(event: VerificationEvents) {
        viewModelScope.launch {
            when(event) {
                is VerificationEvents.ChangePasswordTextField -> {
                    val validate = validatePassword(event.value)
                    _verificationState.value = verificationState.value.copy(
                        password = verificationState.value.password.copy(
                            value = event.value,
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is VerificationEvents.ChangeConfirmPasswordTextField -> {
                    val validate = validateConfirmPassword(event.value, verificationState.value.password.value)
                    _verificationState.value = verificationState.value.copy(
                        confirmPassword = verificationState.value.confirmPassword.copy(
                            value = event.value,
                            valid = !validate.error,
                            errorMessage = validate.message
                        )
                    )
                }
                is VerificationEvents.ChangeOtpTextField -> {
                    _verificationState.value = verificationState.value.copy(
                        otp = verificationState.value.otp.copy(
                            value = event.value
                        )
                    )
                }
                VerificationEvents.Continue -> {
                    changePassword()
                }
                VerificationEvents.VerifyOtp -> {
                    verifyOtp()
                }
                VerificationEvents.GetOtp -> {
                    getOtp()
                }
                is VerificationEvents.PostOtp -> {
                    val state = verificationState.value
                    _verificationState.value = state.copy(
                        otpResponse = event.otp
                    )
                }
            }
        }
    }

    private suspend fun verifyOtp() {
        Log.d("TAG", "verifyOtp: verifying")
        val state = verificationState.value
        if(state.otp.value == state.otpResponse) {
            Log.d("TAG", "verifyOtp: equal")
            _uiEvents.send(UiEvents.OtpVerifiedSuccessfully)
        } else {
            _verificationState.value = state.copy(
                otp = state.otp.copy(valid = false, errorMessage = "Wrong Otp")
            )
            Log.d("TAG", "verifyOtp: unequal")
            _uiEvents.send(UiEvents.WrongOtp)
        }
    }


    private suspend fun changePassword() {
        val state = verificationState.value
        val password = validatePassword(state.password.value)
        val confirmPassword = validateConfirmPassword(state.confirmPassword.value, state.password.value)
        
        if(!(password.error || confirmPassword.error)) {
            val userInfo = useCases.getUserInfoFromDatastore().first()
            useCases.changePassword(userInfo["user_id"] as String, state.password.value).collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        _verificationState.value = verificationState.value.copy(loading = true)
                    }
                    is Resource.Error -> {
                        _verificationState.value = verificationState.value.copy(
                            loading = false,
                            error = it.error ?: "Unknown Error Occurred"
                        )
                    }
                    is Resource.Success -> {
                        val user = it.data ?: defaultUser
                        _verificationState.value = verificationState.value.copy(
                            loading = false,
                            user = user
                        )
                        useCases.saveUserToDatastore(user.id, user.email, user.isEmailVerified)
                    }
                }
            }
        } 
    }
    
    private suspend fun getOtp() {
        val userInfo = useCases.getUserInfoFromDatastore().first()
        useCases.verifyEmail(userInfo["email"] as String).collectLatest {
            when (it) {
                is Resource.Loading -> {
                    _verificationState.value = verificationState.value.copy(loading = true)
                }
                is Resource.Error -> {
                    _verificationState.value = verificationState.value.copy(
                        loading = false,
                        error = it.error ?: "Unknown Error Occurred"
                    )
                }
                is Resource.Success -> {
                    val otp = it.data ?: ""
                    _verificationState.value = verificationState.value.copy(
                        loading = false,
                        otpResponse = otp
                    )
                }
            }
        }
    }
}

sealed class UiEvents {
    object WrongOtp: UiEvents()
    object OtpVerifiedSuccessfully: UiEvents()
}