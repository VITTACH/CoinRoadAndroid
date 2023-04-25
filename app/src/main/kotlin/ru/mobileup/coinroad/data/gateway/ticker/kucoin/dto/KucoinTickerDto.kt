@file:UseSerializers(BigDecimalSerializer::class)

package ru.mobileup.coinroad.data.gateway.ticker.kucoin.dto

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
data class KucoinTickerDto(
    @SerialName("data") val ticker: KucoinTicker
)

@Serializable
data class KucoinTicker(
    @SerialName("high") val biggest24hPrice: BigDecimal,
    @SerialName("low") val lowest24hPrice: BigDecimal
)

fun KucoinTicker.toTicker(currencyPair: CurrencyPair): Ticker {
    val price24hPercentDiff = calculatePercentDifference(biggest24hPrice, lowest24hPrice)
    return Ticker(
        exchange = Exchange.fromId(Exchange.Ids.KUCOIN),
        currencyPair = currencyPair,
        priceChangeForLastDay = biggest24hPrice - lowest24hPrice,
        priceChangeInPercentForLastDay = price24hPercentDiff.toFloat()
    )
}