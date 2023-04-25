@file:UseSerializers(BigDecimalSerializer::class)

package ru.mobileup.coinroad.data.gateway.currency.kraken.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Currency
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer

@Serializable
data class KrakenAssetPairsDto(
    @SerialName("result") val result: Map<String, KrakenAssetPairDto>
)

@Serializable
data class KrakenAssetPairDto(
    @SerialName("altname") val id: String,
    @SerialName("base") val base: String,
    @SerialName("quote") val quote: String,
    @SerialName("pair_decimals") val precision: Int
)

fun KrakenAssetPairsDto.toCurrencyPairs(): List<CurrencyPair> {
    return result
        .filter { !it.key.endsWith(".d") }
        .map {
            CurrencyPair(
                id = it.value.id,
                baseCurrency = adaptCurrency(it.value.base),
                quoteCurrency = adaptCurrency(it.value.quote)
            )
        }
}

private fun adaptCurrency(keyCode: String): Currency {
    val currencyCode =
        if (keyCode.length == 4 &&
            (keyCode.startsWith("X") || keyCode.startsWith("Z"))
        ) {
            keyCode.substring(1)
        } else {
            keyCode
        }
    return Currency.fromSymbol(currencyCode)
}
