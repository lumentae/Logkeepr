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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lumentae.logkeepr.Globals
import dev.lumentae.logkeepr.data.entity.EntryEntity
import dev.lumentae.logkeepr.data.entity.ProjectEntity
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

    val projectDao = Globals.DATABASE.projectDao()
    var projects = projectDao.getAllProjects()

    var tags by remember { mutableStateOf(projectDao.getTagsForProject(project.id)) }
    var entries by remember { mutableStateOf(projectDao.getEntriesForProject(project.id)) }

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
                projectDao.updateProject(project)
                projectShouldRefresh.value = true
            },
            onCancel = {
                showEditProjectDialog.value = false
            },
            projects = projects,
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
        var entryEntity = projectDao.getEntryById(editEntryId.longValue)
        ModifyEntryScreen(
            onEntryAdded = {
                var title = it.first
                var description = it.second
                var timeSpent = it.third

                if (editEntry.value) {
                    var entry = projectDao.getEntryById(editEntryId.longValue)!!
                    entry.title = title
                    entry.content = description
                    entry.timeSpent = timeSpent
                    projectDao.updateEntry(entry)
                } else {
                    val entry = EntryEntity(
                        title = title,
                        content = description,
                        projectId = project.id,
                        id = System.currentTimeMillis(),
                        timestamp = System.currentTimeMillis(),
                        timeSpent = timeSpent
                    )
                    projectDao.insertEntry(entry)
                }
                project.timeSpent = projectDao.getProjectTimeSpent(project.id)
                projectDao.updateProject(project)
                entries = projectDao.getEntriesForProject(project.id)
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
            TopAppBar(title = { Text("Project") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showAddEntryDialog.value = true
                editEntry.value = false
                editEntryId.longValue = 0
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
                                "Are you sure?"
                            }

                            2 -> {
                                "Really?"
                            }

                            else -> {
                                "Delete"
                            }
                        }
                        Button(
                            onClick = {
                                if (deletePressed.intValue < 2) {
                                    deletePressed.intValue++
                                } else {
                                    projectDao.deleteProject(project)
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
                            Text("Edit")
                        }
                    }
                }
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
                                Icon(Icons.Default.MoreVert, contentDescription = "More options")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit") },
                                    onClick = {
                                        editEntry.value = true
                                        showAddEntryDialog.value = true
                                        editEntryId.longValue = entry.id
                                        expanded = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete") },
                                    onClick = {
                                        projectDao.deleteEntry(entry)
                                        // Refresh entries list after deletion
                                        entries = projectDao.getEntriesForProject(project.id)
                                        expanded = false
                                        project.timeSpent =
                                            projectDao.getProjectTimeSpent(project.id)
                                        projectDao.updateProject(project)
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