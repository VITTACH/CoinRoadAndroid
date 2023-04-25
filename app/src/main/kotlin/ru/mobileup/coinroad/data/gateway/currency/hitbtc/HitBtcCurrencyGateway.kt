package ru.mobileup.coinroad.data.gateway.currency.hitbtc

import ru.mobileup.coinroad.data.gateway.currency.CurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.hitbtc.dto.toBar
import ru.mobileup.coinroad.data.gateway.currency.hitbtc.dto.toCurrencyPair
import ru.mobileup.coinroad.data.gateway.currency.hitbtc.dto.toTrade
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.util.makeApiCall

class HitBtcCurrencyGateway(private val api: HitBtcApi) : CurrencyGateway {

    companion object {
        private const val HITBTC_LIMIT = 1000L
    }

    override suspend fun getCurrencyPairs() = makeApiCall {
        api.getCurrencyPairs().map { it.toCurrencyPair() }
    }

    override suspend fun getTrades(currencyPair: CurrencyPair) = makeApiCall {
        api.getTrades(currencyPair.id, HITBTC_LIMIT).map { it.toTrade() }
    }

    override suspend fun getBars(currencyPair: CurrencyPair, timeStep: TimeStep) = makeApiCall {
        return@makeApiCall try {
            api.getBars(currencyPair.id, timeStep.toHitbtcPeriod())
                .map { it.toBar() }
                .reversed()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun TimeStep.toHitbtcPeriod(): String {
        val period = duration.inMinutes.toInt()
        return if (period < 60) "M$period" else "H${period / 60}"
    }
}