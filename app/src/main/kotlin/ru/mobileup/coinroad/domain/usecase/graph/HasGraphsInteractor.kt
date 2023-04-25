package ru.mobileup.coinroad.domain.usecase.graph

import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.data.storage.graph.GraphStorage

class HasGraphsInteractor(
    private val graphStorage: GraphStorage
) {

    suspend operator fun invoke(): Boolean {
        val graphs = graphStorage.observeGraphs().first()
        return graphs.isNotEmpty()
    }
}