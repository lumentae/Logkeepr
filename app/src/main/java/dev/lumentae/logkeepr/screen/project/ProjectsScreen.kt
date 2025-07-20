package dev.lumentae.logkeepr.screen.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
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
fun ProjectsScreen(
    modifier: Modifier,
    navController: NavController,
    createNewProject: Boolean = false
) {
    var projectDao = Globals.DATABASE.projectDao()
    var projects = projectDao.getAllProjects()

    val shouldShowDialog = remember { mutableStateOf(createNewProject) }

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
                    val tags = projectDao.getTagsForProject(project.id)
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