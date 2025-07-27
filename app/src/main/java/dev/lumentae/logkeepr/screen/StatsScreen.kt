package dev.lumentae.logkeepr.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.CartesianDrawingContext
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.Position
import dev.lumentae.logkeepr.R
import dev.lumentae.logkeepr.data.database.DatabaseManager
import dev.lumentae.logkeepr.screen.components.DefaultPageTemplate
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.max

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(modifier: Modifier) {
    val entries = DatabaseManager.getAllEntries().collectAsState()

    val now = System.currentTimeMillis()
    val days = List(7) { offset ->
        val millis = now - (6 - offset) * 24 * 60 * 60 * 1000L
        val date = Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        date
    }

    val modelProducer = remember { CartesianChartModelProducer() }
    val entriesPerDay = days.map { day ->
        val count = entries.value.count { entry ->
            Instant.ofEpochMilli(entry.timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDate() == day
        }
        count
    }

    val formatter = DateTimeFormatter.ofPattern("EEE")
    val xLabels = days.map { it.format(formatter) }

    DefaultPageTemplate(getString(LocalContext.current, R.string.stats), modifier) {
        Card(
            modifier = Modifier.fillMaxSize(),
            elevation = CardDefaults.cardElevation(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                LaunchedEffect(Unit) {
                    modelProducer.runTransaction {
                        lineSeries {
                            val x = (0..6).map { it.toFloat() }
                            val y = entriesPerDay.map { it.toFloat() }
                            series(x, y)
                        }
                    }
                }
                CartesianChartHost(
                    rememberCartesianChart(
                        rememberLineCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(
                            itemPlacer = remember {
                                SingularItemPlacer()
                            }
                        ),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            valueFormatter = { _, value, _ ->
                                xLabels.getOrNull(value.toInt()) ?: ""
                            }
                        ),

                        ),
                    modelProducer,
                )
            }
        }
    }
}

class SingularItemPlacer : VerticalAxis.ItemPlacer {
    override fun getLabelValues(
        context: CartesianDrawingContext,
        axisHeight: Float,
        maxLabelHeight: Float,
        position: Axis.Position.Vertical
    ): List<Double> = getWidthMeasurementLabelValues(context, axisHeight, maxLabelHeight, position)

    override fun getWidthMeasurementLabelValues(
        context: CartesianMeasuringContext,
        axisHeight: Float,
        maxLabelHeight: Float,
        position: Axis.Position.Vertical
    ): List<Double> {
        val labelValues = mutableListOf<Double>()
        for (i in 0 until maxLabelHeight.toInt()) {
            labelValues.add(i.toDouble())
        }
        return labelValues
    }

    override fun getHeightMeasurementLabelValues(
        context: CartesianMeasuringContext,
        position: Axis.Position.Vertical
    ): List<Double> {
        val yRange = context.ranges.getYRange(position)
        return listOf(yRange.minY, (yRange.minY + yRange.maxY) / 2, yRange.maxY)
    }

    override fun getTopLayerMargin(
        context: CartesianMeasuringContext,
        verticalLabelPosition: Position.Vertical,
        maxLabelHeight: Float,
        maxLineThickness: Float,
    ) =
        when (verticalLabelPosition) {
            Position.Vertical.Top -> maxLabelHeight + maxLineThickness / 2
            Position.Vertical.Center -> (max(maxLabelHeight, maxLineThickness) +
                    maxLineThickness
                    / 2)

            else -> maxLineThickness
        }

    override fun getBottomLayerMargin(
        context: CartesianMeasuringContext,
        verticalLabelPosition: Position.Vertical,
        maxLabelHeight: Float,
        maxLineThickness: Float,
    ): Float =
        when (verticalLabelPosition) {
            Position.Vertical.Top -> maxLineThickness
            Position.Vertical.Center -> (max(
                maxLabelHeight,
                maxLineThickness
            ) + maxLineThickness) / 2

            else -> maxLabelHeight + maxLineThickness / 2
        }
}