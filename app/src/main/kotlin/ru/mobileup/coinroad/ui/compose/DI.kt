package ru.mobileup.coinroad.ui.compose

import ru.mobileup.coinroad.ui.compose.connections.connectionModule
import ru.mobileup.coinroad.ui.compose.core.coreModule
import ru.mobileup.coinroad.ui.compose.main.rootModule
import ru.mobileup.coinroad.ui.compose.portfolio.portfolioModule

val composeModules = listOf(
    rootModule,
    coreModule,
    connectionModule,
    portfolioModule
)