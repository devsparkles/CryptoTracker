package com.plcoding.cryptotracker.crypto.presentation.coin_detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.plcoding.cryptotracker.crypto.domain.CoinPrice
import com.plcoding.cryptotracker.ui.theme.CryptoTrackerTheme
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun LineChart(
    dataPoints: List<DataPoint>,
    style: ChartStyle,
    visibleDataPointsIndices: IntRange,
    unit: String,
    selectedDataPoint: DataPoint? = null,
    onSelectedDataPoint: (DataPoint) -> Unit = {},
    onXLabelWidthChange: (Float) -> Unit = {},
    modifier: Modifier = Modifier,
    showHelperLines: Boolean = true
) {
    val textStyle = LocalTextStyle.current.copy(
        fontSize = style.labelFontSize
    )

    val visibleDataPoints = remember(dataPoints, visibleDataPointsIndices) {
        dataPoints.slice(visibleDataPointsIndices)
    }

    val maxYValue = remember(visibleDataPoints) {
        visibleDataPoints.maxOfOrNull { it.y } ?: 0f
    }

    val minYValue = remember(visibleDataPoints) {
        visibleDataPoints.minOfOrNull { it.y } ?: 0f
    }

    val measurer = rememberTextMeasurer()

    var xLabelWitdh by remember {
        mutableFloatStateOf(0f)
    }

    LaunchedEffect(key1 = xLabelWitdh) {
        onXLabelWidthChange(xLabelWitdh)
    }

    val selectedDataPointIndex = remember(selectedDataPoint) {
        dataPoints.indexOf(selectedDataPoint)
    }

    var drawPoints by remember {
        mutableStateOf(listOf<DataPoint>())
    }

    var isShowingDataPoints by remember {
        mutableStateOf(selectedDataPoint != null)
    }

    Canvas(
        modifier = modifier.fillMaxSize()
    ) {
        val minLabelSpacingY = style.minYLabelSpacing.toPx()
        val verticalPaddingPx = style.verticalPadding.toPx()
        val horizontalPaddingPx = style.horizontalPadding.toPx()
        val xAxisLabelSpacingPx = style.xAxisLabelSpacing.toPx()


        val xLabelTextLayoutResults = visibleDataPoints.map {
            measurer.measure(
                text = it.xLabel, style = textStyle.copy(textAlign = TextAlign.Center)
            )
        }

        val maxXLabelWidth = xLabelTextLayoutResults.maxOfOrNull { it.size.width } ?: 0
        val maxXLabelHeight = xLabelTextLayoutResults.maxOfOrNull { it.size.height } ?: 0
        val maxXLabelLineCount = xLabelTextLayoutResults.maxOfOrNull { it.lineCount } ?: 0
        val xLabelLineHeight = maxXLabelHeight / maxXLabelLineCount

        val viewPortHeightPx =
            size.height - (maxXLabelHeight + 2 * verticalPaddingPx + xLabelLineHeight + xAxisLabelSpacingPx)


        val viewPortTopY =
            verticalPaddingPx + xLabelLineHeight + 10f // tiny space between blue box and green box
        val viewPortRightX = size.width
        val viewPortBottomY = viewPortTopY + viewPortHeightPx
        val viewPortLeftX = (2 * horizontalPaddingPx)

        /// calculation of y axis labels and lines ////

        val labelViewPortHeightPx = viewPortHeightPx + xLabelLineHeight
        val labelCountExcludingLastLabel =
            (labelViewPortHeightPx / (xLabelLineHeight + minLabelSpacingY)).toInt()
        val valueIncrement = (maxYValue - minYValue) / labelCountExcludingLastLabel
        val yLabels = (0..labelCountExcludingLastLabel).map {
            ValueLabel(
                value = maxYValue - (valueIncrement * it), unit = unit
            )
        }
        val yLabelTextLayoutResults = yLabels.map {
            measurer.measure(
                text = it.formatted(), style = textStyle.copy(textAlign = TextAlign.Right)
            )
        }
        val heightRequiredForLabels = xLabelLineHeight * (labelCountExcludingLastLabel + 1)
        val remainingHeightForLabels = labelViewPortHeightPx - heightRequiredForLabels
        val spaceBetweenLabels = remainingHeightForLabels / labelCountExcludingLastLabel
        val maxYLabelWidth = yLabelTextLayoutResults.maxOfOrNull { it.size.width } ?: 0f


        val viewPort = Rect(
            left = viewPortLeftX + maxYLabelWidth.toFloat(),
            top = viewPortTopY,
            right = viewPortRightX,
            bottom = viewPortBottomY
        )

        // draw the viewport
        // color = style.chartLineColor,
        drawRect(
            color = Color.Green.copy(alpha = 0.3f), topLeft = viewPort.topLeft, size = viewPort.size
        )

        // draw the y axis labels and lines
        yLabelTextLayoutResults.forEachIndexed { index, result ->
            val x = horizontalPaddingPx + maxYLabelWidth.toFloat() - result.size.width.toFloat()
            val y =
                viewPortTopY + index * (xLabelLineHeight + spaceBetweenLabels) - xLabelLineHeight / 2f
            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = x,
                    y = y
                ),
                color = if (index == selectedDataPointIndex) {
                    style.selectedColor
                } else {
                    style.unselectedColor
                }
            )



            if (showHelperLines) {
                drawLine(
                    color = if (selectedDataPointIndex == index) {
                        style.selectedColor
                    } else {
                        style.unselectedColor
                    },
                    start = Offset(
                        x = viewPortLeftX + maxYLabelWidth.toFloat(),
                        y = y + result.size.height.toFloat() / 2f
                    ),
                    end = Offset(
                        x = viewPortRightX,
                        y = y + result.size.height.toFloat() / 2f
                    ),
                    strokeWidth = if (selectedDataPointIndex == index) style.helperLinesThicknessPx * 1.8f else style.helperLinesThicknessPx
                )
            }
        }


        ////


// show the viewport


        // show the line of bottom of the chart's label

        xLabelWitdh = maxXLabelWidth + xAxisLabelSpacingPx
        xLabelTextLayoutResults.forEachIndexed { index, result ->

            val x =
                maxYLabelWidth.toFloat() + viewPortLeftX + xAxisLabelSpacingPx / 2f + xLabelWitdh * index

            val y = viewPortBottomY + xAxisLabelSpacingPx
            drawText(
                textLayoutResult = result, topLeft = Offset(
                    x = x,
                    y = y
                ), color = if (index == selectedDataPointIndex) {
                    style.selectedColor
                } else {
                    style.unselectedColor
                }
            )


            if (showHelperLines) {
                drawLine(
                    color = if (selectedDataPointIndex == index) {
                        style.selectedColor
                    } else {
                        style.unselectedColor
                    },
                    start = Offset(
                        x = x + result.size.width.toFloat() / 2f,
                        y = viewPortBottomY
                    ),
                    end = Offset(
                        x = x + result.size.width.toFloat() / 2f,
                        y = viewPortTopY
                    ),
                    strokeWidth = if (selectedDataPointIndex == index) style.helperLinesThicknessPx * 1.8f else style.helperLinesThicknessPx
                )
            }



            if (selectedDataPointIndex == index) {
                val valueLabel = ValueLabel(
                    value = visibleDataPoints[index].y, unit = unit
                )
                val valueResult = measurer.measure(
                    maxLines = 1,
                    text = valueLabel.formatted(),
                    style = textStyle.copy(color = style.selectedColor)
                )
                val textPositionX = if(selectedDataPointIndex == visibleDataPointsIndices.last) {
                    x - valueResult.size.width
                } else {
                    x - valueResult.size.width / 2f
                } + result.size.width / 2f

                val isTextInVisibleRAnge =(size.width -textPositionX).roundToInt() in 0..size.width.roundToInt()
                if(isTextInVisibleRAnge){
                    drawText(
                        textLayoutResult = valueResult,
                        topLeft = Offset(
                            x = textPositionX,
                            y = viewPortTopY - valueResult.size.height - 10f
                        ),
                        color = style.selectedColor
                    )
                }
            }
        }


    }


}


@Preview(widthDp = 1000)
@Composable
private fun LineChartPreview() {
    CryptoTrackerTheme {
        val coinHistoryRandomized = remember {
            (1..20).map {
                CoinPrice(
                    priceUsd = Random.nextFloat() * 1000.0,
                    dateTime = ZonedDateTime.now().plusHours(it.toLong())
                )
            }
        }
        val style = ChartStyle(
            chartLineColor = Color.White,
            unselectedColor = Color(0xFF7C7C7C),
            selectedColor = Color.Red,
            helperLinesThicknessPx = 1f,
            axisLinesThicknessPx = 5f,
            labelFontSize = 14.sp,
            minYLabelSpacing = 25.dp,
            verticalPadding = 8.dp,
            horizontalPadding = 8.dp,
            xAxisLabelSpacing = 8.dp
        )
        val dataPoints = remember {
            coinHistoryRandomized.map {
                DataPoint(
                    x = it.dateTime.hour.toFloat(),
                    y = it.priceUsd.toFloat(),
                    xLabel = DateTimeFormatter.ofPattern("ha\nM/d").format(it.dateTime)
                )
            }
        }
        LineChart(
            dataPoints = dataPoints,
            style = style,
            visibleDataPointsIndices = 0..19,
            unit = "$",
            modifier = Modifier
                .width(700.dp)
                .height(300.dp)
                .background(Color.White),
            selectedDataPoint = dataPoints[1]
        )
    }
}