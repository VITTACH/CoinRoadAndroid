package ru.mobileup.coinroad.ui.graph_creating

import androidx.lifecycle.viewModelScope
import me.aartikov.sesame.loading.simple.*
import me.aartikov.sesame.property.*
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.analytics.AnalyticsManager
import ru.mobileup.coinroad.analytics.exchange.ReportFirstCurrencySelect
import ru.mobileup.coinroad.analytics.exchange.ReportSecondCurrencySelect
import ru.mobileup.coinroad.analytics.push.ReportMinMaxVisibility
import ru.mobileup.coinroad.analytics.push.ReportPushCreate
import ru.mobileup.coinroad.domain.advert.AdvertData
import ru.mobileup.coinroad.domain.analytics.PushModel
import ru.mobileup.coinroad.domain.common.*
import ru.mobileup.coinroad.domain.usecase.advert.LoadAdvertsInteractor
import ru.mobileup.coinroad.domain.usecase.alert.LoadAlertsInteractor
import ru.mobileup.coinroad.domain.usecase.billing.LoadAdsPricesInteractor
import ru.mobileup.coinroad.domain.usecase.billing.LoadAdsPurchaseInteractor
import ru.mobileup.coinroad.domain.usecase.currency.LoadCurrencyPairsInteractor
import ru.mobileup.coinroad.domain.usecase.graph.EditGraphsInteractor
import ru.mobileup.coinroad.domain.usecase.graph.LoadGraphsInteractor
import ru.mobileup.coinroad.navigation.alerts.OpenNewAlertScreen
import ru.mobileup.coinroad.navigation.system.BackToAlertsScreen
import ru.mobileup.coinroad.navigation.system.OpenMainScreen
import ru.mobileup.coinroad.ui.alerts.NewAlertData
import ru.mobileup.coinroad.ui.base.BaseViewModel
import ru.mobileup.coinroad.ui.base.widget.CurrencyButtonState
import ru.mobileup.coinroad.ui.graph.ChartType

class GraphCreatingViewModel(
    graphCreatingData: GraphCreatingData,
    private val analyticsManager: AnalyticsManager,
    private val loadAdvertsInteractor: LoadAdvertsInteractor,
    private val loadAdsPricesInteractor: LoadAdsPricesInteractor,
    private val loadAdsPurchaseInteractor: LoadAdsPurchaseInteractor,
    private val loadCurrencyPairsInteractor: LoadCurrencyPairsInteractor,
    private val loadAlertsInteractor: LoadAlertsInteractor,
    private val editGraphsInteractor: EditGraphsInteractor,
    loadGraphsInteractor: LoadGraphsInteractor
) : BaseViewModel() {

    val data = graphCreatingData

    private val pairsLoading = FlowLoading(
        scope = viewModelScope,
        load = { fresh: Boolean -> loadCurrencyPairsInteractor.execute(data.exchange.id, fresh) },
        observe = { loadCurrencyPairsInteractor.observe(data.exchange.id) }
    )

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

    private val alerts by computed(::alertsState) { it.dataOrNull ?: emptyList() }

    private val graphPreview = GraphPreview(viewModelScope, loadGraphsInteractor)
    private val currencyPairsState by stateFromFlow(pairsLoading.stateFlow)
    private var graphDescription by state(GraphDescription())

    private val selectedCurrencyPair by computed(::graphDescription) { state ->
        val currencyPairList = (currencyPairsState as? Loading.State.Data)?.data
        return@computed currencyPairList?.findCurrency(state.baseCurrency, state.quoteCurrency)
    }

    val timeStepState by computed(::graphDescription) { it.timeStep }
    val previewGraphContent by computed(graphPreview::graphPreview) { it }

    var isMinMaxVisible by state(false)
        private set

    var isTickerVisible by state(false)
        private set

    var isAlertsVisible by state(true)
        private set

    var chartType by state(ChartType.CandleStick)
        private set

    private var isFullScreen = false

    private var currentFilter by state("")

    val onCoinClicked = command<Unit>()

    private val chartPayload by computed(
        ::isMinMaxVisible,
        ::isTickerVisible,
        ::isAlertsVisible,
        ::timeStepState,
        ::chartType
    ) { _, _, _, _, _ -> System.currentTimeMillis() }

    init {
        pairsLoading.handleErrors(viewModelScope) { handleException(it.throwable) { onRefresh() } }
        alertsLoading.handleErrors(viewModelScope) { handleException(it.throwable) }
        advertsLoading.handleErrors(viewModelScope) { handleException(it.throwable) }
        adsPricesLoading.handleErrors(viewModelScope) { handleException(it.throwable) }
        adsPurchaseLoading.handleErrors(viewModelScope) { handleException(it.throwable) }

        autorun(this::selectedCurrencyPair) { pair ->
            if (pair != null && data.isAlert) {
                onSecondCurrencyButtonClicked()
                navigate(OpenNewAlertScreen(NewAlertData(data.exchange, pair, BackToAlertsScreen)))
            }
        }

        autorun(::selectedCurrencyPair, ::chartPayload, ::alerts) { currencyPair, _, alerts ->
            currencyPair?.let {
                val alerts = alerts.filter {
                    it.exchange == data.exchange && it.currencyPair == currencyPair && it.isActive
                }
                val preview = Graph(
                    id = "0",
                    exchange = data.exchange,
                    currencyPair = currencyPair,
                    timeStep = timeStepState,
                    isMinMaxVisible = isMinMaxVisible,
                    isTickerVisible = isTickerVisible,
                    isAlertsVisible = isAlertsVisible,
                    chartType = chartType,
                    alerts = alerts
                )
                graphPreview.setGraphPreview(preview)
            }
        }
    }

    override fun onActive() {
        super.onActive()

        pairsLoading.load(false)
        alertsLoading.load(false)
        advertsLoading.load(false)
        adsPricesLoading.load(false)
        adsPurchaseLoading.load(false)
    }

    private fun onRefresh() = pairsLoading.refresh()

    val secondCurrencyButtonText by computed(::graphDescription) {
        it.quoteCurrency?.id ?: resourceHelper.getString(R.string.creating_pair)
    }

    val secondCurrencyButtonState by computed(::graphDescription) {
        when {
            it.baseCurrency != null && it.quoteCurrency == null -> CurrencyButtonState.SELECTING
            it.quoteCurrency != null -> CurrencyButtonState.SELECTED
            else -> CurrencyButtonState.DISABLED
        }
    }

    val firstCurrencyButtonText by computed(::graphDescription) {
        it.baseCurrency?.id ?: resourceHelper.getString(R.string.creating_currency)
    }

    val firstCurrencyButtonState by computed(::graphDescription) {
        when {
            it.baseCurrency != null -> CurrencyButtonState.SELECTED
            else -> CurrencyButtonState.SELECTING
        }
    }

    fun onSecondCurrencyButtonClicked() {
        graphDescription = graphDescription.copy(quoteCurrency = null, timeStep = TimeStep.DEFAULT)
    }

    fun onFirstCurrencyButtonClicked() {
        graphDescription = GraphDescription()
    }

    val isProgressBarVisible by computed(::currencyPairsState) { it is Loading.State.Loading }

    val isGraphElementsVisible by computed(::selectedCurrencyPair) { it != null && !data.isAlert }

    val firstCurrencyListVisible by computed(::graphDescription) {
        it.baseCurrency == null && it.quoteCurrency == null
    }

    val secondCurrencyListVisible by computed(::graphDescription) {
        it.baseCurrency != null && it.quoteCurrency == null
    }

    val firstCurrencyContent by computed(
        ::currencyPairsState,
        ::currentFilter
    ) { state, filter ->
        when (state) {
            is Loading.State.Data -> {
                val filteredList = state.data.getAllBaseCurrencies().filter {
                    it.id.contains(filter, ignoreCase = true)
                }
                CurrencyContent.Data(filteredList, state.refreshing)
            }
            else -> CurrencyContent.Empty
        }
    }

    val secondCurrencyContent by computed(
        ::currencyPairsState,
        ::graphDescription,
        ::currentFilter
    ) { state, data, filter ->
        when (state) {
            is Loading.State.Data -> {
                if (data.baseCurrency != null) {
                    val filteredList = state.data.getQuoteCurrencies(data.baseCurrency).filter {
                        it.id.contains(filter, ignoreCase = true)
                    }
                    CurrencyContent.Data(filteredList, state.refreshing)
                } else {
                    CurrencyContent.Empty
                }
            }
            else -> CurrencyContent.Empty
        }
    }

    fun onAddGraphClicked() = safeLaunch {
        selectedCurrencyPair?.let { pair ->
            editGraphsInteractor.createGraph(
                exchange = data.exchange,
                currencyPair = pair,
                timeStep = graphDescription.timeStep,
                isVisible = true,
                isMinMaxVisible = isMinMaxVisible,
                isTickerVisible = isTickerVisible,
                isAlertsVisible = isAlertsVisible,
                chartType = chartType
            )
            sendCreateEvent(pair)
            navigate(OpenMainScreen)
        }
    }

    fun onFirstCurrencyItemClicked(currency: Currency) {
        onCoinClicked(Unit)
        graphDescription = graphDescription.copy(baseCurrency = currency)
        analyticsManager.handleAnalyticMessage(ReportFirstCurrencySelect(currency))
    }

    fun onSecondCurrencyItemClicked(currency: Currency) {
        onCoinClicked(Unit)
        graphDescription = graphDescription.copy(quoteCurrency = currency)
        analyticsManager.handleAnalyticMessage(ReportSecondCurrencySelect(currency))
    }

    fun onTimeToggleButtonClicked(timeStep: TimeStep) {
        graphDescription = graphDescription.copy(timeStep = timeStep)
    }

    fun onMinMaxChanged() {
        isMinMaxVisible = !isMinMaxVisible

        selectedCurrencyPair?.let {
            val pushInfo = PushModel(it, data.exchange, graphDescription.timeStep)
            if (isMinMaxVisible) {
                analyticsManager.handleAnalyticMessage(ReportMinMaxVisibility(pushInfo, true))
            } else {
                analyticsManager.handleAnalyticMessage(ReportMinMaxVisibility(pushInfo, false))
            }
        }
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
        navigate(OpenNewAlertScreen(NewAlertData(data.exchange, selectedCurrencyPair!!)))
    }

    fun getNewChartMode(): Boolean {
        isFullScreen = !isFullScreen
        return isFullScreen
    }

    fun onSearchInput(filter: String) {
        currentFilter = filter
    }

    private fun sendCreateEvent(currencyPair: CurrencyPair) {
        val pushInfo = PushModel(currencyPair, data.exchange, graphDescription.timeStep)
        analyticsManager.handleAnalyticMessage(ReportPushCreate(pushInfo))
    }
}