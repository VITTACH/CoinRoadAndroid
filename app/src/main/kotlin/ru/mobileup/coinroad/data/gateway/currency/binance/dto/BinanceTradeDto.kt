@file:UseSerializers(BigDecimalSerializer::class, InstantSerializer::class)
package ru.mobileup.coinroad.data.gateway.currency.binance.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Trade
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer
import ru.mobileup.coinroad.util.serializer.InstantSerializer
import java.math.BigDecimal

@Serializable
data class BinanceTradeDto(
    @SerialName("time") val time: Long,
    @SerialName("price") val price: BigDecimal
)

fun BinanceTradeDto.toTrade(): Trade {
    return Trade(
        time = Instant.fromEpochMilliseconds(time),
        price = price
    )
}