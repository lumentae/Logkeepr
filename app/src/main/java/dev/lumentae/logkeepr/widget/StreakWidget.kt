package dev.lumentae.logkeepr.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.lumentae.logkeepr.R
import dev.lumentae.logkeepr.data.database.DatabaseManager

class StreakWidget : GlanceAppWidget() {
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
        val streakDays = DatabaseManager.getAllStreaks()
            .collectAsState().value.count() - 1 // Exclude the current streak day

        val resources = context.resources

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                resources.getQuantityString(R.plurals.home_streak, streakDays, streakDays),
                modifier = GlanceModifier.padding(16.dp),
                style = TextStyle(
                    color = GlanceTheme.colors.onBackground,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}