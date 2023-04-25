package ru.mobileup.coinroad.data.gateway.ticker.kraken.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.Exchange
import ru.mobileup.coinroad.domain.common.Ticker
import ru.mobileup.coinroad.util.serializer.KrakenTickerSerializer
import ru.mobileup.coinroad.util.calculatePercentDifference
import java.math.BigDecimal

@Serializable(with = KrakenTickerSerializer::class)
data class KrakenTicker(
    val lowest24hPrice: BigDecimal,
    val biggest24hPrice: BigDecimal
)

@Serializable
data class KrakenTickerDto(
    @SerialName("result") val result: KrakenTicker
)

fun KrakenTickerDto.toTicker(currencyPair: CurrencyPair): Ticker {
    return with(result) {
        val price24hPercentDiff = calculatePercentDifference(biggest24hPrice, lowest24hPrice)
        Ticker(
            exchange = Exchange.fromId(Exchange.Ids.KRAKEN),
            currencyPair = currencyPair,
            priceChangeForLastDay = biggest24hPrice - lowest24hPrice,
            priceChangeInPercentForLastDay = price24hPercentDiff.toFloat()
        )
    }
}