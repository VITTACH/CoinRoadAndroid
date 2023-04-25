package ru.mobileup.coinroad.domain.common

import kotlinx.datetime.Instant
import java.io.Serializable
import java.math.BigDecimal

data class Alert(
    val id: String,
    val precision: Int,
    val exchange: Exchange,
    val currencyPair: CurrencyPair,
    @Transient val time: Instant,
    val price: BigDecimal,
    val color: Int,
    val status: Status,
    val message: String = "",
    val isActive: Boolean = true
) : Serializable {
    enum class Status {
        ERROR, ENABLED, DISABLED, FILLED
    }
}