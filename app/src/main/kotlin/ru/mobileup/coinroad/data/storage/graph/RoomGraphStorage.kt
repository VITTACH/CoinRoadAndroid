package ru.mobileup.coinroad.data.storage.graph

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.mobileup.coinroad.data.storage.alert.AlertDao
import ru.mobileup.coinroad.data.storage.alert.entity.toDomain
import ru.mobileup.coinroad.data.storage.graph.entity.toDb
import ru.mobileup.coinroad.data.storage.graph.entity.toDomain
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.domain.common.GraphData

class RoomGraphStorage(
    private val graphDao: GraphDao,
    private val alertDao: AlertDao
) : GraphStorage {

    // GraphData is stored in memory because it becomes obsolete very quickly
    private val graphsDataFlow = MutableStateFlow<Map<String, GraphData>>(emptyMap())

    // for graphsDataFlow modifications
    private val mutex = Mutex()

    override fun observeGraphs(): Flow<List<Graph>> {
        return graphDao.observeGraphs()
            .combine(graphsDataFlow) { db, graphsData -> db to graphsData }
            .combine(alertDao.observeAlerts()) { data, alerts ->
                val (dbGraphs, graphsData) = data
                dbGraphs.map { graph ->
                    val alerts = alerts.filter {
                        it.exchange == graph.exchange && it.currencyPair == graph.currencyPair && it.isActive
                    }.map { it.toDomain() }
                    graph.toDomain(graphsData[graph.id], alerts)
                }
            }
    }

    override suspend fun deleteGraph(graphId: String) {
        graphDao.deleteGraph(graphId)
        mutex.withLock {
            graphsDataFlow.value = graphsDataFlow.value.toMutableMap().apply {
                remove(graphId)
            }
        }
    }

    override suspend fun saveGraphsData(graphs: List<Graph>) {
        mutex.withLock {
            graphsDataFlow.value = graphsDataFlow.value.toMutableMap().apply {
                graphs.forEach { graph -> updateGraphData(graph) }
            }
        }
    }

    override suspend fun saveGraph(graph: Graph) {
        graphDao.insertGraph(graph.toDb())
        mutex.withLock {
            graphsDataFlow.value = graphsDataFlow.value.toMutableMap().apply {
                updateGraphData(graph)
            }
        }
    }

    private fun MutableMap<String, GraphData>.updateGraphData(graph: Graph) {
        if (graph.graphData != null) {
            put(graph.id, graph.graphData)
        } else {
            remove(graph.id)
        }
    }
}