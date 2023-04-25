package ru.mobileup.coinroad.ui.exchange

import androidx.lifecycle.viewModelScope
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.property.stateFromFlow
import ru.mobileup.coinroad.domain.usecase.exchange.LoadExchangesInteractor
import ru.mobileup.coinroad.ui.base.BaseViewModel

class SelectExchangeViewModel(
    loadExchangesInteractor: LoadExchangesInteractor
) : BaseViewModel() {

    private val exchangesLoading = OrdinaryLoading(viewModelScope, loadExchangesInteractor::execute)
    val exchangesState by stateFromFlow(exchangesLoading.stateFlow)

    init {
        exchangesLoading.load(false)
    }
}