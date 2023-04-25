package ru.mobileup.coinroad.domain.common

import java.io.Serializable


/**
 * Is used to represent the quotation of the relative value of a currency unit against the unit of another currency
 *
 * @property id unique id
 * @property baseCurrency  the currency that is quoted in relation
 * @property quoteCurrency the currency that is used as the reference
 */
data class CurrencyPair(
    val id: String,
    val baseCurrency: Currency,
    val quoteCurrency: Currency
) : Serializable {

    companion object {
        fun fromSymbol(symbol: String): CurrencyPair {
            val split = symbol.indexOf("/")
            require(split >= 1) { "Could not parse currency'$symbol'" }

            val baseSymbol = symbol.substring(0, split)
            val quoteSymbol = symbol.substring(split + 1)

            return CurrencyPair(
                id = symbol,
                baseCurrency = Currency.fromSymbol(baseSymbol),
                quoteCurrency = Currency.fromSymbol(quoteSymbol)
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        val that = other as? CurrencyPair ?: return false
        return id == that.id
    }

    override fun toString(): String {
        return "${baseCurrency.id} / ${quoteCurrency.id}"
    }
}

fun List<CurrencyPair>.getAllBaseCurrencies(): List<Currency> {
    return map { it.baseCurrency }.sortedBy { it.name }.distinct()
}

fun List<CurrencyPair>.getQuoteCurrencies(baseCurrency: Currency): List<Currency> {
    return filter { it.baseCurrency == baseCurrency }.map { it.quoteCurrency }.sortedBy { it.name }
}

fun List<CurrencyPair>.findCurrency(
    baseCurrency: Currency?,
    quoteCurrency: Currency?
): CurrencyPair? {
    return find { it.baseCurrency == baseCurrency && it.quoteCurrency == quoteCurrency }
}