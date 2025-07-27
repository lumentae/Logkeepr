package dev.lumentae.logkeepr.screen.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getString
import dev.lumentae.logkeepr.data.preferences.PreferenceSection

@Composable
fun Section(
    title: PreferenceSection,
    content: @Composable () -> Unit
) {
    if (!title.enabled.value) return

    val colors = if (title.color == Color.Unspecified) {
        CardDefaults.cardColors()
    } else if (title.color == Color.Red) {
        CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    } else {
        CardDefaults.cardColors(containerColor = title.color)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = colors
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