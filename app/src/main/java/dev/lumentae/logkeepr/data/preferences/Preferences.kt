package dev.lumentae.logkeepr.data.preferences

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.*
import dev.lumentae.logkeepr.R

class PreferenceKey<T>(
    var key: Preferences.Key<T>,
    var value: MutableState<T>,
    var translationKey: Int
)

class PreferenceSection(
    var title: Int,
    var enabled: MutableState<Boolean> = mutableStateOf(true)
)

object Preferences {
    object Sections {
        val theme = PreferenceSection(
            R.string.config_section_theme
        )
    }

    object Keys {
        val useDarkMode = PreferenceKey(
            booleanPreferencesKey("useDarkMode"), mutableStateOf(false),
            R.string.config_entry_useDarkMode
        )
    }
}

