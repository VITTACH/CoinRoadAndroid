package ru.mobileup.coinroad.domain.common

import ru.mobileup.coinroad.ui.graph.ChartType
import java.io.Serializable

/**
 * An item that a user adds to monitor price of a currency pair
 *
 * @property id unique id
 * @property exchange [Exchange] associated with a currency pair
 * @property currencyPair monitored [CurrencyPair]
 * @property timeStep time step for graph displaying
 * @property graphData data to display on a graph or null if it is not loaded
 */
data class Graph(
    val id: String,
    val exchange: Exchange,
    val currencyPair: CurrencyPair,
    @Transient val graphData: GraphData? = null,
    val updateTime: Long = System.currentTimeMillis(),
    val timeStep: TimeStep = TimeStep.DEFAULT,
    val isVisible: Boolean = true,
    val deepLink: DeepLink? = null,
    val isMinMaxVisible: Boolean = false,
    val isTickerVisible: Boolean = false,
    val isAlertsVisible: Boolean = false,
    val chartType: ChartType = ChartType.CandleStick,
    val alerts: List<Alert> = emptyList()
) : Serializable

fun Graph.toAnalytic(): Map<String, String> {
    return mapOf(
        "id" to id,
        "currencyPair" to currencyPair.id,
        "exchange" to exchange.id.mName,
        "isVisible" to isVisible.toString()
    )
}

/**
 * Data for graph displaying
 * @property ticker last known [Ticker]
 * @property bars bars composed for a given time step
 */
data class GraphData(
    val ticker: Ticker,
    val bars: List<Bar>
)