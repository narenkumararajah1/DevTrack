package com.naren.devtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.naren.devtrack.navigation.DevTrackNavHost
import com.naren.devtrack.ui.theme.DevTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DevTrackTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    DevTrackNavHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
