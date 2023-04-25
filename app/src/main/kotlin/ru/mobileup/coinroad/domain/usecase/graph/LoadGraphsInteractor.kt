package ru.mobileup.coinroad.domain.usecase.graph

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.supervisorScope
import ru.mobileup.coinroad.data.gateway.currency.CurrencyGatewayProvider
import ru.mobileup.coinroad.data.gateway.ticker.TickerGatewayProvider
import ru.mobileup.coinroad.data.gateway.time.TimeGateway
import ru.mobileup.coinroad.data.storage.graph.GraphStorage
import ru.mobileup.coinroad.domain.common.BarComposer
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.domain.common.GraphData

class LoadGraphsInteractor(
    private val graphStorage: GraphStorage,
    private val currencyGatewayProvider: CurrencyGatewayProvider,
    private val tickerGatewayProvider: TickerGatewayProvider,
    private val barComposer: BarComposer,
    private val timeGateway: TimeGateway
) {

    suspend fun load(fresh: Boolean = false): List<Graph> = coroutineScope {
        val graphs = graphStorage.observeGraphs().first()
        val freshGraphs = graphs.map {
            async { getFreshGraph(it) }
        }.awaitAll()

        graphStorage.saveGraphsData(freshGraphs)
        return@coroutineScope freshGraphs
    }

    fun observe(): Flow<List<Graph>> = graphStorage.observeGraphs()

    suspend fun previewGraph(graph: Graph): Graph = getFreshGraph(graph, isPreview = true)

    private suspend fun getFreshGraph(
        graph: Graph,
        isPreview: Boolean = false
    ): Graph = supervisorScope {
        try {
            val currencyGateway = currencyGatewayProvider.getCurrencyGateway(graph.exchange.id)
            val tickerGateway = tickerGatewayProvider.getTickerGateway(graph.exchange.id)
            val deferredTicker = async { tickerGateway.getTicker(graph.currencyPair) }
            val deferredTrades = async { currencyGateway.getTrades(graph.currencyPair) }
            val deferredBars = async { currencyGateway.getBars(graph.currencyPair, graph.timeStep) }
            val ticker = deferredTicker.await()
            var bars = deferredBars.await()
            if (bars.isEmpty()) {
                val trades = deferredTrades.await()
                bars = barComposer.compose(trades, graph.timeStep, timeGateway.currentTime)
            }
            val graphData = GraphData(ticker, bars)
            val currentTime = System.currentTimeMillis()
            graph.copy(graphData = graphData).let {
                if (currentTime - it.updateTime >= it.timeStep.duration.inMilliseconds) {
                    it.copy(updateTime = currentTime).also {
                        if (!isPreview) {
                            graphStorage.saveGraph(it)
                        }
                    }
                } else it
            }
        } catch (e: Exception) {
            graph
        }
    }
}