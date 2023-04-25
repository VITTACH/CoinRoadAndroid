package ru.mobileup.coinroad.ui.alerts

import me.aartikov.sesame.navigation.NavigationMessage
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.Exchange
import ru.mobileup.coinroad.navigation.system.Back
import java.io.Serializable

data class NewAlertData(
    val exchange: Exchange,
    val currencyPair: CurrencyPair,
    @Transient val onBackNavMessage: NavigationMessage = Back
) : Serializable