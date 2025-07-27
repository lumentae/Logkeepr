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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavController
import dev.lumentae.logkeepr.R
import dev.lumentae.logkeepr.data.database.DatabaseManager
import dev.lumentae.logkeepr.screen.components.DefaultPageTemplate
import dev.lumentae.logkeepr.screen.project.components.ProjectCard
import dev.lumentae.logkeepr.screen.project.utils.formatDurationToString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, navController: NavController) {

    val lastProject = DatabaseManager.getLastChangedProject()
    val tags = DatabaseManager.getTagsForProject(lastProject?.id ?: -1)
    val projects = DatabaseManager.getAllProjects().collectAsState()
    val entries = DatabaseManager.getAllEntries().collectAsState()
    val totalTime = entries.value.sumOf { it.timeSpent }
    val streakDays = DatabaseManager.getAllStreaks()
        .collectAsState().value.count() - 1 // Exclude the current streak day

    val context = LocalContext.current
    val resources = context.resources

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
                        getString(LocalContext.current, R.string.home_welcome),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        getString(LocalContext.current, R.string.home_nice_to_see_you),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                FloatingActionButton(
                    onClick = { navController.navigate("Projects/CreateNew") },
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = getString(LocalContext.current, R.string.add_project)
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(),
        ) {
            Text(
                "${
                    resources.getQuantityString(
                        R.plurals.home_stats_projects,
                        projects.value.count(),
                        projects.value.count()
                    )
                }\n" +
                        "${
                            resources.getQuantityString(
                                R.plurals.home_stats_entries,
                                entries.value.count(),
                                entries.value.count()
                            )
                        }\n" +
                        context.getString(
                            R.string.home_stats_time,
                            formatDurationToString(totalTime)
                        ),
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
                    resources.getQuantityString(R.plurals.home_streak, streakDays, streakDays),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        if (lastProject == null) {
            Text(
                getString(LocalContext.current, R.string.no_projects),
                style = MaterialTheme.typography.bodyMedium
            )
            return@DefaultPageTemplate
        }
        Text(
            getString(LocalContext.current, R.string.home_last_project),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
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