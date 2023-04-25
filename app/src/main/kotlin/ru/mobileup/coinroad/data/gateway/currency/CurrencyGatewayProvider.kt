package ru.mobileup.coinroad.data.gateway.currency

import ru.mobileup.coinroad.domain.common.Exchange

interface CurrencyGatewayProvider {

    fun getCurrencyGateway(exchangeId: Exchange.Ids): CurrencyGateway
}