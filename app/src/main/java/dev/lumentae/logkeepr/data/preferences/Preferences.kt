package dev.lumentae.logkeepr.data.preferences

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.core.Preferences
import dev.lumentae.logkeepr.R
import dev.lumentae.logkeepr.data.database.DatabaseManager

class PreferenceKey<T>(
    var key: Preferences.Key<T>,
    var value: MutableState<T>,
    var translationKey: Int,
    var extras: Any = Unit,
    var onChange: (MutableState<T>) -> Unit = { _ -> }
)

class PreferenceSection(
    var title: Int,
    var enabled: MutableState<Boolean> = mutableStateOf(true),
    var color: Color = Color.Unspecified
)

object Preferences {
    object Sections {
        val general = PreferenceSection(
            R.string.config_section_general
        )
        val data = PreferenceSection(
            R.string.config_section_data
        )
        val danger = PreferenceSection(
            R.string.config_section_danger,
            mutableStateOf(false), // This section is disabled by default,
            Color.Red
        )
    }

    object Keys {
        val language = PreferenceKey(
            stringPreferencesKey("language"), mutableStateOf("en"),
            R.string.config_entry_language,
            mapOf(
                "en" to R.string.language_english,
                "de" to R.string.language_german,
            )
        )
        val useDarkMode = PreferenceKey(
            booleanPreferencesKey("useDarkMode"), mutableStateOf(false),
            R.string.config_entry_useDarkMode
        )
        val enableDangerZone = PreferenceKey(
            booleanPreferencesKey("enableDangerZone"), Sections.danger.enabled,
            R.string.config_entry_enableDangerZone,
            onChange = { Sections.danger.enabled.value = it.value }
        )
        val setStreak = PreferenceKey(
            intPreferencesKey("setStreak"), mutableIntStateOf(0),
            R.string.config_entry_setStreak,
            extras = "streak",
            onChange = { DatabaseManager.setStreak(it) }
        )
    }
}

