package ru.mobileup.coinroad.data.gateway.currency.kucoin

import ru.mobileup.coinroad.data.gateway.currency.CurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.kucoin.dto.toCurrencyPairs
import ru.mobileup.coinroad.data.gateway.currency.kucoin.dto.toTrade
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.util.makeApiCall

class KucoinCurrencyGateway(private val api: KucoinApi) : CurrencyGateway {

    override suspend fun getCurrencyPairs() = makeApiCall {
        api.getCurrencyPairs().toCurrencyPairs()
    }

    override suspend fun getTrades(currencyPair: CurrencyPair) = makeApiCall {
        api.getTrades(currencyPair.id).trades.map { it.toTrade() }
    }

    override suspend fun getBars(currencyPair: CurrencyPair, timeStep: TimeStep) = makeApiCall {
        return@makeApiCall try {
            api.getBars(currencyPair.id, timeStep.toKucoinPeriod()).bars.map { it.bar }.reversed()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun TimeStep.toKucoinPeriod(): String {
        val period = duration.inMinutes.toInt()
        return if (period < 60) "${period}min" else "${period / 60}hour"
    }
}