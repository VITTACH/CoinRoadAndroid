@file:UseSerializers(BigDecimalSerializer::class)

package ru.mobileup.coinroad.data.gateway.currency.kickex.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Bar
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer
import java.math.BigDecimal

@Serializable
data class KickExBarDto(
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("openPrice") val openPrice: BigDecimal,
    @SerialName("closePrice") val closePrice: BigDecimal,
    @SerialName("highPrice") val highPrice: BigDecimal,
    @SerialName("lowPrice") val lowPrice: BigDecimal,
    @SerialName("transactionVolume") val transactionVolume: BigDecimal,
    @SerialName("transactionAmount") val transactionAmount: BigDecimal
)

fun KickExBarDto.toBar(): Bar {
    return Bar(
        startTime = Instant.fromEpochMilliseconds(timestamp),
        lowPrice = lowPrice,
        highPrice = highPrice,
        openPrice = openPrice,
        closePrice = closePrice
    )
}