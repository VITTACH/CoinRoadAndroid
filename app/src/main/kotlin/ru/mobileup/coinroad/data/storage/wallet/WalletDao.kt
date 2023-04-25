package ru.mobileup.coinroad.data.storage.wallet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.data.storage.wallet.entity.WalletDb

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallets")
    fun observeWallets(): Flow<List<WalletDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallet(walletDb: WalletDb)

    @Query("DELETE FROM wallets WHERE id = :walletId")
    suspend fun deleteWallet(walletId: String)
}