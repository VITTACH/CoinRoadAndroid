package ru.mobileup.coinroad.data.storage.graph

import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.domain.common.Graph

interface GraphStorage {

    fun observeGraphs(): Flow<List<Graph>>

    suspend fun saveGraphsData(graphs: List<Graph>)

    suspend fun saveGraph(graph: Graph)

    suspend fun deleteGraph(graphId: String)
}