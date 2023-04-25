@file:UseSerializers(BigDecimalSerializer::class)

package ru.mobileup.coinroad.data.gateway.currency.kickex.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Trade
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer
import java.math.BigDecimal

@Serializable
data class KickExTradeDto(
    @SerialName("price") val price: BigDecimal,
    @SerialName("timestamp") val time: Long
)

fun KickExTradeDto.toTrade() = Trade(
    time = Instant.fromEpochMilliseconds(time),
    price = price
)