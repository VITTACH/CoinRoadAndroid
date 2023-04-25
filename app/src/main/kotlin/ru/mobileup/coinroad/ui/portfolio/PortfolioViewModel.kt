package ru.mobileup.coinroad.ui.portfolio

import androidx.lifecycle.viewModelScope
import me.aartikov.sesame.loading.simple.FlowLoading
import me.aartikov.sesame.loading.simple.dataOrNull
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.stateFromFlow
import ru.mobileup.coinroad.domain.common.Connection
import ru.mobileup.coinroad.domain.usecase.portfolio.LoadAccountsInteractor
import ru.mobileup.coinroad.domain.usecase.portfolio.LoadWalletsInteractor
import ru.mobileup.coinroad.navigation.profile.OpenConnectionScreen
import ru.mobileup.coinroad.navigation.profile.OpenNewConnectionScreen
import ru.mobileup.coinroad.navigation.system.OpenSelectConnectionDialog
import ru.mobileup.coinroad.ui.base.BaseViewModel
import ru.mobileup.coinroad.ui.connection.ConnectionItemData
import ru.mobileup.coinroad.ui.connection.EmptyConnectionItemData

class PortfolioViewModel(
    private val loadWalletsInteractor: LoadWalletsInteractor,
    private val loadAccountsInteractor: LoadAccountsInteractor
) : BaseViewModel() {

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

    init {
        walletsLoading.handleErrors(viewModelScope) { error -> handleException(error.throwable) }
        accountsLoading.handleErrors(viewModelScope) { error -> handleException(error.throwable) }
    }

    override fun onActive() {
        super.onActive()

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
        this::accountsState
    ) { state ->
        val data = state.dataOrNull
        return@computed if (data.isNullOrEmpty()) {
            listOf(EmptyConnectionItemData)
        } else data.map {
            ConnectionItemData(it) { onAccountClicked(it) }
        }
    }

    fun onNewConnectionClicked() {
        navigate(OpenSelectConnectionDialog(
            onNewConnectionClicked = { navigate(OpenNewConnectionScreen) },
            onConnectionClicked = { navigate(OpenConnectionScreen) }
        ))
    }

    private fun onWalletClicked() {
    }

    private fun onAccountClicked(account: Connection) {
    }
}