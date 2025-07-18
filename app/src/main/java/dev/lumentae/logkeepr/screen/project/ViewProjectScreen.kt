package dev.lumentae.logkeepr.screen.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lumentae.logkeepr.Globals
import dev.lumentae.logkeepr.data.entity.ProjectEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewProjectScreen(
    modifier: Modifier,
    project: ProjectEntity?,
    navController: NavController
) {
    if (project == null) {
        return
    }

    val projectDao = Globals.DATABASE.projectDao()
    val tags = projectDao.getTagsForProject(project.id)

    val shouldShowDialog = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Project") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                shouldShowDialog.value = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Log")
            }
        },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            item {
                ProjectCard(
                    project = project,
                    tags = tags,
                    showTagAddButton = true,
                    hasButtons = true,
                    onClick = {}
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val deletePressed = remember { mutableStateOf(false) }
                        val deleteMessage = if (deletePressed.value) {
                            "Are you sure?"
                        } else {
                            "Delete"
                        }
                        Button(
                            onClick = {
                                if (!deletePressed.value) {
                                    deletePressed.value = true
                                } else {
                                    projectDao.deleteProject(project)
                                    deletePressed.value = false
                                    navController.navigateUp()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        ) {
                            Text(deleteMessage)
                        }
                        Button(
                            onClick = {

                            },
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {

                            },
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            }
        }
    }
}