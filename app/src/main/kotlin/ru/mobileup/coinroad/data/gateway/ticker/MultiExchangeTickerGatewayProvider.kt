package ru.mobileup.coinroad.data.gateway.ticker

import ru.mobileup.coinroad.data.gateway.ticker.binance.BinanceTickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.hitbtc.HitBtcTickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.kickex.KickExTickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.kraken.KrakenTickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.kucoin.KucoinTickerGateway
import ru.mobileup.coinroad.domain.common.Exchange

class MultiExchangeTickerGatewayProvider(
    private val hitBtcTickerGateway: HitBtcTickerGateway,
    private val binanceTickerGateway: BinanceTickerGateway,
    private val kucoinCurrencyGateway: KucoinTickerGateway,
    private val kickExTickerGateway: KickExTickerGateway,
    private val krakenTickerGateway: KrakenTickerGateway,
) : TickerGatewayProvider {

    override fun getTickerGateway(exchangeId: Exchange.Ids): TickerGateway {
        return when (exchangeId) {
            Exchange.Ids.HITBTC -> hitBtcTickerGateway
            Exchange.Ids.BINANCE -> binanceTickerGateway
            Exchange.Ids.KUCOIN -> kucoinCurrencyGateway
            Exchange.Ids.KICKEX -> kickExTickerGateway
            Exchange.Ids.KRAKEN -> krakenTickerGateway
            else -> throw IllegalArgumentException("Unknown exchange id $exchangeId")
        }
    }
}