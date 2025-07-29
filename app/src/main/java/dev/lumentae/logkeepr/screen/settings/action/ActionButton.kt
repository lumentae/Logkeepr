@file:Suppress("UNCHECKED_CAST")

package dev.lumentae.logkeepr.screen.settings.action

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ActionButton(
    text: Any,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Start,
    ) {
        if (text is String) {
            Text(
                text,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
            )
        } else if (text is MutableState<*>) {
            text as MutableState<String>
            Text(
                text.value,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
            )
        }
    }
}