package dev.lumentae.logkeepr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import dev.lumentae.logkeepr.data.database.AppDatabase
import dev.lumentae.logkeepr.data.database.DatabaseManager
import dev.lumentae.logkeepr.data.preferences.PreferenceManager
import dev.lumentae.logkeepr.data.preferences.Preferences
import dev.lumentae.logkeepr.screen.*
import dev.lumentae.logkeepr.screen.project.ProjectsScreen
import dev.lumentae.logkeepr.screen.project.ViewProjectScreen
import dev.lumentae.logkeepr.screen.settings.SettingsScreen
import dev.lumentae.logkeepr.ui.theme.LogkeeprTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Globals.DATABASE = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "logkeepr"
        )
            .allowMainThreadQueries()
            .build()

        DatabaseManager.loadDatabase()
        DatabaseManager.checkStreak()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkTheme = PreferenceManager.getPreference(this, Preferences.Entries.useDarkMode)
            LogkeeprTheme(
                darkTheme = darkTheme.value
            ) {
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
    STATS("Stats", Icons.Default.Insights),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    selectedDestination: MutableIntState
) {
    NavHost(
        navController,
        startDestination = startDestination.route
    ) {
        Destination.entries.forEach { destination ->
            composable(destination.route) {
                when (destination) {
                    Destination.HOME -> HomeScreen(modifier, navController)
                    Destination.PROJECTS -> ProjectsScreen(modifier, navController)
                    Destination.STATS -> StatsScreen(modifier)
                    Destination.SETTINGS -> SettingsScreen(modifier)
                }
            }
        }
        composable("Projects/CreateNew") {
            selectedDestination.intValue = Destination.PROJECTS.ordinal
            ProjectsScreen(modifier, navController, createNewProject = true)
        }
        composable("ViewProject/{projectId}") { backStackEntry ->
            selectedDestination.intValue = Destination.PROJECTS.ordinal
            val projectId = backStackEntry.arguments?.getString("projectId")?.toLongOrNull()
            if (projectId != null) {
                val project = DatabaseManager.getProjectById(projectId)
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
    var selectedDestination = rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    if (!destination.showInBottomBar) return@forEachIndexed
                    NavigationBarItem(
                        selected = selectedDestination.intValue == index,
                        onClick = {
                            navController.navigate(route = destination.route)
                            selectedDestination.intValue = index
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
        AppNavHost(
            navController,
            startDestination,
            modifier = Modifier.padding(contentPadding),
            selectedDestination = selectedDestination
        )
    }
}