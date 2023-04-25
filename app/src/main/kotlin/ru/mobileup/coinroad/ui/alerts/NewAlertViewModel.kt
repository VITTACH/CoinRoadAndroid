package ru.mobileup.coinroad.ui.alerts

import androidx.lifecycle.viewModelScope
import me.aartikov.sesame.loading.simple.FlowLoading
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.property.command
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import ru.mobileup.coinroad.domain.common.Alert
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.domain.usecase.alert.EditAlertsInteractor
import ru.mobileup.coinroad.domain.usecase.alert.LoadAlertsInteractor
import ru.mobileup.coinroad.domain.usecase.graph.LoadGraphsInteractor
import ru.mobileup.coinroad.navigation.system.Back
import ru.mobileup.coinroad.ui.alerts.content.AlertsContent
import ru.mobileup.coinroad.ui.alerts.content.GraphDataItemContent
import ru.mobileup.coinroad.ui.base.BaseViewModel
import ru.mobileup.coinroad.ui.graph.toGraphItem
import ru.mobileup.coinroad.ui.graph_creating.GraphPreview
import ru.mobileup.coinroad.ui.graph_creating.PreviewContent
import ru.mobileup.coinroad.util.calculatePercentDifference
import ru.mobileup.coinroad.util.formatNumber
import ru.mobileup.coinroad.util.getTickerPrice
import java.math.BigDecimal

class NewAlertViewModel(
    private val data: NewAlertData,
    private val editAlertsInteractor: EditAlertsInteractor,
    loadAlertsInteractor: LoadAlertsInteractor,
    loadGraphsInteractor: LoadGraphsInteractor
) : BaseViewModel() {

    private data class PriceState(
        val value: BigDecimal = BigDecimal.ZERO,
        val time: Long = System.currentTimeMillis()
    )

    private val alertLoading = FlowLoading(
        scope = viewModelScope,
        load = loadAlertsInteractor::load,
        observe = loadAlertsInteractor::observe
    )
    private val alertsState by stateFromFlow(alertLoading.stateFlow)

    private var currentFilter by state("")

    val filteredAlertState by computed(this::alertsState, this::currentFilter) { state, filter ->
        when (state) {
            is Loading.State.Data -> {
                val filterPrice = if (filter.isEmpty()) BigDecimal.ZERO else filter.toBigDecimal()
                val filteredAlerts = state.data.filter {
                    it.exchange == data.exchange && it.currencyPair == data.currencyPair && it.price >= filterPrice
                }
                if (filteredAlerts.isEmpty()) {
                    AlertsContent.Empty(filter.isNotEmpty())
                } else AlertsContent.Data(filteredAlerts, state.refreshing)
            }

            is Loading.State.Loading -> AlertsContent.Loading

            else -> AlertsContent.Empty()
        }
    }

    private val graphPreview = GraphPreview(viewModelScope, loadGraphsInteractor)

    private var price: PriceState by state(PriceState())

    var initPrice: BigDecimal by state(BigDecimal.ZERO)
        private set

    val onPriceChanged = command<BigDecimal?>()

    val pricePercentChange by computed(this::price, this::initPrice) { newPrice, oldPrice ->
        formatNumber(calculatePercentDifference(newPrice.value, oldPrice), 0)
    }

    val previewGraphContent by computed(graphPreview::graphPreview) { state ->
        if (state is PreviewContent.PreviewGraph) {
            state.graph.graphData?.let {
                val dataItem = state.graph.toGraphItem()
                initPrice = BigDecimal(dataItem.candlestickSeries.getTickerPrice())
                price = PriceState(initPrice)
                onPriceChanged.invoke(initPrice)
                return@computed GraphDataItemContent.Data(dataItem)
            }
        }
        GraphDataItemContent.Empty
    }

    init {
        graphPreview.setGraphPreview(Graph("0", data.exchange, data.currencyPair))

        alertLoading.handleErrors(viewModelScope) { handleException(it.throwable) }
    }

    override fun onActive() {
        super.onActive()

        alertLoading.load(false)
    }

    override fun navigateBack() {
        navigate(data.onBackNavMessage)
    }

    fun onCurrencyPairClicked() {
        navigate(Back)
    }

    fun onRefresh() {
        graphPreview.onRefresh()
    }

    fun onSearchInput(filter: String) {
        currentFilter = filter
    }

    fun onPercentClicked(value: String) {
        price = if (value.contains("-") || value.contains("+")) {
            PriceState(price.value + initPrice * BigDecimal(value).divide(BigDecimal("100")))
        } else {
            PriceState(value.toBigDecimal())
        }
        onPriceChanged.invoke(price.value)
    }

    fun onAlertUpdated(alert: Alert) = safeLaunch {
        editAlertsInteractor.updateAlert(alert)
    }

    fun onAlertDelete(alertId: String) = safeLaunch {
        editAlertsInteractor.deleteAlert(alertId)
    }

    fun onPriceChange(price: BigDecimal) {
        this.price = PriceState(price)
    }

    fun onNewAlertClicked() = safeLaunch {
        val dataItem = (previewGraphContent as GraphDataItemContent.Data).dataItem
        editAlertsInteractor.createAlert(
            exchange = data.exchange,
            currencyPair = data.currencyPair,
            price = price.value,
            precision = dataItem.ticker.precision
        )
        // navigate(data.onBackNavMessage)
    }
}