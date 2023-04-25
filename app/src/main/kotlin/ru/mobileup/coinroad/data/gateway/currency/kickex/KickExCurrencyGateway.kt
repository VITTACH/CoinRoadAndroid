package ru.mobileup.coinroad.data.gateway.currency.kickex

import ru.mobileup.coinroad.data.gateway.currency.CurrencyGateway
import ru.mobileup.coinroad.data.gateway.currency.kickex.dto.KickExCurrencyPairDto
import ru.mobileup.coinroad.data.gateway.currency.kickex.dto.toCurrencyPair
import ru.mobileup.coinroad.data.gateway.currency.kickex.dto.toTrade
import ru.mobileup.coinroad.data.gateway.currency.kickex.dto.toBar
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.util.makeApiCall

class KickExCurrencyGateway(private val api: KickExApi) : CurrencyGateway {

    override suspend fun getCurrencyPairs() = makeApiCall {
        api.getCurrencyPairs()
            .filter { it.tradingState == KickExCurrencyPairDto.TRADING_AVAILABLE }
            .map { it.toCurrencyPair() }
    }

    override suspend fun getTrades(currencyPair: CurrencyPair) = makeApiCall {
        api.getTrades(currencyPair.id).map { it.toTrade() }
    }

    override suspend fun getBars(currencyPair: CurrencyPair, timeStep: TimeStep) = makeApiCall {
        return@makeApiCall try {
            api.getBars(currencyPair.id, timeStep.duration.inMinutes.toInt()).map { it.toBar() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}