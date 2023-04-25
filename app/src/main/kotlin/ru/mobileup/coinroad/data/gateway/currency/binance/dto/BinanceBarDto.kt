@file:UseSerializers(BigDecimalSerializer::class, InstantSerializer::class)

package ru.mobileup.coinroad.data.gateway.currency.binance.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Bar
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer
import ru.mobileup.coinroad.util.serializer.BinanceBarSerializer
import ru.mobileup.coinroad.util.serializer.InstantSerializer

@Serializable(with = BinanceBarSerializer::class)
data class BinanceBarDto(val bar: Bar)