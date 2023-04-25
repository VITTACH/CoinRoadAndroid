package ru.mobileup.coinroad.ui.graph_creating

import ru.mobileup.coinroad.domain.common.Currency
import ru.mobileup.coinroad.domain.common.TimeStep

data class GraphDescription(
    val baseCurrency: Currency? = null,
    val quoteCurrency: Currency? = null,
    val timeStep: TimeStep = TimeStep.DEFAULT
)