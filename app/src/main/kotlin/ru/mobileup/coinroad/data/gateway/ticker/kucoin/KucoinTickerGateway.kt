package ru.mobileup.coinroad.data.gateway.ticker.kucoin

import ru.mobileup.coinroad.data.gateway.currency.kucoin.KucoinApi
import ru.mobileup.coinroad.data.gateway.ticker.TickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.kucoin.dto.toTicker
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.util.makeApiCall

class KucoinTickerGateway(
    private val api: KucoinApi
) : TickerGateway {

    override suspend fun getTicker(currencyPair: CurrencyPair) = makeApiCall {
        api.getTicker(currencyPair.id).ticker.toTicker(currencyPair)
    }
}