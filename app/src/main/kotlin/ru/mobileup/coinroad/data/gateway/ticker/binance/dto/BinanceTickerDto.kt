@file:UseSerializers(BigDecimalSerializer::class)
package ru.mobileup.coinroad.data.gateway.ticker.binance.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.Exchange
import ru.mobileup.coinroad.domain.common.Ticker
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer
import java.math.BigDecimal

@Serializable
data class BinanceTickerDto(
    @SerialName("priceChangePercent") val priceChangePercent: Float,
    @SerialName("priceChange") val priceChange: BigDecimal
)

fun BinanceTickerDto.toTicker(currencyPair: CurrencyPair): Ticker {
    return Ticker(
        exchange = Exchange.fromId(Exchange.Ids.BINANCE),
        currencyPair = currencyPair,
        priceChangeForLastDay = priceChange,
        priceChangeInPercentForLastDay = priceChangePercent
    )
}