package ru.mobileup.coinroad.domain.analytics

import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.Exchange
import ru.mobileup.coinroad.domain.common.TimeStep
import java.util.*

data class PushModel(
    val currencyPair: CurrencyPair,
    val exchange: Exchange,
    val timeStep: TimeStep
)

fun PushModel.toAnalytic(): Map<String, String> {
    return mapOf(
        "id" to hashCode().toString(),
        "currencyPair" to currencyPair.id,
        "exchange" to exchange.id.mName,
        "timeStep" to timeStep.toString(),
        "date" to Date().toString()
    )
}