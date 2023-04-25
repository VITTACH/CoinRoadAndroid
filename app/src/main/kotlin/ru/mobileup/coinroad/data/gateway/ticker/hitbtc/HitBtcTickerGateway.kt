package ru.mobileup.coinroad.data.gateway.ticker.hitbtc

import ru.mobileup.coinroad.data.gateway.currency.hitbtc.HitBtcApi
import ru.mobileup.coinroad.data.gateway.ticker.TickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.hitbtc.dto.toTicker
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.util.makeApiCall

class HitBtcTickerGateway(
    private val api: HitBtcApi
) : TickerGateway {

    override suspend fun getTicker(currencyPair: CurrencyPair) = makeApiCall {
        api.getTicker(currencyPair.id).toTicker(currencyPair)
    }
}