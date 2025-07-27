package dev.lumentae.logkeepr.screen.settings

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.getString
import dev.lumentae.logkeepr.R
import dev.lumentae.logkeepr.data.database.DatabaseManager
import dev.lumentae.logkeepr.data.preferences.Preferences
import dev.lumentae.logkeepr.screen.components.DefaultPageTemplate
import dev.lumentae.logkeepr.screen.settings.action.ActionButton
import dev.lumentae.logkeepr.screen.settings.preference.Preference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(modifier: Modifier) {
    val context = LocalContext.current
    DefaultPageTemplate(getString(context, R.string.settings), modifier) {
        Section(Preferences.Sections.general) {
            Preference(Preferences.Keys.language)
            Preference(Preferences.Keys.useDarkMode)
            Preference(Preferences.Keys.enableDangerZone)
        }
        Section(Preferences.Sections.data) {
            ActionButton(
                text = getString(context, R.string.export_data),
                onClick = {
                    val export = DatabaseManager.exportDatabase()
                    Log.d("SettingsScreen", "Exported data: $export")
                }
            )
            ActionButton(
                text = getString(context, R.string.import_data),
                onClick = {

                }
            )
        }
        Section(Preferences.Sections.danger) {
            Preference(Preferences.Keys.setStreak)

            val resetDataText = remember {
                mutableStateOf(getString(context, R.string.reset_data))
            }
            val resetDataCounter = remember { mutableIntStateOf(0) }
            ActionButton(
                text = resetDataText,
                onClick = {
                    resetDataCounter.intValue++
                    when (resetDataCounter.intValue) {
                        1 -> resetDataText.value = getString(context, R.string.confirm_1)
                        2 -> resetDataText.value = getString(context, R.string.confirm_2)
                        3 -> {
                            resetDataText.value = getString(context, R.string.reset_data)
                            // TODO: Enable this when the backup and restore feature is implemented
                            //Preferences.Keys.setStreak.onChange(mutableIntStateOf(0))
                            //DatabaseManager.resetData()
                            resetDataCounter.intValue = 0
                        }
                    }
                }
            )
        }
    }
}