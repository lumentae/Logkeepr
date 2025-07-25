@file:Suppress("UNCHECKED_CAST")

package dev.lumentae.logkeepr.screen.settings

import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import dev.lumentae.logkeepr.data.preferences.PreferenceKey
import dev.lumentae.logkeepr.data.preferences.PreferenceManager
import dev.lumentae.logkeepr.screen.settings.preference.PreferenceContainer
import kotlinx.coroutines.launch

@Composable
fun <T> Preference(preference: PreferenceKey<T>) {
    val context = LocalContext.current
    val preferenceValue = PreferenceManager.getPreference(context, preference)
        .collectAsState(initial = false)

    val scope = rememberCoroutineScope()

    when (preference.defaultValue) {
        is Boolean -> {
            PreferenceContainer<T>(
                name = getString(context, preference.translationKey),
                widget = {
                    Switch(
                        checked = preferenceValue.value as Boolean,
                        onCheckedChange = { isChecked ->
                            scope.launch {
                                PreferenceManager.setPreference(context, preference, isChecked as T)
                            }
                        }
                    )
                }
            )
        }

        is Int -> {
            // TODO: Implement Int preference handling
        }

        is String -> {
            // TODO: Implement String preference handling
        }

        else -> {
            throw IllegalArgumentException(
                "Unsupported preference type: ${
                    getString(
                        context,
                        preference.translationKey
                    )
                }"
            )
        }
    }
}