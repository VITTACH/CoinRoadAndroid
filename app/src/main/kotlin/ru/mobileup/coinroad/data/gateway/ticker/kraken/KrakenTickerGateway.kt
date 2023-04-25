package ru.mobileup.coinroad.data.gateway.ticker.kraken

import ru.mobileup.coinroad.data.gateway.currency.kraken.KrakenApi
import ru.mobileup.coinroad.data.gateway.ticker.TickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.kraken.dto.toTicker
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.util.makeApiCall

class KrakenTickerGateway(
    private val api: KrakenApi
) : TickerGateway {

    override suspend fun getTicker(currencyPair: CurrencyPair) = makeApiCall {
        api.getTicker(currencyPair.id).toTicker(currencyPair)
    }
}