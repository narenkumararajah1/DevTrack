package com.naren.devtrack.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.naren.devtrack.ui.components.CenteredTitle
import com.naren.devtrack.viewmodel.SplashDestination
import com.naren.devtrack.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = viewModel()
) {
    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(destination) {
        when (destination) {
            SplashDestination.LOGIN -> onNavigateToLogin()
            SplashDestination.HOME -> onNavigateToHome()
            null -> Unit
        }
    }

    CenteredTitle(text = "Splash Screen", modifier = modifier)
}
