@file:UseSerializers(BigDecimalSerializer::class)

package ru.mobileup.coinroad.data.gateway.currency.binance.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import ru.mobileup.coinroad.domain.common.Currency
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.util.serializer.BigDecimalSerializer

@Serializable
data class BinanceAssetPairsDto(
    @SerialName("symbols") val symbols: List<BinanceCurrencySymbol>
)

@Serializable
data class BinanceCurrencySymbol(
    @SerialName("symbol") val id: String,
    @SerialName("baseAsset") val baseAsset: String,
    @SerialName("quoteAsset") val quoteAsset: String
)

fun BinanceAssetPairsDto.toCurrencyPairs(): List<CurrencyPair> {
    return symbols.map {
        CurrencyPair(
            id = it.id,
            baseCurrency = Currency.fromSymbol(it.baseAsset),
            quoteCurrency = Currency.fromSymbol(it.quoteAsset)
        )
    }
}
