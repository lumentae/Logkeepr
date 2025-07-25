package dev.lumentae.logkeepr.screen.project.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.lumentae.logkeepr.R
import dev.lumentae.logkeepr.data.database.entity.EntryEntity
import dev.lumentae.logkeepr.screen.project.utils.formatDurationToString
import java.time.Instant
import java.time.ZoneId

@Composable
fun EntryCard(
    entry: EntryEntity,
    menu: @Composable () -> Unit,
) {
    Card(
        elevation = CardDefaults.cardElevation(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(entry.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    LocalContext.current.getString(
                        R.string.entry_stats,
                        formatDurationToString(entry.timeSpent),
                        Instant.ofEpochMilli(entry.timestamp)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate().toString()
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (entry.content.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))

                    Text(
                        entry.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            menu()
        }
    }
}