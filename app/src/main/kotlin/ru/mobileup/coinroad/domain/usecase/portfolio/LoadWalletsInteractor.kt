package ru.mobileup.coinroad.domain.usecase.portfolio

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import ru.mobileup.coinroad.data.storage.wallet.WalletStorage
import ru.mobileup.coinroad.domain.common.Connection

class LoadWalletsInteractor(
    private val walletStorage: WalletStorage
) {
    suspend fun load(fresh: Boolean = false): List<Connection.Wallet> = coroutineScope {
        walletStorage.observeWallets().first()
    }

    fun observe(): Flow<List<Connection.Wallet>> {
        return walletStorage.observeWallets()
    }
}