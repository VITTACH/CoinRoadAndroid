package ru.mobileup.coinroad.data.storage.widget.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.mobileup.coinroad.data.storage.graph.entity.CurrencyPairDb
import ru.mobileup.coinroad.data.storage.graph.entity.ExchangeDb
import ru.mobileup.coinroad.data.storage.graph.entity.toDb
import ru.mobileup.coinroad.data.storage.graph.entity.toDomain
import ru.mobileup.coinroad.domain.common.Widget

@Entity(tableName = "widgets")
data class WidgetDb(
    @PrimaryKey val id: Int,
    @Embedded(prefix = "exchange_") val exchange: ExchangeDb,
    @Embedded(prefix = "currencyPair_") val currencyPair: CurrencyPairDb
)

// To db

fun Widget.toDb() = WidgetDb(
    id = id,
    exchange = exchange.toDb(),
    currencyPair = currencyPair.toDb()
)

// To domain

fun WidgetDb.toDomain() = Widget(
    id = id,
    exchange = exchange.toDomain(),
    currencyPair = currencyPair.toDomain()
)
