package ru.mobileup.coinroad.data.gateway.exchange

import ru.mobileup.coinroad.domain.common.Exchange

class HardcodedExchangeGateway : ExchangeGateway {

    override suspend fun getExchanges() = listOf(
        Exchange.fromId(id = Exchange.Ids.KICKEX),
        Exchange.fromId(id = Exchange.Ids.BINANCE, isPrivateEnabled = true),
        Exchange.fromId(id = Exchange.Ids.HITBTC),
        Exchange.fromId(id = Exchange.Ids.KRAKEN),
        Exchange.fromId(id = Exchange.Ids.KUCOIN),
        Exchange.fromId(id = Exchange.Ids.BITMEX, isPublicEnabled = false),
        Exchange.fromId(id = Exchange.Ids.BITSTAMP, isPublicEnabled = false),
        Exchange.fromId(id = Exchange.Ids.POLONIEX, isPublicEnabled = false)
    )
}