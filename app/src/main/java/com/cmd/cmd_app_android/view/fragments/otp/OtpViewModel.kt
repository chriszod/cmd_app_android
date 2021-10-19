package com.cmd.cmd_app_android.view.fragments.otp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cmd.cmd_app_android.common.Resource
import com.cmd.cmd_app_android.data.models.defaultUser
import com.cmd.cmd_app_android.domain.usecases.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val useCases: UserUseCases,
    application: Application
) : AndroidViewModel(application) {

    private val _otpState = MutableStateFlow(OtpState())
    val otpState: StateFlow<OtpState> get() = _otpState

    private val _uiEvents = Channel<UiEvents>()
    val uiEvents = _uiEvents.receiveAsFlow()

    init {
        execute(OtpEvents.GetUserByID)
    }

    fun execute(event: OtpEvents) {
        viewModelScope.launch {
            when(event) {
                is OtpEvents.ChangeOtpTextField -> {
                    _otpState.value = otpState.value.copy(
                        otp = otpState.value.otp.copy(
                            value = event.value
                        )
                    )
                }
                is OtpEvents.VerifyOtp -> {
                    verifyOtp()
                }
                is OtpEvents.GetOtp -> {
                    getOtp()
                }
                is OtpEvents.PostOtp -> {
                    val state = otpState.value
                    _otpState.value = state.copy(
                        otpResponse = event.otp
                    )
                }
                is OtpEvents.GetUserByID -> {
                    getUser()
                }
            }
        }

    }

    private suspend fun getUser() {
        val data = useCases.getUserInfoFromDatastore().first()
        val id = data["user_id"] as String
        Log.d("TAG", "getUser: $id")
        useCases.getUserById(id).collectLatest {
            when (it) {
                is Resource.Loading -> {
                    _otpState.value = otpState.value.copy(loading = true)
                }
                is Resource.Error -> {
                    _otpState.value = otpState.value.copy(
                        loading = false,
                        error = it.error ?: "Unknown Error Occurred"
                    )
                }
                is Resource.Success -> {
                    val user = it.data ?: defaultUser
                    _otpState.value = otpState.value.copy(
                        loading = false,
                        user = user
                    )
                }
            }
        }
    }

    private suspend fun verifyOtp() {
        val state = otpState.value
        if(state.otp.value == state.otpResponse) {
            if(state.user != defaultUser) {
                Log.d("tag", "verifyOtp: ${state.user.toString()}")
                val user = state.user.copy(isEmailVerified = true)
                useCases.updateUser(user).collect {
                    when (it) {
                        is Resource.Loading -> {
                            _otpState.value = otpState.value.copy(loading = true)
                        }
                        is Resource.Error -> {
                            _otpState.value = otpState.value.copy(
                                loading = false,
                                error = it.error ?: "Unknown Error Occurred"
                            )
                        }
                        is Resource.Success -> {
                            val userDto = it.data ?: defaultUser
                            _otpState.value = otpState.value.copy(
                                loading = false,
                                user = userDto
                            )
                            Log.d("TAG", "verifyOtp: ${it.data.toString()}")
                            useCases.saveUserToDatastore(userDto.id, userDto.email, userDto.isEmailVerified)
                            _uiEvents.send(UiEvents.OtpVerifiedSuccessfully)
                        }
                    }
                }
            } else {
                getUser()
            }
        } else {
            _otpState.value = state.copy(
                otp = state.otp.copy(valid = false, errorMessage = "Wrong Otp")
            )
            _uiEvents.send(UiEvents.WrongOtp)
        }
    }

    private suspend fun getOtp() {
        val userInfo = useCases.getUserInfoFromDatastore().first()
        useCases.verifyEmail(userInfo["email"] as String).collectLatest {
            when (it) {
                is Resource.Loading -> {
                    _otpState.value = otpState.value.copy(loading = true)
                }
                is Resource.Error -> {
                    _otpState.value = otpState.value.copy(
                        loading = false,
                        error = it.error ?: "Unknown Error Occurred"
                    )
                }
                is Resource.Success -> {
                    val otp = it.data ?: ""
                    _otpState.value = otpState.value.copy(
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