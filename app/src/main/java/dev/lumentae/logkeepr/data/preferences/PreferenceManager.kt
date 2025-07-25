package dev.lumentae.logkeepr.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

object PreferenceManager {
    private val Context.dataStore by preferencesDataStore(name = "settings")

    suspend fun <T> setPreference(context: Context, key: PreferenceKey<T>, value: T) {
        context.dataStore.edit { settings ->
            settings[key.key] = value
        }
    }

    fun <T> getPreference(context: Context, key: PreferenceKey<T>): Flow<T?> {
        return context.dataStore.data.map { settings ->
            settings[key.key] ?: key.defaultValue
        }
    }

    fun <T> getPreferenceValue(context: Context, key: PreferenceKey<T>): T {
        return runBlocking {
            getPreference(context, key).map { it ?: key.defaultValue }.first()
        }
    }
}