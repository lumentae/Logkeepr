package dev.lumentae.logkeepr.data.preferences

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.*
import dev.lumentae.logkeepr.R

class PreferenceKey<T>(
    var key: Preferences.Key<T>,
    var value: MutableState<T>,
    var translationKey: Int,
    var extras: Any = Unit,
)

class PreferenceSection(
    var title: Int,
    var enabled: MutableState<Boolean> = mutableStateOf(true)
)

object Preferences {
    object Sections {
        val general = PreferenceSection(
            R.string.config_section_general
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
    }
}

