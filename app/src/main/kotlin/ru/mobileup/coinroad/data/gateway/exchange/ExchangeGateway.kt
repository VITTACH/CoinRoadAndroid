package ru.mobileup.coinroad.data.gateway.exchange

import ru.mobileup.coinroad.domain.common.Exchange

interface ExchangeGateway {

    suspend fun getExchanges(): List<Exchange>
}