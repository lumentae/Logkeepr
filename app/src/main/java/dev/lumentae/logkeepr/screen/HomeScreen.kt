package dev.lumentae.logkeepr.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lumentae.logkeepr.Globals
import dev.lumentae.logkeepr.screen.components.DefaultPageTemplate
import dev.lumentae.logkeepr.screen.project.components.ProjectCard
import dev.lumentae.logkeepr.screen.project.utils.formatDurationToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavController) {
    var projectDao = Globals.DATABASE.projectDao()
    var streakDao = Globals.DATABASE.streakDao()

    var lastProject = projectDao.getLastChangedProject()
    var tags = projectDao.getTagsForProject(lastProject?.id ?: -1)
    var projects = projectDao.getAllProjects()
    var entries = projectDao.getAllEntries()
    var totalTime = entries.sumOf { it.timeSpent }
    var streakDays = streakDao.getAllStreaks().count() - 1 // Exclude the current streak day

    DefaultPageTemplate("Home", modifier) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(),
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Welcome to LogKeepr!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Nice to see you again!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                FloatingActionButton(
                    onClick = { navController.navigate("Projects/CreateNew") },
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Project")
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(),
        ) {
            Text(
                "📂 ${projects.count()} projects\n📗 ${entries.count()} entries\n🕒 ${
                    formatDurationToString(
                        totalTime
                    )
                } total time logged",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
        if (streakDays > 0) {
            Spacer(Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(),
            ) {
                Text(
                    "🔥 $streakDays days streak",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (lastProject == null) {
            Text(
                "No projects found. Create a new project to get started!",
                style = MaterialTheme.typography.bodyMedium
            )
            return@DefaultPageTemplate
        }
        Text("Last Project", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        ProjectCard(
            project = lastProject,
            tags = tags,
            onClick = {
                navController.navigate("ViewProject/${lastProject.id}")
            }
        )
    }
}