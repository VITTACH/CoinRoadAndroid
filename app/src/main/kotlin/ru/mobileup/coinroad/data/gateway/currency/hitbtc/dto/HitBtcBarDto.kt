@file:UseSerializers(BigDecimalSerializer::class, InstantSerializer::class)

package ru.mobileup.coinroad.data.gateway.currency.hitbtc.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Bar
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer
import ru.mobileup.coinroad.util.serializer.InstantSerializer
import java.math.BigDecimal

@Serializable
data class HitBtcBarDto(
    @SerialName("timestamp") val timestamp: Instant,
    @SerialName("open") val open: BigDecimal,
    @SerialName("close") val close: BigDecimal,
    @SerialName("min") val min: BigDecimal,
    @SerialName("max") val max: BigDecimal,
    @SerialName("volume") val volume: BigDecimal,
    @SerialName("volumeQuote") val volumeQuote: BigDecimal
)

fun HitBtcBarDto.toBar(): Bar {
    return Bar(
        startTime = timestamp,
        lowPrice = min,
        highPrice = max,
        openPrice = open,
        closePrice = close
    )
}