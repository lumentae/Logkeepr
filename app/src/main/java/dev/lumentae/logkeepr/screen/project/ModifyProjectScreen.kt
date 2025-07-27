package dev.lumentae.logkeepr.screen.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat.getString
import androidx.core.graphics.toColorInt
import dev.lumentae.logkeepr.R
import dev.lumentae.logkeepr.data.database.entity.ProjectEntity

@Composable
fun ModifyProjectScreen(
    onProjectAdded: (Triple<String, String, String>) -> Unit,
    onCancel: () -> Unit,
    projects: List<ProjectEntity>,
    editing: Boolean = false,
    name: String = "",
    description: String = "",
    color: String = "#",
) {
    val name = remember { mutableStateOf(name) }
    val nameError = remember { mutableStateOf(false) }
    val description = remember { mutableStateOf(description) }
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
                    text = if (editing) getString(
                        LocalContext.current,
                        R.string.edit_project
                    ) else getString(LocalContext.current, R.string.add_project),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(4.dp)
                )
                TextField(
                    value = name.value,
                    onValueChange = {
                        name.value = it
                        nameError.value =
                            it.isEmpty() || projects.any { project -> project.name == it }
                    },
                    isError = projects.any { it.name == name.value } || name.value.isEmpty(),
                    label = { Text(getString(LocalContext.current, R.string.project_name)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true,
                )
                TextField(
                    value = description.value,
                    onValueChange = { description.value = it },
                    label = { Text(getString(LocalContext.current, R.string.description)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
                Row()
                {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .offset(y = 24.dp, x = 12.dp)
                            .background(
                                color = try {
                                    Color(color.value.toColorInt())
                                } catch (_: Exception) {
                                    Color.White
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                    )
                    TextField(
                        value = color.value,
                        onValueChange = {
                            if (!it.startsWith("#")) {
                                color.value = "#"
                                return@TextField
                            }
                            if (it.length > 7)
                                return@TextField

                            color.value = it
                            colorError.value =
                                !Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$").matches(it)
                        },
                        isError = !Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$").matches(color.value),
                        label = { Text(getString(LocalContext.current, R.string.color_hex)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        singleLine = true,
                    )
                }
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
                            if (name.value.isEmpty() || color.value.isEmpty() || nameError.value || colorError.value) {
                                return@TextButton
                            }
                            onProjectAdded(Triple(name.value, description.value, color.value))
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