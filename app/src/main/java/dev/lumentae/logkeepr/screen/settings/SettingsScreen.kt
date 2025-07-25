package dev.lumentae.logkeepr.screen.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.lumentae.logkeepr.data.preferences.PreferenceKeys
import dev.lumentae.logkeepr.screen.components.DefaultPageTemplate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier) {
    DefaultPageTemplate("Settings", modifier) {
        Preference(PreferenceKeys.useDarkMode)
    }
}