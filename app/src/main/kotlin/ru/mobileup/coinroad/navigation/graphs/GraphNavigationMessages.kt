package ru.mobileup.coinroad.navigation.graphs

import me.aartikov.sesame.navigation.NavigationMessage
import ru.mobileup.coinroad.domain.common.Exchange
import ru.mobileup.coinroad.domain.common.Graph

data class OpenGraphCreateScreen(val exchange: Exchange) : NavigationMessage
data class OpenGraphEditScreen(val graph: Graph) : NavigationMessage