package ru.mobileup.coinroad.navigation.profile

import me.aartikov.sesame.navigation.NavigationMessage
import me.aartikov.sesame.navigation.NavigationMessageHandler
import ru.mobileup.coinroad.ui.compose.ComposeMainScreen
import ru.mobileup.coinroad.navigation.FragmentNavigator

class ProfileRouter(
    private val navigator: FragmentNavigator
) : NavigationMessageHandler {

    override fun handleNavigationMessage(message: NavigationMessage): Boolean {
        when (message) {
            is OpenConnectionScreen -> navigator.goTo(ComposeMainScreen())
            is OpenNewConnectionScreen -> navigator.goTo(ComposeMainScreen())
            else -> return false
        }
        return true
    }
}