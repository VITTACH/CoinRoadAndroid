package ru.mobileup.coinroad.navigation.system

import me.aartikov.sesame.navigation.NavigationMessage
import ru.mobileup.coinroad.domain.common.Connection
import ru.mobileup.coinroad.domain.common.Exchange
import java.net.URL

object OpenMainScreen : NavigationMessage
object OpenAboutScreen : NavigationMessage
object OpenExchangeChoosingScreen : NavigationMessage
object BackToAlertsScreen : NavigationMessage
object Back : NavigationMessage
object Close : NavigationMessage

data class OpenExternalUrl(val url: URL) : NavigationMessage

data class OpenSelectExchangeDialog(
    val onExchangeClicked: (Exchange) -> Unit
) : NavigationMessage

data class OpenSelectConnectionDialog(
    val onNewConnectionClicked: (Connection) -> Unit,
    val onConnectionClicked: (Connection) -> Unit
) : NavigationMessage