package ru.mobileup.coinroad.data.gateway.ticker

import ru.mobileup.coinroad.domain.common.Exchange

interface TickerGatewayProvider {

    fun getTickerGateway(exchangeId: Exchange.Ids): TickerGateway
}