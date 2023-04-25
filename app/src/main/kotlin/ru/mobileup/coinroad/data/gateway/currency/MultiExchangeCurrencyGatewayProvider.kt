package ru.mobileup.coinroad.data.gateway.currency

import ru.mobileup.coinroad.data.gateway.currency.binance.BinanceCurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.hitbtc.HitBtcCurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.kickex.KickExCurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.kraken.KrakenCurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.kucoin.KucoinCurrencyGateway
import ru.mobileup.coinroad.domain.common.Exchange

class MultiExchangeCurrencyGatewayProvider(
    private val hitbtcCurrencyGateway: HitBtcCurrencyGateway,
    private val binanceCurrencyGateway: BinanceCurrencyGateway,
    private val kucoinCurrencyGateway: KucoinCurrencyGateway,
    private val kickExCurrencyGateway: KickExCurrencyGateway,
    private val krakenCurrencyGateway: KrakenCurrencyGateway,
) : CurrencyGatewayProvider {

    override fun getCurrencyGateway(exchangeId: Exchange.Ids): CurrencyGateway {
        return when (exchangeId) {
            Exchange.Ids.HITBTC -> hitbtcCurrencyGateway
            Exchange.Ids.BINANCE -> binanceCurrencyGateway
            Exchange.Ids.KUCOIN -> kucoinCurrencyGateway
            Exchange.Ids.KICKEX -> kickExCurrencyGateway
            Exchange.Ids.KRAKEN -> krakenCurrencyGateway
            else -> throw IllegalArgumentException("Unknown exchange id $exchangeId")
        }
    }
}