package ru.mobileup.coinroad.data.gateway.ticker.kickex

import ru.mobileup.coinroad.data.gateway.currency.kickex.KickExApi
import ru.mobileup.coinroad.data.gateway.ticker.TickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.kickex.dto.toTicker
import ru.mobileup.coinroad.domain.common.CurrencyPair

class KickExTickerGateway(
    private val api: KickExApi
) : TickerGateway {

    override suspend fun getTicker(
        currencyPair: CurrencyPair
    ) = api.getTickers()
        .filter { it.pairName == currencyPair.id }
        .map { it.toTicker(currencyPair) }
        .first()
}