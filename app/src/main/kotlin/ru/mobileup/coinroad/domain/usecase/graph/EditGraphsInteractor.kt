package ru.mobileup.coinroad.domain.usecase.graph

import ru.mobileup.coinroad.data.storage.graph.GraphStorage
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.Exchange
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.domain.usecase.alert.LoadAlertsInteractor
import ru.mobileup.coinroad.ui.graph.ChartType

class EditGraphsInteractor(
    private val storage: GraphStorage,
    private val loadAlertsInteractor: LoadAlertsInteractor
) {

    suspend fun updateGraph(graph: Graph) = storage.saveGraph(graph)

    suspend fun deleteGraph(graphId: String) = storage.deleteGraph(graphId)

    suspend fun createGraph(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        timeStep: TimeStep,
        isVisible: Boolean,
        isMinMaxVisible: Boolean,
        isTickerVisible: Boolean,
        isAlertsVisible: Boolean,
        chartType: ChartType
    ): Graph {
        val alerts = loadAlertsInteractor.load().filter {
            it.exchange == exchange && it.currencyPair == currencyPair && it.isActive
        }
        val graph = Graph(
            id = System.currentTimeMillis().toString(),
            exchange = exchange,
            currencyPair = currencyPair,
            timeStep = timeStep,
            isVisible = isVisible,
            isMinMaxVisible = isMinMaxVisible,
            isTickerVisible = isTickerVisible,
            isAlertsVisible = isAlertsVisible,
            chartType = chartType,
            alerts = alerts
        )
        storage.saveGraph(graph)
        return graph
    }
}