package ru.mobileup.coinroad.di

import org.koin.dsl.module
import ru.mobileup.coinroad.data.gateway.currency.CurrencyGatewayProvider
import ru.mobileup.coinroad.data.gateway.currency.MultiExchangeCurrencyGatewayProvider
import ru.mobileup.coinroad.data.gateway.currency.binance.BinanceCurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.hitbtc.HitBtcCurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.kickex.KickExCurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.kraken.KrakenCurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.kucoin.KucoinCurrencyGateway
import ru.mobileup.coinroad.data.gateway.exchange.ExchangeGateway
import ru.mobileup.coinroad.data.gateway.exchange.HardcodedExchangeGateway
import ru.mobileup.coinroad.data.gateway.ticker.MultiExchangeTickerGatewayProvider
import ru.mobileup.coinroad.data.gateway.ticker.TickerGatewayProvider
import ru.mobileup.coinroad.data.gateway.ticker.binance.BinanceTickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.hitbtc.HitBtcTickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.kickex.KickExTickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.kraken.KrakenTickerGateway
import ru.mobileup.coinroad.data.gateway.ticker.kucoin.KucoinTickerGateway
import ru.mobileup.coinroad.data.gateway.time.RealTimeGateway
import ru.mobileup.coinroad.data.gateway.time.TimeGateway

val gatewayModule = module {
    single { HitBtcCurrencyGateway(get()) }
    single { BinanceCurrencyGateway(get()) }
    single { KucoinCurrencyGateway(get()) }
    single { KrakenCurrencyGateway(get()) }
    single { KickExCurrencyGateway(get()) }

    single { HitBtcTickerGateway(get()) }
    single { BinanceTickerGateway(get()) }
    single { KucoinTickerGateway(get()) }
    single { KrakenTickerGateway(get()) }
    single { KickExTickerGateway(get()) }

    single<ExchangeGateway> { HardcodedExchangeGateway() }
    single<TickerGatewayProvider> {
        MultiExchangeTickerGatewayProvider(get(), get(), get(), get(), get())
    }
    single<CurrencyGatewayProvider> {
        MultiExchangeCurrencyGatewayProvider(get(), get(), get(), get(), get())
    }
    single<TimeGateway> { RealTimeGateway() }
}