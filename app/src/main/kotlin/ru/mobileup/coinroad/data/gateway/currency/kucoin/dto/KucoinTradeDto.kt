@file:UseSerializers(BigDecimalSerializer::class, InstantSerializer::class)

package ru.mobileup.coinroad.data.gateway.currency.kucoin.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Trade
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer
import ru.mobileup.coinroad.util.serializer.InstantSerializer
import java.math.BigDecimal

@Serializable
data class KucoinTradeDto(
    @SerialName("data") val trades: List<KucoinTrade>
)

@Serializable
data class KucoinTrade(
    @SerialName("time") val time: Long,
    @SerialName("price") val price: BigDecimal
)

fun KucoinTrade.toTrade(): Trade {
    return Trade(
        time = Instant.fromEpochMilliseconds(time / 1_000_000_000L),
        price = price
    )
}