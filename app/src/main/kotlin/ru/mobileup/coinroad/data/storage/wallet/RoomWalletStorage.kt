package ru.mobileup.coinroad.data.storage.wallet

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.mobileup.coinroad.data.storage.wallet.entity.toDb
import ru.mobileup.coinroad.data.storage.wallet.entity.toDomain
import ru.mobileup.coinroad.domain.common.Connection

class RoomWalletStorage(private val walletDao: WalletDao) : WalletStorage {

    private val mutex = Mutex()

    override fun observeWallets(): Flow<List<Connection.Wallet>> {
        return walletDao.observeWallets().map { db ->
            db.map { it.toDomain() }
        }
    }

    override suspend fun deleteWallet(walletId: String) {
        walletDao.deleteWallet(walletId)
    }

    override suspend fun saveWallet(wallet: Connection.Wallet) {
        mutex.withLock {
            walletDao.insertWallet(wallet.toDb())
        }
    }
}