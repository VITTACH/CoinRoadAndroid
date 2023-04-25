package ru.mobileup.coinroad.navigation.system

import me.aartikov.sesame.navigation.NavigationMessage
import me.aartikov.sesame.navigation.NavigationMessageHandler
import ru.mobileup.coinroad.navigation.FragmentNavigator
import ru.mobileup.coinroad.navigation.SystemBackNavigator
import ru.mobileup.coinroad.ui.about.AboutScreen
import ru.mobileup.coinroad.ui.alerts.AlertsScreen
import ru.mobileup.coinroad.ui.connection.SelectConnectionDialog
import ru.mobileup.coinroad.ui.exchange.ExchangeChoosingScreen
import ru.mobileup.coinroad.ui.exchange.SelectExchangeDialog
import ru.mobileup.coinroad.ui.main.MainScreen
import ru.mobileup.coinroad.util.helper.WebTabHelper
import java.net.URL

class SystemRouter(
    private val navigator: FragmentNavigator,
    private val webTabHelper: WebTabHelper,
    private val systemBackNavigator: SystemBackNavigator
) : NavigationMessageHandler {

    override fun handleNavigationMessage(message: NavigationMessage): Boolean {
        when (message) {
            is Back -> {
                if (!navigator.back()) systemBackNavigator.closeApp()
            }

            is BackToAlertsScreen -> navigator.backTo<AlertsScreen>()

            is Close -> systemBackNavigator.closeApp(true)

            is OpenExchangeChoosingScreen -> navigator.setRoot(ExchangeChoosingScreen())

            is OpenExternalUrl -> webTabHelper.openUrl(message.url)

            is OpenAboutScreen -> navigator.goTo(AboutScreen())

            is OpenMainScreen -> navigator.setRoot(MainScreen())

            is OpenSelectExchangeDialog -> {
                val fragment = SelectExchangeDialog(
                    { webTabHelper.openUrl(URL(it.url)) },
                    message.onExchangeClicked
                )
                navigator.showDialog(fragment)
            }

            is OpenSelectConnectionDialog -> {
                val fragment = SelectConnectionDialog(
                    onNewConnectionClicked = message.onNewConnectionClicked,
                    onConnectionClicked = message.onConnectionClicked
                )
                navigator.showDialog(fragment)
            }

            else -> return false
        }
        return true
    }
}