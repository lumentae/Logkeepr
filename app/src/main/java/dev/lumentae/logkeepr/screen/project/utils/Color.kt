package dev.lumentae.logkeepr.screen.project.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import androidx.core.graphics.toColorInt
import dev.lumentae.logkeepr.R
import kotlin.math.pow

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
                        Color(color.value.toColorInt())
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
            label = { Text(getString(LocalContext.current, R.string.color_hex)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true,
        )
    }
}

fun contrastRatio(color1: Color, color2: Color): Double {
    fun luminance(c: Color): Double {
        fun channel(v: Float): Double {
            val d = v.toDouble()
            return if (d <= 0.03928) d / 12.92 else ((d + 0.055) / 1.055).pow(2.4)
        }

        val r = channel(color1.red)
        val g = channel(color1.green)
        val b = channel(color1.blue)

        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    val lum1 = luminance(color1) + 0.05
    val lum2 = luminance(color2) + 0.05

    return if (lum1 > lum2) lum1 / lum2 else lum2 / lum1
}

@Composable
fun bestTextColor(background: Color): Color {
    val whiteContrast = contrastRatio(background, Color.White)
    val blackContrast = contrastRatio(background, Color.Black)
    return if (whiteContrast >= blackContrast)
        MaterialTheme.colorScheme.onSurfaceVariant
    else
        MaterialTheme.colorScheme.inverseOnSurface
}
