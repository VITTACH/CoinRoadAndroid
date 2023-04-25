package ru.mobileup.coinroad.ui.graph

import com.androidplot.xy.CandlestickSeries
import ru.mobileup.coinroad.domain.common.Alert
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.domain.common.Ticker
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.ui.graph.CustomChartView.Companion.MAX_CHART_CANDLES
import ru.mobileup.coinroad.ui.graph.GraphDataItem.Companion.CANDLES_LIMIT
import ru.mobileup.coinroad.ui.graph.GraphDataItem.Companion.DEFAULT_CANDLE_SERIES

enum class ChartType(val value: String) {
    Linear("Linear"),
    CandleStick("Candle Stick")
}

data class GraphDataItem(
    val candlestickSeries: CandlestickSeries,
    val ticker: Ticker,
    val timeStep: TimeStep,
    val updateTime: Long,
    val isMinMaxVisible: Boolean,
    val isTickerVisible: Boolean,
    val chartType: ChartType,
    val alerts: List<Alert> = emptyList()
) {
    companion object {
        const val CANDLES_LIMIT = 1000

        val DEFAULT_CANDLE_SERIES: CandlestickSeries
            get() = CandlestickSeries((0..MAX_CHART_CANDLES).mapIndexed { position, _ ->
                if (position == MAX_CHART_CANDLES) {
                    CandlestickSeries.Item(1.0, 1.0, 1.0, 1.0)
                } else {
                    CandlestickSeries.Item(0.0, 0.0, 0.0, 0.0)
                }
            })

        val DEFAULT = GraphDataItem(
            candlestickSeries = DEFAULT_CANDLE_SERIES,
            ticker = Ticker.DEFAULT,
            timeStep = TimeStep.DEFAULT,
            updateTime = System.currentTimeMillis(),
            isMinMaxVisible = false,
            isTickerVisible = false,
            chartType = ChartType.CandleStick
        )
    }
}

fun Graph.toGraphItem(): GraphDataItem {
    require(graphData != null) { "Graph.graphData must not be null for preview use Graph.toPreviewGraphItem()" }
    val precision = graphData.bars.takeLast(CANDLES_LIMIT)
        .map { it.closePrice }
        .maxByOrNull {
            it.stripTrailingZeros().scale()
        }?.stripTrailingZeros()?.scale()
        ?: 0
    val candles = mutableListOf<CandlestickSeries.Item>()

    graphData.bars.takeLast(CANDLES_LIMIT).forEach {
        val lowPrice = it.lowPrice.toDouble()
        val highPrice = it.highPrice.toDouble()
        val openPrice = it.openPrice.toDouble()
        val closePrice = it.closePrice.toDouble()
        candles.add(CandlestickSeries.Item(lowPrice, highPrice, openPrice, closePrice))
    }

    return GraphDataItem(
        candlestickSeries = CandlestickSeries(candles),
        ticker = graphData.ticker.copy(precision = precision, exchange = exchange),
        timeStep = timeStep,
        updateTime = updateTime,
        isMinMaxVisible = isMinMaxVisible,
        isTickerVisible = isTickerVisible,
        chartType = chartType,
        alerts = if (isAlertsVisible) alerts else emptyList()
    )
}

fun Graph.toPreviewGraphItem(): GraphDataItem {
    return GraphDataItem(
        candlestickSeries = DEFAULT_CANDLE_SERIES,
        ticker = Ticker.DEFAULT.copy(exchange = exchange, currencyPair = currencyPair),
        timeStep = timeStep,
        updateTime = updateTime,
        isMinMaxVisible = isMinMaxVisible,
        isTickerVisible = isTickerVisible,
        chartType = chartType
    )
}
