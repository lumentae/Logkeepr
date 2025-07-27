package dev.lumentae.logkeepr.screen.project

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import dev.lumentae.logkeepr.R
import dev.lumentae.logkeepr.data.database.DatabaseManager
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
    val projects = DatabaseManager.getAllProjects().collectAsState(initial = emptyList())

    val shouldShowDialog = remember { mutableStateOf(createNewProject) }
    if (shouldShowDialog.value) {
        ModifyProjectScreen(
            onProjectAdded = {
                val projectName = it.first
                val description = it.second
                val color = it.third

                shouldShowDialog.value = false
                val id = System.currentTimeMillis() // Generate a unique ID for the project
                // Refresh projects list after adding a new project
                DatabaseManager.insertProject(
                    ProjectEntity(
                        name = projectName,
                        id = id,
                        createdAt = id,
                        description = description,
                        color = color
                    )
                )
                navController.navigate("ViewProject/${id}") // Navigate to the new project
            },
            onCancel = {
                shouldShowDialog.value = false
            },
            projects = projects.value,
        )
    }

    DefaultPageTemplate("Projects", modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                shouldShowDialog.value = true
            }) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = getString(LocalContext.current, R.string.add_project)
                )
            }
        }
    )
    {
        projects.value.forEach { project ->
            val tags = DatabaseManager.getTagsForProject(project.id)
            ProjectCard(project, tags, onClick = {
                // Navigate to ViewProjectScreen with project ID
                navController.navigate("ViewProject/${project.id}")
            })
        }
        if (projects.value.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Text(
                    getString(LocalContext.current, R.string.no_projects),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}