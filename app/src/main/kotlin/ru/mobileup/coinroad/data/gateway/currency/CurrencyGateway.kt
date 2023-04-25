package ru.mobileup.coinroad.data.gateway.currency

import ru.mobileup.coinroad.domain.common.Bar
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.domain.common.Trade

interface CurrencyGateway {

    suspend fun getCurrencyPairs(): List<CurrencyPair>

    suspend fun getTrades(currencyPair: CurrencyPair): List<Trade>

    suspend fun getBars(currencyPair: CurrencyPair, timeStep: TimeStep): List<Bar>
}