package com.naren.devtrack.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.naren.devtrack.ui.components.CenteredTitle
import kotlinx.coroutines.delay

private const val SPLASH_DELAY_MILLIS = 1500L

@Composable
fun SplashScreen(onTimeout: () -> Unit, modifier: Modifier = Modifier) {
    LaunchedEffect(Unit) {
        delay(SPLASH_DELAY_MILLIS)
        onTimeout()
    }
    CenteredTitle(text = "Splash Screen", modifier = modifier)
}
