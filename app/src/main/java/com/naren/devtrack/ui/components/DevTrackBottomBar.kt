package com.naren.devtrack.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.naren.devtrack.navigation.Screen

@Composable
fun DevTrackBottomBar(selectedRoute: String, onNavigate: (String) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedRoute == Screen.Home.route,
            onClick = { onNavigate(Screen.Home.route) },
            icon = { Text("🏠") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = selectedRoute == Screen.ProjectList.route,
            onClick = { onNavigate(Screen.ProjectList.route) },
            icon = { Text("📁") },
            label = { Text("Projects") }
        )
    }
}
