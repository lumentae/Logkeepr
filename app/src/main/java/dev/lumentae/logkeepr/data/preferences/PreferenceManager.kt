@file:Suppress("UNCHECKED_CAST")

package dev.lumentae.logkeepr.data.preferences

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

object PreferenceManager {
    private val Context.dataStore by preferencesDataStore(name = "settings")

    suspend fun <T> setPreference(context: Context, key: PreferenceKey<T>, value: T) {
        key.value.value = value
        context.dataStore.edit { settings ->
            settings[key.key] = value
        }
    }

    @Composable
    fun <T> getPreference(context: Context, key: PreferenceKey<T>): MutableState<T> {
        val state = key.value

        LaunchedEffect(key) {
            context.dataStore.data.map { settings ->
                settings[key.key] ?: key.value.value
            }.collect { value ->
                state.value = value
            }
        }

        return state
    }
}