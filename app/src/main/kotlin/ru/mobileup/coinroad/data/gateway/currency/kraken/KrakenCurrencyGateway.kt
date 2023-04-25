package ru.mobileup.coinroad.data.gateway.currency.kraken

import ru.mobileup.coinroad.data.gateway.currency.CurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.kickex.dto.toBar
import ru.mobileup.coinroad.data.gateway.currency.kraken.dto.toBars
import ru.mobileup.coinroad.data.gateway.currency.kraken.dto.toCurrencyPairs
import ru.mobileup.coinroad.data.gateway.currency.kraken.dto.toTrades
import ru.mobileup.coinroad.domain.common.Bar
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.util.makeApiCall

class KrakenCurrencyGateway(private val api: KrakenApi) : CurrencyGateway {

    override suspend fun getCurrencyPairs() = makeApiCall {
        api.getCurrencyPairs().toCurrencyPairs()
    }

    override suspend fun getTrades(currencyPair: CurrencyPair) = makeApiCall {
        api.getTrades(symbol = currencyPair.id).toTrades()
    }

    override suspend fun getBars(currencyPair: CurrencyPair, timeStep: TimeStep) = makeApiCall {
        return@makeApiCall try {
            api.getBars(currencyPair.id, timeStep.duration.inMinutes.toInt()).toBars()
        } catch (e: Exception) {
            emptyList()
        }
    }
}