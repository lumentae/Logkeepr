package dev.lumentae.logkeepr.screen.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import dev.lumentae.logkeepr.R
import dev.lumentae.logkeepr.data.database.DatabaseManager
import dev.lumentae.logkeepr.data.database.entity.EntryEntity
import dev.lumentae.logkeepr.data.database.entity.ProjectEntity
import dev.lumentae.logkeepr.screen.project.components.ProjectCard
import dev.lumentae.logkeepr.screen.project.entry.EntryCard
import dev.lumentae.logkeepr.screen.project.entry.ModifyEntryScreen
import dev.lumentae.logkeepr.screen.project.utils.formatDurationToString

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

    var projects = DatabaseManager.getAllProjects().collectAsState()

    var tags by remember { mutableStateOf(DatabaseManager.getTagsForProject(project.id)) }
    var entries by remember { mutableStateOf(DatabaseManager.getEntriesForProject(project.id)) }

    val projectShouldRefresh = remember { mutableStateOf(false) }
    val showEditProjectDialog = remember { mutableStateOf(false) }

    if (showEditProjectDialog.value) {
        ModifyProjectScreen(
            onProjectAdded = {
                var projectName = it.first
                var description = it.second
                var color = it.third

                project.name = projectName
                project.description = description
                project.color = color

                showEditProjectDialog.value = false
                // Refresh projects list after adding a new project
                DatabaseManager.updateProject(project)
                projectShouldRefresh.value = true
                DatabaseManager.updateStreak()
            },
            onCancel = {
                showEditProjectDialog.value = false
            },
            projects = projects.value,
            editing = true,
            name = project.name,
            description = project.description ?: "",
            color = project.color ?: "#"
        )
    }

    var showAddEntryDialog = remember { mutableStateOf(false) }
    var editEntry = remember { mutableStateOf(false) }
    var editEntryId = remember { mutableLongStateOf(0) }

    if (showAddEntryDialog.value) {
        var entryEntity = DatabaseManager.getEntryById(editEntryId.longValue)
        ModifyEntryScreen(
            onEntryAdded = {
                var title = it.first
                var description = it.second
                var timeSpent = it.third

                if (editEntry.value) {
                    var entry = DatabaseManager.getEntryById(editEntryId.longValue)!!
                    entry.title = title
                    entry.content = description
                    entry.timeSpent = timeSpent
                    DatabaseManager.updateEntry(entry)
                } else {
                    val entry = EntryEntity(
                        title = title,
                        content = description,
                        projectId = project.id,
                        id = System.currentTimeMillis(),
                        timestamp = System.currentTimeMillis(),
                        timeSpent = timeSpent
                    )
                    DatabaseManager.insertEntry(entry)
                    DatabaseManager.updateStreak()
                }
                project.timeSpent = DatabaseManager.getProjectTimeSpent(project.id)
                DatabaseManager.updateProject(project)
                entries = DatabaseManager.getEntriesForProject(project.id)
                showAddEntryDialog.value = false
                projectShouldRefresh.value = true
            },
            onCancel = {
                showAddEntryDialog.value = false
            },
            editing = editEntry.value,
            title = if (editEntry.value) {
                entryEntity?.title ?: ""
            } else {
                ""
            },
            description = if (editEntry.value) {
                entryEntity?.content ?: ""
            } else {
                ""
            },
            timeSpent = if (editEntry.value) {
                entryEntity?.timeSpent?.let { formatDurationToString(it) } ?: "0"
            } else {
                "0"
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(getString(LocalContext.current, R.string.project)) })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showAddEntryDialog.value = true
                editEntry.value = false
                editEntryId.longValue = 0
            }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = getString(LocalContext.current, R.string.add_log)
                )
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
                    shouldRefresh = projectShouldRefresh,
                    onClick = {}
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val deletePressed = remember { mutableIntStateOf(0) }
                        val deleteMessage = when (deletePressed.intValue) {
                            1 -> {
                                getString(LocalContext.current, R.string.project_delete_confirm_1)
                            }

                            2 -> {
                                getString(LocalContext.current, R.string.project_delete_confirm_2)
                            }

                            else -> {
                                getString(LocalContext.current, R.string.delete)
                            }
                        }
                        Button(
                            onClick = {
                                if (deletePressed.intValue < 2) {
                                    deletePressed.intValue++
                                } else {
                                    DatabaseManager.deleteProject(project)
                                    deletePressed.intValue = 0
                                    navController.navigateUp()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        ) {
                            Text(deleteMessage)
                        }
                        Button(
                            onClick = {
                                showEditProjectDialog.value = true
                                projectShouldRefresh.value = true
                            },
                        ) {
                            Text(getString(LocalContext.current, R.string.edit))
                        }
                    }
                }
            }
            item {
                HorizontalDivider(
                    modifier = Modifier.padding(bottom = 4.dp),
                    thickness = 1.dp
                )
            }
            entries.forEach { entry ->
                item(key = entry.id) {
                    var expanded by remember { mutableStateOf(false) }
                    EntryCard(entry = entry, menu = {
                        Box(
                            modifier = Modifier
                                .padding(16.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            IconButton(onClick = { expanded = !expanded }) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = getString(
                                        LocalContext.current,
                                        R.string.more_options
                                    )
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(getString(LocalContext.current, R.string.edit)) },
                                    onClick = {
                                        editEntry.value = true
                                        showAddEntryDialog.value = true
                                        editEntryId.longValue = entry.id
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            getString(
                                                LocalContext.current,
                                                R.string.delete
                                            )
                                        )
                                    },
                                    onClick = {
                                        DatabaseManager.deleteEntry(entry)
                                        // Refresh entries list after deletion
                                        entries = DatabaseManager.getEntriesForProject(project.id)
                                        expanded = false
                                        project.timeSpent =
                                            DatabaseManager.getProjectTimeSpent(project.id)
                                        DatabaseManager.updateProject(project)
                                    }
                                )
                            }
                        }
                    })
                }
            }
        }
    }
}