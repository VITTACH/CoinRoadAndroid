package ru.mobileup.coinroad.domain.common

import java.io.Serializable

/**
 * Market for the trading of currencies
 *
 * @property id unique id
 * @property name readable name
 * @property isPublicEnabled is exchange available in application
 * @property isPrivateEnabled is exchange available in portfolio
 */
data class Exchange(
    val id: Ids,
    val name: String,
    val url: String,
    val isPublicEnabled: Boolean,
    val isPrivateEnabled: Boolean
) : Serializable {

    companion object {
        fun fromId(
            id: Ids,
            isPublicEnabled: Boolean = true,
            isPrivateEnabled: Boolean = false
        ) = Exchange(id, id.mName, id.url, isPublicEnabled, isPrivateEnabled)

        val DEFAULT = fromId(Ids.DEFAULT)
    }

    enum class Ids(val mName: String, val url: String = "") {
        BINANCE("Binance", "https://accounts.binance.com/en/register"),
        BITMEX("BitMEX", "https://www.bitmex.com/register"),
        BITSTAMP("Bitstamp", "https://www.bitstamp.net/onboarding/register/account-type"),
        DEFAULT("EXCHANGE"),
        HITBTC("HitBTC", "https://hitbtc.com/signupapp"),
        KICKEX("KickEX", "https://id.kickex.com/signup"),
        KRAKEN("Kraken", "https://www.kraken.com/sign-up"),
        KUCOIN("KuCoin", "https://www.kucoin.com/ucenter/signup"),
        POLONIEX("Poloniex", "https://poloniex.com/signup")
    }

    override fun equals(other: Any?): Boolean {
        val that = other as? Exchange ?: return false
        return id == that.id
    }

    override fun toString(): String {
        return name.uppercase()
    }
}

fun Exchange.toAccount() = Connection.Account(exchange = this)