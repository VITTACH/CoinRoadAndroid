package ru.mobileup.coinroad.data.storage.account

import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.domain.common.Connection

interface AccountStorage {

    fun observeAccounts(): Flow<List<Connection.Account>>

    suspend fun deleteAccount(accountId: String)

    suspend fun saveAccount(account: Connection.Account)
}