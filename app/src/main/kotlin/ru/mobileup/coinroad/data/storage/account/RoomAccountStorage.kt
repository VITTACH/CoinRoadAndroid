package ru.mobileup.coinroad.data.storage.account

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.mobileup.coinroad.data.storage.account.entity.toDb
import ru.mobileup.coinroad.data.storage.account.entity.toDomain
import ru.mobileup.coinroad.domain.common.Connection

class RoomAccountStorage(private val accountDao: AccountDao) : AccountStorage {

    private val mutex = Mutex()

    override fun observeAccounts(): Flow<List<Connection.Account>> {
        return accountDao.observeAccounts().map { db ->
            db.map { it.toDomain() }
        }
    }

    override suspend fun deleteAccount(accountId: String) {
        accountDao.deleteAccount(accountId)
    }

    override suspend fun saveAccount(account: Connection.Account) {
        mutex.withLock {
            accountDao.insertAccount(account.toDb())
        }
    }
}