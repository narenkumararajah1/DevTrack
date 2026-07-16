package com.naren.devtrack.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naren.devtrack.data.repository.AuthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val SPLASH_DELAY_MILLIS = 1500L

enum class SplashDestination { LOGIN, HOME }

class SplashViewModel @JvmOverloads constructor(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            delay(SPLASH_DELAY_MILLIS)
            _destination.value = if (authRepository.currentUser != null) {
                SplashDestination.HOME
            } else {
                SplashDestination.LOGIN
            }
        }
    }
}
