@file:UseSerializers(BigDecimalSerializer::class, InstantSerializer::class)
package ru.mobileup.coinroad.data.gateway.currency.hitbtc.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Trade
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer
import ru.mobileup.coinroad.util.serializer.InstantSerializer
import java.math.BigDecimal

@Serializable
data class HitBtcTradeDto(
    @SerialName("timestamp") val time: Instant,
    @SerialName("price") val price: BigDecimal
)

fun HitBtcTradeDto.toTrade(): Trade {
    return Trade(
        time = time,
        price = price
    )
}