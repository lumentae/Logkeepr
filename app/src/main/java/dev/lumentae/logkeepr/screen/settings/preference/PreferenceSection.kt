package dev.lumentae.logkeepr.screen.settings.preference

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import dev.lumentae.logkeepr.data.preferences.PreferenceSection

@Composable
fun PreferenceSection(
    title: PreferenceSection,
    content: @Composable () -> Unit
) {
    if (!title.enabled.value) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Text(
            getString(LocalContext.current, title.title),
            modifier = Modifier.padding(start = 16.dp, top = 16.dp),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = 20.sp,
            ),
            fontWeight = FontWeight.Bold
        )
        content()
    }
}