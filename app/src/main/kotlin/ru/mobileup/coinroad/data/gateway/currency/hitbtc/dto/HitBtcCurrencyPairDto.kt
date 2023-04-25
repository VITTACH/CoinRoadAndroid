@file:UseSerializers(BigDecimalSerializer::class)

package ru.mobileup.coinroad.data.gateway.currency.hitbtc.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Currency
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer
import java.math.BigDecimal

@Serializable
data class HitBtcCurrencyPairDto(
    @SerialName("id") val id: String,
    @SerialName("baseCurrency") val baseCurrency: String,
    @SerialName("quoteCurrency") val quoteCurrency: String,
    @SerialName("tickSize") val minValue: BigDecimal
)

fun HitBtcCurrencyPairDto.toCurrencyPair() = CurrencyPair(
    id = id,
    baseCurrency = Currency.fromSymbol(baseCurrency),
    quoteCurrency = Currency.fromSymbol(quoteCurrency)
)
