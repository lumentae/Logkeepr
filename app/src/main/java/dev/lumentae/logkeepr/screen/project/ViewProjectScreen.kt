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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lumentae.logkeepr.Globals
import dev.lumentae.logkeepr.data.entity.ProjectEntity
import dev.lumentae.logkeepr.screen.entry.EntryCard
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import dev.lumentae.logkeepr.data.entity.EntryEntity
import dev.lumentae.logkeepr.screen.entry.ModifyEntryScreen

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
        ModifyEntryScreen(
            onEntryAdded = {
                var title = it.first
                var description = it.second

                if (editEntry.value) {
                    var entry = projectDao.getEntryById(editEntryId.longValue)!!
                    entry.title = title
                    entry.content = description
                    projectDao.updateEntry(entry)
                } else {
                    val entry = EntryEntity(
                        title = title,
                        content = description,
                        projectId = project.id,
                        id = System.currentTimeMillis(),
                        timestamp = System.currentTimeMillis()
                    )
                    projectDao.insertEntry(entry)
                }
                entries = projectDao.getEntriesForProject(project.id)
                showAddEntryDialog.value = false
            },
            onCancel = {
                showAddEntryDialog.value = false
            },
            editing = editEntry.value,
            title = if (editEntry.value) {
                projectDao.getEntryById(editEntryId.longValue)?.title ?: ""
            } else {
                ""
            },
            description = if (editEntry.value) {
                projectDao.getEntryById(editEntryId.longValue)?.content ?: ""
            } else {
                ""
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
                                .padding(16.dp)
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