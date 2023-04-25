package ru.mobileup.coinroad.ui.graph_creating

import com.github.ajalt.timberkt.d
import kotlinx.coroutines.CoroutineScope
import me.aartikov.sesame.loading.simple.*
import me.aartikov.sesame.property.PropertyHost
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.stateFromFlow
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.domain.usecase.graph.LoadGraphsInteractor
import ru.mobileup.coinroad.ui.graph_creating.PreviewContent.Empty
import ru.mobileup.coinroad.ui.graph_creating.PreviewContent.PreviewGraph

class GraphPreview(
    override val propertyHostScope: CoroutineScope,
    private val loadGraphsInteractor: LoadGraphsInteractor
) : PropertyHost {

    private var graph: Graph? = null

    private val previewLoading = OrdinaryLoading(
        scope = propertyHostScope,
        load = { graph?.let { loadGraphsInteractor.previewGraph(it) } }
    )
    private val previewState by stateFromFlow(previewLoading.stateFlow)

    val graphPreview by computed(::previewState) { state ->
        return@computed when (state) {
            is Loading.State.Error, Loading.State.Empty -> {
                this.graph?.let { PreviewGraph(it) } ?: Empty
            }
            is Loading.State.Data -> PreviewGraph(state.data)
            else -> Empty
        }
    }

    init {
        previewLoading.refresh()
        previewLoading.handleErrors(propertyHostScope) { d { "Graph preview error: $it" } }
    }

    fun onRefresh() = previewLoading.restart()

    fun setGraphPreview(previewGraph: Graph) {
        this.graph = previewGraph
        onRefresh()
    }
}
