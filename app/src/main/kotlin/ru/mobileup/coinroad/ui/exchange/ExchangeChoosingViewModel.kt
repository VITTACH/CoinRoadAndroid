package ru.mobileup.coinroad.ui.exchange

import androidx.lifecycle.viewModelScope
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.property.stateFromFlow
import ru.mobileup.coinroad.domain.common.Exchange
import ru.mobileup.coinroad.domain.usecase.exchange.LoadExchangesInteractor
import ru.mobileup.coinroad.navigation.graphs.OpenGraphCreateScreen
import ru.mobileup.coinroad.navigation.system.OpenAboutScreen
import ru.mobileup.coinroad.navigation.system.OpenSelectExchangeDialog
import ru.mobileup.coinroad.ui.base.BaseViewModel

class ExchangeChoosingViewModel(
    private val loadExchangesInteractor: LoadExchangesInteractor
) : BaseViewModel() {

    private val loading = OrdinaryLoading(viewModelScope, load = loadExchangesInteractor::execute)
    val exchangesState by stateFromFlow(loading.stateFlow)

    init {
        loading.load(false)
    }

    fun onDialogExchangeClicked(exchange: Exchange) {
        navigate(OpenGraphCreateScreen(exchange))
    }

    fun onMoreExchangesClicked() {
        navigate(OpenSelectExchangeDialog { navigate(OpenGraphCreateScreen(it)) })
    }

    fun onAboutClicked() {
        navigate(OpenAboutScreen)
    }
}