package ru.mobileup.coinroad.data.storage.account

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.data.storage.account.entity.AccountDb

@Dao
interface AccountDao {

    @Query("SELECT * FROM accounts")
    fun observeAccounts(): Flow<List<AccountDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(accountDb: AccountDb)

    @Query("DELETE FROM accounts WHERE id = :accountId")
    suspend fun deleteAccount(accountId: String)
}