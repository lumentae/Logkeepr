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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import dev.lumentae.logkeepr.data.database.AppDatabase
import dev.lumentae.logkeepr.data.database.DatabaseManager
import dev.lumentae.logkeepr.data.preferences.PreferenceManager
import dev.lumentae.logkeepr.data.preferences.Preferences
import dev.lumentae.logkeepr.screen.HomeScreen
import dev.lumentae.logkeepr.screen.StatsScreen
import dev.lumentae.logkeepr.screen.project.ProjectsScreen
import dev.lumentae.logkeepr.screen.project.ViewProjectScreen
import dev.lumentae.logkeepr.screen.settings.SettingsScreen
import dev.lumentae.logkeepr.ui.theme.LogkeeprTheme
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

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

        FileKit.init(this)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkTheme = PreferenceManager.getPreference(this, Preferences.Keys.useDarkMode)
            LogkeeprTheme(
                darkTheme = darkTheme.value
            ) {
                AppNavigationBar()
            }
        }
    }
}

enum class Destination(
    val route: Int,
    val icon: ImageVector,
    val showInBottomBar: Boolean = true
) {
    HOME(R.string.route_home, Icons.Default.Dashboard),
    PROJECTS(R.string.route_projects, Icons.Default.Folder),
    STATS(R.string.stats, Icons.Default.Insights),
    SETTINGS(R.string.route_settings, Icons.Default.Settings)
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: Destination,
    modifier: Modifier = Modifier,
    selectedDestination: MutableIntState
) {
    val context = LocalContext.current
    NavHost(
        navController,
        startDestination = getString(context, startDestination.route)
    ) {
        Destination.entries.forEach { destination ->
            composable(getString(context, destination.route)) {
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
                Text(
                    getString(context, R.string.project_not_found),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun AppNavigationBar(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val startDestination = Destination.HOME
    val selectedDestination = rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    val context = LocalContext.current

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    if (!destination.showInBottomBar) return@forEachIndexed
                    NavigationBarItem(
                        selected = selectedDestination.intValue == index,
                        onClick = {
                            navController.navigate(route = getString(context, destination.route))
                            selectedDestination.intValue = index
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = getString(
                                    LocalContext.current,
                                    destination.route
                                )
                            )
                        },
                        label = { Text(getString(LocalContext.current, destination.route)) }
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