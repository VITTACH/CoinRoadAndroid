package ru.mobileup.coinroad.domain.usecase.portfolio

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.data.storage.account.AccountStorage
import ru.mobileup.coinroad.domain.common.Connection

class LoadAccountsInteractor(
    private val accountStorage: AccountStorage
) {
    suspend fun load(fresh: Boolean = false): List<Connection.Account> = coroutineScope {
        accountStorage.observeAccounts().first()
    }

    fun observe(): Flow<List<Connection.Account>> {
        return accountStorage.observeAccounts()
    }
}