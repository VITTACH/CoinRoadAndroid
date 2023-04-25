@file:UseSerializers(BigDecimalSerializer::class, InstantSerializer::class)

package ru.mobileup.coinroad.data.gateway.currency.kraken.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Bar
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer
import ru.mobileup.coinroad.util.serializer.InstantSerializer
import ru.mobileup.coinroad.util.serializer.KrakenBarSerializer

@Serializable(with = KrakenBarSerializer::class)
data class KrakenPublicBars(val bars: List<Bar>)

@Serializable
data class KrakenBarDto(
    @SerialName("result") val result: KrakenPublicBars
)

fun KrakenBarDto.toBars(): List<Bar> = result.bars