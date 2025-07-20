package dev.lumentae.logkeepr.screen.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.lumentae.logkeepr.Globals
import dev.lumentae.logkeepr.data.entity.ProjectEntity
import dev.lumentae.logkeepr.data.entity.TagEntity
import dev.lumentae.logkeepr.screen.entry.formatDurationToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(modifier: Modifier, navController: NavController) {
    var projectDao = Globals.DATABASE.projectDao()
    var projects = projectDao.getAllProjects()
    var tags = projectDao.getAllTags()

    val shouldShowDialog = remember { mutableStateOf(false) }

    if (shouldShowDialog.value) {
        ModifyProjectScreen(
            onProjectAdded = {
                var projectName = it.first
                var description = it.second
                var color = it.third

                shouldShowDialog.value = false
                // Refresh projects list after adding a new project
                projectDao.insertProject(
                    ProjectEntity(
                        name = projectName,
                        id = System.currentTimeMillis(),
                        createdAt = System.currentTimeMillis(),
                        description = description,
                        color = color
                    )
                )
                projects = projectDao.getAllProjects()
            },
            onCancel = {
                shouldShowDialog.value = false
            },
            projects = projects,
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Projects") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                shouldShowDialog.value = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Project")
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
            projects.forEach { project ->
                item(key = project.id) {
                    ProjectCard(project, tags, onClick = {
                        // Navigate to ViewProjectScreen with project ID
                        navController.navigate("ViewProject/${project.id}")
                    })
                }
            }
            if (projects.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Text(
                            "No projects found. Click the '+' button to add a new project.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProjectCard(
    project: ProjectEntity,
    tags: List<TagEntity>,
    showTagAddButton: Boolean = false,
    hasButtons: Boolean = false,
    onClick: () -> Unit = {},
    buttons: @Composable () -> Unit = {}
) {
    Card(
        elevation = CardDefaults.cardElevation(),
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = try {
                Color(android.graphics.Color.parseColor(project.color))
            } catch (_: Exception) {
                Color.Unspecified
            }
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(project.name, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "⌚ ${formatDurationToString(project.timeSpent)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!project.description.isNullOrEmpty()) {
                Spacer(Modifier.height(8.dp))

                Text(
                    project.description!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Tags
            if (showTagAddButton) {
                Spacer(Modifier.height(8.dp))
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                tags.forEach {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(it.name)
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = try {
                                Color(android.graphics.Color.parseColor(it.color))
                            } catch (_: Exception) {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                        ),
                        modifier = Modifier
                            .height(24.dp)
                    )
                }
                if (showTagAddButton) {
                    AssistChip(
                        onClick = {},
                        label = {
                            Text(
                                "+",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 12.sp
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                        modifier = Modifier
                            .height(24.dp)
                    )
                }
            }

            // Buttons
            Spacer(Modifier.height(8.dp))
            if (hasButtons) {
                buttons()
            }
        }
    }
}