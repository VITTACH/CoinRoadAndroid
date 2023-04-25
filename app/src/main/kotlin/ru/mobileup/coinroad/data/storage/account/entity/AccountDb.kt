package ru.mobileup.coinroad.data.storage.account.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant
import ru.mobileup.coinroad.data.storage.graph.entity.ExchangeDb
import ru.mobileup.coinroad.data.storage.graph.entity.toDb
import ru.mobileup.coinroad.data.storage.graph.entity.toDomain
import ru.mobileup.coinroad.domain.common.Connection

@Entity(tableName = "accounts")
data class AccountDb(
    @PrimaryKey val id: String,
    @Embedded(prefix = "exchange_") val exchange: ExchangeDb,
    val lastSync: Long,
    val isEnabled: Boolean
)

// To db
fun Connection.Account.toDb() = AccountDb(
    id = id,
    exchange = exchange.toDb(),
    lastSync = lastSyncTime?.toEpochMilliseconds() ?: 0,
    isEnabled = isEnabled
)

// To domain
fun AccountDb.toDomain() = Connection.Account(
    id = id,
    exchange = exchange.toDomain(),
    lastSyncTime = Instant.fromEpochMilliseconds(lastSync),
    isEnabled = isEnabled
)
