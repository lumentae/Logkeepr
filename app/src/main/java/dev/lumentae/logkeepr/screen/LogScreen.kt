package dev.lumentae.logkeepr.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogScreen(modifier: Modifier) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Log") })
        }
    ) { padding ->

    }
}