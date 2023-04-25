package ru.mobileup.coinroad.navigation.alerts

import me.aartikov.sesame.navigation.NavigationMessage
import me.aartikov.sesame.navigation.NavigationMessageHandler
import ru.mobileup.coinroad.navigation.FragmentNavigator
import ru.mobileup.coinroad.ui.alerts.AlertsScreen
import ru.mobileup.coinroad.ui.alerts.NewAlertScreen
import ru.mobileup.coinroad.ui.graph_creating.GraphCreatingData
import ru.mobileup.coinroad.ui.graph_creating.GraphCreatingScreen

class AlertRouter(
    private val navigator: FragmentNavigator
) : NavigationMessageHandler {

    override fun handleNavigationMessage(message: NavigationMessage): Boolean {
        when (message) {
            is OpenAlertsScreen -> navigator.goTo(AlertsScreen())

            is OpenNewAlertScreen -> {
                navigator.goTo(NewAlertScreen().apply {
                    data = message.data
                })
            }

            is OpenAlertCreatingScreen -> {
                navigator.goTo(GraphCreatingScreen().apply {
                    data = GraphCreatingData(message.exchange, isAlert = true)
                })
            }

            else -> return false
        }
        return true
    }
}