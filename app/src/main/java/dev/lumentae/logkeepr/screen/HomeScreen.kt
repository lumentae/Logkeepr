package dev.lumentae.logkeepr.screen

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.lumentae.logkeepr.Globals
import dev.lumentae.logkeepr.screen.project.ProjectCard
import dev.lumentae.logkeepr.screen.project.utils.formatDurationToString
import java.util.logging.Logger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavController) {
    var projectDao = Globals.DATABASE.projectDao()
    var lastProject = projectDao.getLastChangedProject()
    var tags = projectDao.getTagsForProject(lastProject.id)
    var projects = projectDao.getAllProjects()
    var entries = projectDao.getAllEntries()
    var totalTime = entries.sumOf { it.timeSpent }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home") })
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(),
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
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
                        "📂 ${projects.count()} projects\n📗 ${entries.count()} entries\n🕒 ${formatDurationToString(totalTime)} total time logged",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            item {
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
    }
}