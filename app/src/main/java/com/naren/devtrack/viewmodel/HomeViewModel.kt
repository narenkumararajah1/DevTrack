package com.naren.devtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naren.devtrack.data.repository.AuthRepository
import com.naren.devtrack.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val userEmail: String? = null,
    val errorMessage: String? = null
)

class HomeViewModel @JvmOverloads constructor(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userProfileRepository: UserProfileRepository = UserProfileRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Not signed in")
            return
        }

        viewModelScope.launch {
            val result = userProfileRepository.getUserProfile(uid)
            _uiState.value = result.fold(
                onSuccess = { profile ->
                    _uiState.value.copy(isLoading = false, userEmail = profile?.email)
                },
                onFailure = { error ->
                    _uiState.value.copy(isLoading = false, errorMessage = error.message ?: "Failed to load profile")
                }
            )
        }
    }
}
