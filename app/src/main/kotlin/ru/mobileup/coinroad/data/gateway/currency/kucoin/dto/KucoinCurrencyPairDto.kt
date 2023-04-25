@file:UseSerializers(BigDecimalSerializer::class)

package ru.mobileup.coinroad.data.gateway.currency.kucoin.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Currency
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer

@Serializable
data class KucoinAssetPairsDto(
    @SerialName("data") val symbols: List<KucoinCurrencySymbol>
)

@Serializable
data class KucoinCurrencySymbol(
    @SerialName("symbol") val id: String,
    @SerialName("baseCurrency") val baseAsset: String,
    @SerialName("quoteCurrency") val quoteAsset: String
)

fun KucoinAssetPairsDto.toCurrencyPairs(): List<CurrencyPair> {
    return symbols.map {
        CurrencyPair(
            id = it.id,
            baseCurrency = Currency.fromSymbol(it.baseAsset),
            quoteCurrency = Currency.fromSymbol(it.quoteAsset)
        )
    }
}
