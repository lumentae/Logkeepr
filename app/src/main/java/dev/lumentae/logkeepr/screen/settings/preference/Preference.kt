@file:Suppress("UNCHECKED_CAST")

package dev.lumentae.logkeepr.screen.settings.preference

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import dev.lumentae.logkeepr.data.preferences.PreferenceKey
import dev.lumentae.logkeepr.data.preferences.PreferenceManager
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

@Composable
fun <T> Preference(preference: PreferenceKey<T>) {
    val context = LocalContext.current
    val preferenceValue = PreferenceManager.getPreference(context, preference)

    val scope = rememberCoroutineScope()

    when (preference.value.value) {
        is Boolean -> {
            PreferenceContainer(
                name = getString(context, preference.translationKey),
                widget = {
                    Switch(
                        checked = preferenceValue.value as Boolean,
                        onCheckedChange = { isChecked ->
                            scope.launch {
                                PreferenceManager.setPreference(context, preference, isChecked as T)
                                preference.onChange(preferenceValue)
                            }
                        }
                    )
                }
            )
        }

        is Int -> {
            if (preference.extras == "streak") {

                PreferenceContainer(
                    name = getString(context, preference.translationKey),
                    widget = {
                        Box(
                            modifier = Modifier.fillMaxWidth(.5f)
                        ) {
                            OutlinedTextField(
                                value = (preferenceValue.value as Int - 1).toString(),
                                onValueChange = { newValue ->
                                    var intValue = newValue.toIntOrNull()
                                    if (intValue != null) {
                                        intValue += 1
                                        scope.launch {
                                            PreferenceManager.setPreference(
                                                context,
                                                preference,
                                                intValue as T
                                            )
                                            preference.onChange(preferenceValue)
                                        }
                                    }
                                },
                                label = { Text(getString(context, preference.translationKey)) },
                            )
                        }
                    }
                )
            }
        }

        is String -> {
            PreferenceContainer(
                name = getString(context, preference.translationKey),
                widget = {
                    if (preference.extras is Map<*, *>) {
                        val options = preference.extras as Map<String, Int>
                        val optionsList = options.map { getString(context, it.value) }

                        var expanded by remember { mutableStateOf(false) }
                        var selectedOption by remember { mutableStateOf(preferenceValue.value as String) }

                        Box(
                            modifier = Modifier.fillMaxWidth(.5f)
                        ) {
                            OutlinedTextField(
                                value = getString(context, options[selectedOption]!!),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(getString(context, preference.translationKey)) },
                                interactionSource = remember {
                                    InteractionSource {
                                        expanded = true
                                    }
                                },
                            )
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                            ) {
                                optionsList.forEachIndexed { index, s ->
                                    DropdownMenuItem(
                                        text = { Text(s) },
                                        onClick = {
                                            expanded = false
                                            selectedOption = options.keys.elementAt(index)
                                            scope.launch {
                                                PreferenceManager.setPreference(
                                                    context,
                                                    preference,
                                                    options.keys.elementAt(index) as T
                                                )
                                                preference.onChange(preferenceValue)
                                            }
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            )
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

private class InteractionSource(val onClick: () -> Unit) : MutableInteractionSource {
    override val interactions =
        MutableSharedFlow<Interaction>(
            extraBufferCapacity = 16,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    override suspend fun emit(interaction: Interaction) {
        when (interaction) {
            is PressInteraction.Press -> {
                onClick()
            }
        }
        interactions.emit(interaction)
    }

    override fun tryEmit(interaction: Interaction): Boolean {
        return interactions.tryEmit(interaction)
    }
}
