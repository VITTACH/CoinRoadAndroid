package ru.mobileup.coinroad.ui.connection

import androidx.lifecycle.viewModelScope
import me.aartikov.sesame.loading.simple.FlowLoading
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.dataOrNull
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.property.command
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.stateFromFlow
import ru.mobileup.coinroad.domain.common.Connection
import ru.mobileup.coinroad.domain.common.toAccount
import ru.mobileup.coinroad.domain.usecase.exchange.LoadExchangesInteractor
import ru.mobileup.coinroad.domain.usecase.portfolio.LoadAccountsInteractor
import ru.mobileup.coinroad.domain.usecase.portfolio.LoadWalletsInteractor
import ru.mobileup.coinroad.ui.base.BaseViewModel

class SelectConnectionViewModel(
    private val loadWalletsInteractor: LoadWalletsInteractor,
    private val loadAccountsInteractor: LoadAccountsInteractor,
    private val loadExchangesInteractor: LoadExchangesInteractor
) : BaseViewModel() {

    private val exchangesLoading = OrdinaryLoading(viewModelScope, loadExchangesInteractor::execute)
    private val exchangesState by stateFromFlow(exchangesLoading.stateFlow)

    private val walletsLoading = FlowLoading(
        scope = viewModelScope,
        load = loadWalletsInteractor::load,
        observe = loadWalletsInteractor::observe
    )
    private val walletsState by stateFromFlow(walletsLoading.stateFlow)

    private val accountsLoading = FlowLoading(
        scope = viewModelScope,
        load = loadAccountsInteractor::load,
        observe = loadAccountsInteractor::observe
    )
    private val accountsState by stateFromFlow(accountsLoading.stateFlow)

    val onNewConnectionCommand = command<Connection>()
    val onConnectionCommand = command<Connection>()

    init {
        walletsLoading.handleErrors(viewModelScope) { error -> handleException(error.throwable) }
        accountsLoading.handleErrors(viewModelScope) { error -> handleException(error.throwable) }
    }

    override fun onActive() {
        super.onActive()

        exchangesLoading.load(false)
        walletsLoading.load(false)
        accountsLoading.load(false)
    }

    val walletDetailsItems by computed(
        this::walletsState
    ) { state ->
        val data = state.dataOrNull
        data
    }

    val accountDetailsItems by computed(
        this::accountsState,
        this::exchangesState
    ) { accountsState, exchangesState ->
        val accounts = accountsState.dataOrNull
        val exchanges = exchangesState.dataOrNull ?: return@computed null

        if (accounts.isNullOrEmpty()) {
            listOf(EmptyConnectionItemData) + exchanges.filter {
                it.isPrivateEnabled
            }.map {
                NewConnectionItemData(it.toAccount()) { it.onNewConnectionClicks() }
            }
        } else accounts.map {
            ConnectionItemData(it) { it.onConnectionClicks() }
        }
    }

    fun onSyncConnectionsClicked() {}

    private fun Connection.onNewConnectionClicks() = onNewConnectionCommand(this)

    private fun Connection.onConnectionClicks() = onConnectionCommand(this)
}