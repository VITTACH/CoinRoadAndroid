package ru.mobileup.coinroad.data.gateway.currency.kickex.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.mobileup.coinroad.domain.common.Currency
import ru.mobileup.coinroad.domain.common.CurrencyPair

@Serializable
data class KickExCurrencyPairDto(
    @SerialName("pairName") val id: String,
    @SerialName("baseCurrency") val baseCurrency: String,
    @SerialName("quoteCurrencу") val quoteCurrency: String,
    @SerialName("state") val tradingState: Int
) {
    companion object {
        const val TRADING_AVAILABLE = 4
    }
}

fun KickExCurrencyPairDto.toCurrencyPair() = CurrencyPair(
    id = id,
    baseCurrency = Currency.fromSymbol(baseCurrency),
    quoteCurrency = Currency.fromSymbol(quoteCurrency)
)