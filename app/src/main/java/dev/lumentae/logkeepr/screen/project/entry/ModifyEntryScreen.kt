package dev.lumentae.logkeepr.screen.project.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getString
import dev.lumentae.logkeepr.R
import dev.lumentae.logkeepr.screen.project.utils.parseDurationToSeconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyEntryScreen(
    onEntryAdded: (Triple<String, String, Long>) -> Unit,
    onCancel: () -> Unit,
    editing: Boolean = false,
    title: String = "",
    description: String = "",
    timeSpent: String = "",
) {
    var title by remember { mutableStateOf(title) }
    var description by remember { mutableStateOf(description) }
    var timeSpent by remember { mutableStateOf(timeSpent) }
    var timeSpentError by remember { mutableStateOf(false) }
    var timeSpentDuration by remember { mutableLongStateOf(0) }

    Dialog(onDismissRequest = { onCancel() }) {
        // Draw a rectangle shape with rounded corners inside the dialog
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = if (editing) getString(
                        LocalContext.current,
                        R.string.edit_entry
                    ) else getString(LocalContext.current, R.string.add_entry),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(4.dp)
                )
                TextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    isError = title.isEmpty(),
                    label = { Text(getString(LocalContext.current, R.string.entry_title)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true,
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(getString(LocalContext.current, R.string.description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                TextField(
                    value = timeSpent,
                    onValueChange = {
                        timeSpentError =
                            !Regex("^(?=.*\\d+[dhms])(?:(\\d+)d\\s*)?(?:(\\d+)h\\s*)?(?:(\\d+)m\\s*)?(?:(\\d+)s)?\$").matches(
                                it
                            )
                        timeSpent = it
                        if (!timeSpentError) {
                            timeSpentDuration = parseDurationToSeconds(it) // Validate the input
                        }
                    },
                    isError = timeSpentError,
                    label = { Text(getString(LocalContext.current, R.string.time_spent)) },
                    supportingText = {
                        if (timeSpentError) {
                            Text(getString(LocalContext.current, R.string.time_invalid_format))
                        } else if (timeSpent.isNotEmpty()) {
                            Text(getString(LocalContext.current, R.string.time_format))
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = { onCancel() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(getString(LocalContext.current, R.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            if (title.isEmpty()) {
                                return@TextButton
                            }
                            onEntryAdded(Triple(title, description, timeSpentDuration))
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text(getString(LocalContext.current, R.string.confirm))
                    }
                }
            }
        }
    }
}