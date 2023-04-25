package ru.mobileup.coinroad.navigation.alerts

import me.aartikov.sesame.navigation.NavigationMessage
import ru.mobileup.coinroad.domain.common.Exchange
import ru.mobileup.coinroad.ui.alerts.NewAlertData

object OpenAlertsScreen : NavigationMessage
data class OpenNewAlertScreen(val data: NewAlertData) : NavigationMessage
data class OpenAlertCreatingScreen(val exchange: Exchange) : NavigationMessage