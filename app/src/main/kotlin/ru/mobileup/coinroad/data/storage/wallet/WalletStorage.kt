package ru.mobileup.coinroad.data.storage.wallet

import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.domain.common.Connection

interface WalletStorage {

    fun observeWallets(): Flow<List<Connection.Wallet>>

    suspend fun deleteWallet(walletId: String)

    suspend fun saveWallet(wallet: Connection.Wallet)
}