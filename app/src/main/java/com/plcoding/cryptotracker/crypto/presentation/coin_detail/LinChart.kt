package com.plcoding.cryptotracker.crypto.presentation.coin_detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LineChart(
    dataPoints: List<DataPoint>,
    style: ChartStyle,
    visibleDataPointsIndices: IntRange,
    unit: String,
    selectedDataPoint: DataPoint? = null,
    onSelectedDataPoint: (DataPoint) -> Unit = {},
    onXLabelWidthChange: (Float) -> Unit = {},
    modifier: Modifier = Modifier
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
        modifier = modifier
            .fillMaxSize()
    ) {
       val minLabelSpacingY = style.minYLabelSpacing.roundToPx()
       val verticalPaddingPx = style.verticalPadding.roundToPx()
       val horizontalPaddingPx = style.horizontalPadding.roundToPx()
       val xAxisLabelSpacingPx = style.xAxisLabelSpacing.roundToPx()







    }


}


@Preview
@Composable
private fun LineChatPreview() {

}