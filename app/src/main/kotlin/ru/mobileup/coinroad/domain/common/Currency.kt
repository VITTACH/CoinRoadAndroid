package ru.mobileup.coinroad.domain.common

import java.io.Serializable

/**
 * Currency ("USD", "BTX" etc.)
 *
 * @property id unique id
 * @property name readable name
 */
data class Currency(
    val id: String,
    val name: String
) : Serializable {

    companion object {
        fun fromSymbol(symbol: String) = Currency(symbol, symbol)
    }
}