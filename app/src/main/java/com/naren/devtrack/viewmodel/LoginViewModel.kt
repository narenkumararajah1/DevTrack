package com.naren.devtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naren.devtrack.data.repository.AuthRepository
import com.naren.devtrack.util.isValidEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

class LoginViewModel @JvmOverloads constructor(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun login() {
        val state = _uiState.value
        val validationError = validate(state.email, state.password)
        if (validationError != null) {
            _uiState.value = state.copy(errorMessage = validationError)
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = authRepository.signIn(state.email.trim(), state.password)
            _uiState.value = result.fold(
                onSuccess = { _uiState.value.copy(isLoading = false, isLoginSuccessful = true) },
                onFailure = { error ->
                    _uiState.value.copy(isLoading = false, errorMessage = error.message ?: "Login failed")
                }
            )
        }
    }

    private fun validate(email: String, password: String): String? = when {
        email.isBlank() -> "Email is required"
        !isValidEmail(email) -> "Enter a valid email address"
        password.isBlank() -> "Password is required"
        else -> null
    }
}
