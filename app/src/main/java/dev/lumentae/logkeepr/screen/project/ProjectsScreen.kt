package dev.lumentae.logkeepr.screen.project

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lumentae.logkeepr.Globals
import dev.lumentae.logkeepr.data.database.entity.ProjectEntity
import dev.lumentae.logkeepr.screen.components.DefaultPageTemplate
import dev.lumentae.logkeepr.screen.project.components.ProjectCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    modifier: Modifier,
    navController: NavController,
    createNewProject: Boolean = false
) {
    var projectDao = Globals.DATABASE.projectDao()
    var projects by remember { mutableStateOf(emptyList<ProjectEntity>()) }

    var loaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!loaded) {
            projects = projectDao.getAllProjects()
            loaded = true
        }
    }

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

    DefaultPageTemplate("Projects", modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                shouldShowDialog.value = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Project")
            }
        }
    )
    {
        if (loaded) {
            projects.forEach { project ->
                val tags = projectDao.getTagsForProject(project.id)
                ProjectCard(project, tags, onClick = {
                    // Navigate to ViewProjectScreen with project ID
                    navController.navigate("ViewProject/${project.id}")
                })
            }
            if (projects.isEmpty()) {
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
        } else {
            // Show a loading indicator or placeholder while projects are being loaded
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        }
    }
}