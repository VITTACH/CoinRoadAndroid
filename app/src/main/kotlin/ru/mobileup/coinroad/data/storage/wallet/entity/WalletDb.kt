package ru.mobileup.coinroad.data.storage.wallet.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.mobileup.coinroad.domain.common.Connection

@Entity(tableName = "wallets")
data class WalletDb(
    @PrimaryKey val id: String
)

// To db
fun Connection.Wallet.toDb() = WalletDb(
    id = id
)

// To domain
fun WalletDb.toDomain() = Connection.Wallet(
    id = id
)
