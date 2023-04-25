package ru.mobileup.coinroad.widget

import android.content.Context
import androidx.lifecycle.viewModelScope
import me.aartikov.sesame.loading.simple.FlowLoading
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.property.command
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.domain.usecase.graph.LoadGraphsInteractor
import ru.mobileup.coinroad.domain.usecase.settings.GetSettingsInteractor
import ru.mobileup.coinroad.domain.usecase.settings.WidgetHelpInteractor
import ru.mobileup.coinroad.domain.usecase.widget.EditWidgetsInteractor
import ru.mobileup.coinroad.navigation.system.Close
import ru.mobileup.coinroad.ui.base.BaseViewModel
import ru.mobileup.coinroad.ui.dashboard.content.GraphsContent

class WidgetActivityViewModel(
    private val getSettingsInteractor: GetSettingsInteractor,
    private val widgetHelpInteractor: WidgetHelpInteractor,
    private val loadGraphsInteractor: LoadGraphsInteractor,
    private val editWidgetsInteractor: EditWidgetsInteractor
) : BaseViewModel() {

    private val graphLoading = FlowLoading(
        scope = viewModelScope,
        load = loadGraphsInteractor::load,
        observe = loadGraphsInteractor::observe
    )
    private val graphsState by stateFromFlow(graphLoading.stateFlow)

    private var currentFilter by state("")

    val onCloseApp = command<Unit>()

    val onWidgetHelpShown = command<Boolean>()

    val filteredGraphState by computed(this::graphsState, this::currentFilter) { state, filter ->
        when (state) {
            is Loading.State.Data -> {
                val filteredGraphs =
                    state.data.filter { it.currencyPair.id.contains(filter, ignoreCase = true) }
                if (filteredGraphs.isEmpty()) {
                    GraphsContent.Empty
                } else GraphsContent.Data(filteredGraphs, state.refreshing)
            }

            is Loading.State.Loading -> GraphsContent.Loading

            else -> GraphsContent.Cancel
        }
    }

    init {
        graphLoading.handleErrors(viewModelScope) { handleException(it.throwable) }

        safeLaunch {
            onWidgetHelpShown(!getSettingsInteractor.load().isWidgetHelpShown)
        }
    }

    override fun onActive() {
        super.onActive()

        graphLoading.load(false)
    }

    fun onCloseInfoClick() = safeLaunch {
        onWidgetHelpShown(false)
        widgetHelpInteractor.execute()
    }

    fun onSearchInput(filter: String) {
        currentFilter = filter
    }

    fun onGraphClicked(graph: Graph, id: Int) = safeLaunch {
        editWidgetsInteractor.createWidget(id, graph.exchange, graph.currencyPair)
        onCloseApp(Unit)
    }

    fun onClose(context: Context) = safeLaunch {
        WidgetProvider.scheduleUpdJob(context, true)
        navigate(Close)
    }
}