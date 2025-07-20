package dev.lumentae.logkeepr.screen.project.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorPicker(
    color: MutableState<String>,
    colorError: MutableState<Boolean>
) {
    Row()
    {
        Box(
            modifier = Modifier
                .size(40.dp)
                .offset(y = 24.dp, x = 12.dp)
                .background(
                    color = try {
                        Color(android.graphics.Color.parseColor(color.value))
                    } catch (_: Exception) {
                        Color.White
                    },
                    shape = RoundedCornerShape(8.dp)
                )
        )
        TextField(
            value = color.value,
            onValueChange = {
                if (!it.startsWith("#")) {
                    color.value = "#"
                    return@TextField
                }
                if (it.length > 7)
                    return@TextField

                color.value = it
                colorError.value =
                    !Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$").matches(it)
            },
            isError = !Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$").matches(color.value),
            label = { Text("Color (Hex)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
        )
    }
}