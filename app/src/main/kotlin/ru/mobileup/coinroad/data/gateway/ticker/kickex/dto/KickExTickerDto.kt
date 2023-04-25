@file:UseSerializers(BigDecimalSerializer::class)

package ru.mobileup.coinroad.data.gateway.ticker.kickex.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.Exchange
import ru.mobileup.coinroad.domain.common.Ticker
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer
import ru.mobileup.coinroad.util.calculatePercentDifference
import java.math.BigDecimal

@Serializable
data class KickExTickerDto(
    @SerialName("highestPrice") val highestPrice: BigDecimal,
    @SerialName("lowestPrice") val lowestPrice: BigDecimal,
    @SerialName("pairName") val pairName: String
)

fun KickExTickerDto.toTicker(currencyPair: CurrencyPair): Ticker {
    val price24hPercentDiff = calculatePercentDifference(highestPrice, lowestPrice)
    return Ticker(
        exchange = Exchange.fromId(Exchange.Ids.KICKEX),
        currencyPair = currencyPair,
        priceChangeForLastDay = highestPrice - lowestPrice,
        priceChangeInPercentForLastDay = price24hPercentDiff.toFloat()
    )
}