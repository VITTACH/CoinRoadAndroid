package ru.mobileup.coinroad.data.gateway.ticker.binance

import ru.mobileup.coinroad.data.gateway.currency.binance.BinanceApi
import ru.mobileup.coinroad.data.gateway.ticker.TickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.binance.dto.toTicker
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.util.makeApiCall

class BinanceTickerGateway(
    private val api: BinanceApi
) : TickerGateway {

    override suspend fun getTicker(currencyPair: CurrencyPair) = makeApiCall {
        api.getTicker(currencyPair.id).toTicker(currencyPair)
    }
}