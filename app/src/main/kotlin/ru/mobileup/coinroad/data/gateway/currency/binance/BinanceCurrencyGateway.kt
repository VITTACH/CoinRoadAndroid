package ru.mobileup.coinroad.data.gateway.currency.binance

import ru.mobileup.coinroad.data.gateway.currency.CurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.binance.dto.toCurrencyPairs
import ru.mobileup.coinroad.data.gateway.currency.binance.dto.toTrade
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.util.makeApiCall

class BinanceCurrencyGateway(private val api: BinanceApi) : CurrencyGateway {

    companion object {
        private const val BINANCE_LIMIT = 1000L
    }

    override suspend fun getCurrencyPairs() = makeApiCall {
        api.getCurrencyPairs().toCurrencyPairs()
    }

    override suspend fun getTrades(currencyPair: CurrencyPair) = makeApiCall {
        api.getTrades(currencyPair.id, BINANCE_LIMIT).map { it.toTrade() }
    }

    override suspend fun getBars(currencyPair: CurrencyPair, timeStep: TimeStep) = makeApiCall {
        return@makeApiCall try {
            api.getBars(currencyPair.id, timeStep.toBinancePeriod(), BINANCE_LIMIT).map { it.bar }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun TimeStep.toBinancePeriod(): String {
        val period = duration.inMinutes.toInt()
        return if (period < 60) "${period}m" else "${period / 60}h"
    }
}