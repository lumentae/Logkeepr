package dev.lumentae.logkeepr.data.preferences

import androidx.datastore.preferences.core.*
import dev.lumentae.logkeepr.R

class PreferenceKey<T>(
    val key: Preferences.Key<T>,
    val defaultValue: T,
    val translationKey: Int
)

object PreferenceKeys {
    val useDarkMode = PreferenceKey(
        booleanPreferencesKey("useDarkMode"), true,
        R.string.config_useDarkMode
    )
}