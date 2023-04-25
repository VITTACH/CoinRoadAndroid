package ru.mobileup.coinroad.data.gateway.ticker

import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.Ticker

interface TickerGateway {

    suspend fun getTicker(currencyPair: CurrencyPair): Ticker
}