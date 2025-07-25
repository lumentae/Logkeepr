package dev.lumentae.logkeepr.screen.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import dev.lumentae.logkeepr.R
import dev.lumentae.logkeepr.data.preferences.Preferences
import dev.lumentae.logkeepr.screen.components.DefaultPageTemplate
import dev.lumentae.logkeepr.screen.settings.preference.PreferenceSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier) {
    DefaultPageTemplate(getString(LocalContext.current, R.string.settings), modifier) {
        PreferenceSection(Preferences.Sections.general) {
            Preference(Preferences.Keys.language)
            Preference(Preferences.Keys.useDarkMode)
        }
    }
}