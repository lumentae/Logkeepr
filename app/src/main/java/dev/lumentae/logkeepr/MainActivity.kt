package dev.lumentae.logkeepr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import dev.lumentae.logkeepr.data.AppDatabase
import dev.lumentae.logkeepr.screen.*
import dev.lumentae.logkeepr.screen.project.ProjectsScreen
import dev.lumentae.logkeepr.screen.project.ViewProjectScreen
import dev.lumentae.logkeepr.ui.theme.LogkeeprTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Globals.DATABASE = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "logkeepr"
        )
            .allowMainThreadQueries()
            .build()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LogkeeprTheme {
                AppNavigationBar()
            }
        }
    }
}

enum class Destination(
    val route: String,
    val icon: ImageVector,
    val showInBottomBar: Boolean = true
) {
    HOME("Home", Icons.Default.Dashboard),
    PROJECTS("Projects", Icons.Default.Folder),
    LOG("Log", Icons.Default.AddCircleOutline, false),
    STATS("Stats", Icons.Default.Insights),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.HOME -> HomeScreen(modifier)
                    Destination.PROJECTS -> ProjectsScreen(modifier, navController)
                    Destination.LOG -> LogScreen(modifier)
                    Destination.STATS -> StatsScreen(modifier)
                    Destination.SETTINGS -> SettingsScreen(modifier)
                }
            }
        }
        composable("ViewProject/{projectId}") { backStackEntry ->
            val projectId = backStackEntry.arguments?.getString("projectId")?.toLongOrNull()
            if (projectId != null) {
                val project = Globals.DATABASE.projectDao().getProjectById(projectId)
                ViewProjectScreen(modifier, project, navController)
            } else {
                Text("Project not found", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun AppNavigationBar(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.HOME
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    if (!destination.showInBottomBar) return@forEachIndexed
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.route
                            )
                        },
                        label = { Text(destination.route) }
                    )
                }
            }
        }
    ) { contentPadding ->
        AppNavHost(navController, startDestination, modifier = Modifier.padding(contentPadding))
    }
}