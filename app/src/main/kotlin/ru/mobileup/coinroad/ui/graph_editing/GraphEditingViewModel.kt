package ru.mobileup.coinroad.ui.graph_editing

import androidx.lifecycle.viewModelScope
import me.aartikov.sesame.dialog.DialogControl
import me.aartikov.sesame.loading.simple.FlowLoading
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.loading.simple.mapData
import me.aartikov.sesame.property.autorun
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import ru.mobileup.coinroad.analytics.AnalyticsManager
import ru.mobileup.coinroad.analytics.push.ReportPushRemove
import ru.mobileup.coinroad.domain.advert.AdvertData
import ru.mobileup.coinroad.domain.analytics.PushModel
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.domain.usecase.advert.LoadAdvertsInteractor
import ru.mobileup.coinroad.domain.usecase.alert.EditAlertsInteractor
import ru.mobileup.coinroad.domain.usecase.alert.LoadAlertsInteractor
import ru.mobileup.coinroad.domain.usecase.billing.LoadAdsPricesInteractor
import ru.mobileup.coinroad.domain.usecase.billing.LoadAdsPurchaseInteractor
import ru.mobileup.coinroad.domain.usecase.graph.EditGraphsInteractor
import ru.mobileup.coinroad.domain.usecase.graph.LoadGraphsInteractor
import ru.mobileup.coinroad.navigation.alerts.OpenNewAlertScreen
import ru.mobileup.coinroad.navigation.system.Back
import ru.mobileup.coinroad.navigation.system.OpenMainScreen
import ru.mobileup.coinroad.ui.alerts.NewAlertData
import ru.mobileup.coinroad.ui.base.BaseViewModel
import ru.mobileup.coinroad.ui.graph.ChartType
import ru.mobileup.coinroad.ui.graph_creating.GraphPreview
import ru.mobileup.coinroad.util.system.dataOrNull

class GraphEditingViewModel(
    private val graph: Graph,
    private val analyticsManager: AnalyticsManager,
    private val editGraphsInteractor: EditGraphsInteractor,
    private val loadAdvertsInteractor: LoadAdvertsInteractor,
    private val loadAdsPricesInteractor: LoadAdsPricesInteractor,
    private val loadAdsPurchaseInteractor: LoadAdsPurchaseInteractor,
    private val editAlertsInteractor: EditAlertsInteractor,
    private val loadAlertsInteractor: LoadAlertsInteractor,
    loadGraphsInteractor: LoadGraphsInteractor
) : BaseViewModel() {

    private data class DrawingSettings(
        val isMinMaxVisible: Boolean,
        val isTickerVisible: Boolean,
        val isAlertsVisible: Boolean
    )

    private val graphPreview = GraphPreview(viewModelScope, loadGraphsInteractor)
    private val graphState by state(graph)
    var timeStepState by state(graph.timeStep)
        private set

    private val alertsLoading = FlowLoading(
        scope = viewModelScope,
        load = loadAlertsInteractor::load,
        observe = loadAlertsInteractor::observe
    )
    private val alertsState by stateFromFlow(alertsLoading.stateFlow)

    private val adsPurchaseLoading = FlowLoading(
        scope = viewModelScope,
        load = loadAdsPurchaseInteractor::load,
        observe = loadAdsPurchaseInteractor::observe
    )
    private val adsPurchaseSate by stateFromFlow(adsPurchaseLoading.stateFlow)

    private val adsPricesLoading = FlowLoading(
        scope = viewModelScope,
        load = loadAdsPricesInteractor::load,
        observe = loadAdsPricesInteractor::observe
    )
    private val adsPricesState by stateFromFlow(adsPricesLoading.stateFlow)

    private val advertsLoading = FlowLoading(
        scope = viewModelScope,
        load = loadAdvertsInteractor::load,
        observe = loadAdvertsInteractor::observe
    )
    private val advertsState by stateFromFlow(advertsLoading.stateFlow)

    val advertsData by computed(
        ::advertsState,
        ::adsPricesState,
        ::adsPurchaseSate
    ) { adverts, prices, purchase ->
        adverts.mapData { AdvertData(it, prices.dataOrNull, purchase.dataOrNull) }
    }

    private val alerts by computed(::alertsState) {
        it.dataOrNull?.filter {
            it.exchange == graph.exchange && it.currencyPair == graph.currencyPair && it.isActive
        } ?: emptyList()
    }

    val deleteDialog = DialogControl<Unit, Unit>()

    private var isFullScreen = false

    private var isMinMaxVisible by state(graph.isMinMaxVisible)

    private var isTickerVisible by state(graph.isTickerVisible)

    private var isAlertsVisible by state(graph.isAlertsVisible)

    var chartType by state(graph.chartType)
        private set

    private val drawingSettings by computed(
        this::isMinMaxVisible,
        this::isTickerVisible,
        this::isAlertsVisible
    ) { isMinMaxVisible, isTickerVisible, isAlertsVisible ->
        DrawingSettings(isMinMaxVisible, isTickerVisible, isAlertsVisible)
    }

    init {
        alertsLoading.handleErrors(viewModelScope) { handleException(it.throwable) }
        advertsLoading.handleErrors(viewModelScope) { handleException(it.throwable) }
        adsPricesLoading.handleErrors(viewModelScope) { handleException(it.throwable) }
        adsPurchaseLoading.handleErrors(viewModelScope) { handleException(it.throwable) }

        autorun(
            this::timeStepState,
            this::drawingSettings,
            this::chartType,
            this::alerts
        ) { timeStep, drawingSettings, chartType, alerts ->
            graphPreview.setGraphPreview(
                graph.copy(
                    timeStep = timeStep,
                    isMinMaxVisible = drawingSettings.isMinMaxVisible,
                    isTickerVisible = drawingSettings.isTickerVisible,
                    isAlertsVisible = drawingSettings.isAlertsVisible,
                    chartType = chartType,
                    alerts = alerts
                )
            )
        }
    }

    override fun onActive() {
        super.onActive()

        alertsLoading.load(false)
        advertsLoading.load(false)
        adsPricesLoading.load(false)
        adsPurchaseLoading.load(false)
    }

    val previewGraphContent by computed(graphPreview::graphPreview) { it }

    val editButtonVisible by computed(
        this::graphState,
        this::timeStepState,
        this::drawingSettings,
        this::chartType
    ) { graphState, timeStep, drawingSettings, chartType ->
        graphState.chartType != chartType
                || graphState.isMinMaxVisible != drawingSettings.isMinMaxVisible
                || graphState.isTickerVisible != drawingSettings.isTickerVisible
                || graphState.isAlertsVisible != drawingSettings.isAlertsVisible
                || graphState.timeStep != timeStep
    }

    fun onDeleteButtonClicked() = safeLaunch {
        deleteDialog.showForResult(Unit)?.let {
            editGraphsInteractor.deleteGraph(graph.id)
            alerts.forEach { editAlertsInteractor.deleteAlert(it.id) }
            sendRemoveEvent()
            navigate(Back)
        }
    }

    fun onEditButtonClicked() = safeLaunch {
        val updatedGraph = graph.copy(
            timeStep = timeStepState,
            isMinMaxVisible = isMinMaxVisible,
            isTickerVisible = isTickerVisible,
            isAlertsVisible = isAlertsVisible,
            chartType = chartType
        )
        editGraphsInteractor.updateGraph(updatedGraph)
        navigate(OpenMainScreen)
    }

    fun onTimeToggleButtonClicked(timeStep: TimeStep) {
        timeStepState = timeStep
    }

    fun onMinMaxChanged() {
        isMinMaxVisible = !isMinMaxVisible
    }

    fun onAlertsChanged() {
        isAlertsVisible = !isAlertsVisible
    }

    fun onTickerChanged() {
        isTickerVisible = !isTickerVisible
    }

    fun onChartTypeChanged(chartType: ChartType) {
        this.chartType = chartType
    }

    fun onNewAlertClicked() {
        with(graph) {
            navigate(OpenNewAlertScreen(NewAlertData(exchange, currencyPair)))
        }
    }

    fun getNewChartMode(): Boolean {
        isFullScreen = !isFullScreen
        return isFullScreen
    }

    private fun sendRemoveEvent() {
        analyticsManager.handleAnalyticMessage(ReportPushRemove(
            with(graph) {
                PushModel(currencyPair, exchange, timeStep)
            }
        ))
    }
}