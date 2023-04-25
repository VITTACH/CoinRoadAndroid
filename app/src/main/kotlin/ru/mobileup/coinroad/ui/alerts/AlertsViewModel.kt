package ru.mobileup.coinroad.ui.alerts

import androidx.lifecycle.viewModelScope
import me.aartikov.sesame.loading.simple.FlowLoading
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import ru.mobileup.coinroad.domain.common.Alert
import ru.mobileup.coinroad.domain.usecase.alert.EditAlertsInteractor
import ru.mobileup.coinroad.domain.usecase.alert.LoadAlertsInteractor
import ru.mobileup.coinroad.navigation.alerts.OpenAlertCreatingScreen
import ru.mobileup.coinroad.navigation.system.OpenSelectExchangeDialog
import ru.mobileup.coinroad.ui.alerts.content.AlertsContent
import ru.mobileup.coinroad.ui.base.BaseViewModel

class AlertsViewModel(
    private val editAlertsInteractor: EditAlertsInteractor,
    loadAlertsInteractor: LoadAlertsInteractor
) : BaseViewModel() {

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
                val filteredAlerts =
                    state.data.filter { it.currencyPair.id.contains(filter, ignoreCase = true) }
                if (filteredAlerts.isEmpty()) {
                    AlertsContent.Empty(filter.isNotEmpty())
                } else {
                    AlertsContent.Data(filteredAlerts, state.refreshing)
                }
            }
            is Loading.State.Loading -> AlertsContent.Loading
            else -> AlertsContent.Empty()
        }
    }

    init {
        alertLoading.handleErrors(viewModelScope) { error -> handleException(error.throwable) }
    }

    override fun onActive() {
        super.onActive()

        alertLoading.load(false)
    }

    fun onSearchInput(filter: String) {
        currentFilter = filter
    }

    fun onAlertUpdated(alert: Alert) = safeLaunch {
        editAlertsInteractor.updateAlert(alert)
    }

    fun onAlertDelete(alertId: String) = safeLaunch {
        editAlertsInteractor.deleteAlert(alertId)
    }

    fun onNewAlertClicked() {
        navigate(OpenSelectExchangeDialog {
            navigate(OpenAlertCreatingScreen(it))
        })
    }
}