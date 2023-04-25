package ru.mobileup.coinroad.data.storage.alert.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import ru.mobileup.coinroad.data.storage.graph.entity.*
import ru.mobileup.coinroad.domain.common.*
import java.math.BigDecimal

@Entity(tableName = "alerts")
data class AlertDb(
    @PrimaryKey val id: String,
    @Embedded(prefix = "exchange_") val exchange: ExchangeDb,
    @Embedded(prefix = "currencyPair_") val currencyPair: CurrencyPairDb,
    val time: Long,
    val price: String,
    val precision: Int,
    val color: Int,
    val status: Int,
    val message: String,
    val isActive: Boolean
)

// To db

fun Alert.toDb() = AlertDb(
    id = id,
    exchange = exchange.toDb(),
    currencyPair = currencyPair.toDb(),
    time = time.toEpochMilliseconds(),
    price = price.toPlainString(),
    precision = precision,
    color = color,
    status = status.ordinal,
    message = message,
    isActive = isActive
)

// To domain

fun AlertDb.toDomain() = Alert(
    id = id,
    exchange = exchange.toDomain(),
    currencyPair = currencyPair.toDomain(),
    time = Instant.fromEpochMilliseconds(time),
    price = BigDecimal(price),
    precision = precision,
    color = color,
    status = Alert.Status.values()[status],
    message = message,
    isActive = isActive
)
