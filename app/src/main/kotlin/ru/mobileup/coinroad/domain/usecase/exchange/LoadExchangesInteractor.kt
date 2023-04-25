package ru.mobileup.coinroad.domain.usecase.exchange

import ru.mobileup.coinroad.data.gateway.exchange.ExchangeGateway
import ru.mobileup.coinroad.domain.common.Exchange

class LoadExchangesInteractor(
    private val gateway: ExchangeGateway
) {
    suspend fun execute(fresh: Boolean = false): List<Exchange> {
        return gateway.getExchanges()
    }
}