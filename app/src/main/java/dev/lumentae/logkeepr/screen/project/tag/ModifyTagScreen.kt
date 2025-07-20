package dev.lumentae.logkeepr.screen.project.tag

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.lumentae.logkeepr.screen.project.utils.ColorPicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyTagScreen(
    onTagAdded: (Pair<String, String>) -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    editing: Boolean = false,
    title: String = "",
    color: String = "#",
) {
    var title by remember { mutableStateOf(title) }
    val color = remember { mutableStateOf(color) }
    val colorError = remember { mutableStateOf(false) }

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
                    text = if (editing) "Edit Tag" else "Add Tag",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(4.dp)
                )
                TextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    isError = title.isEmpty(),
                    label = { Text("Tag Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true,
                )
                ColorPicker(
                    color = color,
                    colorError = colorError
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TextButton(
                        onClick = {
                            onDelete()
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                    Row() {
                        TextButton(
                            onClick = { onCancel() },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = {
                                if (title.isEmpty()) {
                                    return@TextButton
                                }
                                onTagAdded(Pair(title, color.value))
                            },
                            modifier = Modifier.padding(8.dp),
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}