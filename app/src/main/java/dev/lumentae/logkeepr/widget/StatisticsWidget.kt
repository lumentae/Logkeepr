package dev.lumentae.logkeepr.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.lumentae.logkeepr.R
import dev.lumentae.logkeepr.data.database.DatabaseManager
import dev.lumentae.logkeepr.screen.project.utils.formatDurationToString

class StatisticsWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                if (!DatabaseManager.databaseInitialized) {
                    DatabaseManager.initDatabase(context)
                }
                Content(context)
            }
        }
    }

    override val sizeMode = SizeMode.Exact

    @Composable
    private fun Content(context: Context) {
        val entries = DatabaseManager.getAllEntries().collectAsState()
        val projects = DatabaseManager.getAllProjects().collectAsState()
        val totalTime = entries.value.sumOf { it.timeSpent }

        val resources = context.resources

        LazyColumn(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background),
        ) {
            item {
                Box(
                    modifier = GlanceModifier.fillMaxWidth()
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
                        modifier = GlanceModifier.padding(16.dp),
                        style = TextStyle(
                            color = GlanceTheme.colors.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}