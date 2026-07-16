package com.naren.devtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naren.devtrack.data.repository.AuthRepository
import com.naren.devtrack.data.repository.UserProfileRepository
import com.naren.devtrack.util.isValidEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val MIN_PASSWORD_LENGTH = 6

data class RegisterUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegisterSuccessful: Boolean = false
)

class RegisterViewModel @JvmOverloads constructor(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userProfileRepository: UserProfileRepository = UserProfileRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun register() {
        val state = _uiState.value
        val validationError = validate(state)
        if (validationError != null) {
            _uiState.value = state.copy(errorMessage = validationError)
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val signUpResult = authRepository.signUp(state.email.trim(), state.password)
            val result = signUpResult.fold(
                onSuccess = { authUser -> userProfileRepository.createUserProfile(authUser.uid, authUser.email) },
                onFailure = { Result.failure(it) }
            )
            if (signUpResult.isSuccess) {
                // Registration signs the new user in; sign out so the Register -> Login flow
                // requires them to authenticate explicitly, per the app's navigation spec.
                authRepository.signOut()
            }
            _uiState.value = result.fold(
                onSuccess = { _uiState.value.copy(isLoading = false, isRegisterSuccessful = true) },
                onFailure = { error ->
                    _uiState.value.copy(isLoading = false, errorMessage = error.message ?: "Registration failed")
                }
            )
        }
    }

    private fun validate(state: RegisterUiState): String? = when {
        state.email.isBlank() -> "Email is required"
        !isValidEmail(state.email) -> "Enter a valid email address"
        state.password.length < MIN_PASSWORD_LENGTH -> "Password must be at least $MIN_PASSWORD_LENGTH characters"
        state.password != state.confirmPassword -> "Passwords do not match"
        else -> null
    }
}
