@file:UseSerializers(BigDecimalSerializer::class, InstantSerializer::class)

package ru.mobileup.coinroad.data.gateway.currency.kucoin.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Bar
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer
import ru.mobileup.coinroad.util.serializer.InstantSerializer
import ru.mobileup.coinroad.util.serializer.KucoinBarSerializer

@Serializable(with = KucoinBarSerializer::class)
data class KucoinBar(val bar: Bar)

@Serializable
data class KucoinBarDto(@SerialName("data") val bars: List<KucoinBar>)