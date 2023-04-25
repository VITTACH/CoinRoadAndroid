package ru.mobileup.coinroad.navigation.settings

import me.aartikov.sesame.navigation.NavigationMessage
import me.aartikov.sesame.navigation.NavigationMessageHandler
import ru.mobileup.coinroad.navigation.FragmentNavigator
import ru.mobileup.coinroad.ui.settings.SettingsScreen

class SettingsRouter(
    private val navigator: FragmentNavigator
) : NavigationMessageHandler {

    override fun handleNavigationMessage(message: NavigationMessage): Boolean {
        when (message) {
            is OpenSettingsScreen -> navigator.goTo(SettingsScreen())
            is ReOpenSettingsScreen -> navigator.replace(SettingsScreen())
            else -> return false
        }
        return true
    }
}