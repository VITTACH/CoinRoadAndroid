package ru.mobileup.coinroad.navigation.graphs

import me.aartikov.sesame.navigation.NavigationMessage
import me.aartikov.sesame.navigation.NavigationMessageHandler
import ru.mobileup.coinroad.navigation.FragmentNavigator
import ru.mobileup.coinroad.ui.graph_creating.GraphCreatingData
import ru.mobileup.coinroad.ui.graph_creating.GraphCreatingScreen
import ru.mobileup.coinroad.ui.graph_editing.GraphEditingScreen

class GraphRouter(
    private val navigator: FragmentNavigator
) : NavigationMessageHandler {

    override fun handleNavigationMessage(message: NavigationMessage) = when (message) {
        is OpenGraphCreateScreen -> {
            navigator.goTo(GraphCreatingScreen().apply {
                data = GraphCreatingData(message.exchange)
            })
            true
        }

        is OpenGraphEditScreen -> {
            navigator.goTo(GraphEditingScreen().apply { graph = message.graph })
            true
        }

        else -> false
    }
}