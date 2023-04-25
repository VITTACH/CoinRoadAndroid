package ru.mobileup.coinroad.data.storage.alert

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.data.storage.alert.entity.AlertDb

@Dao
interface AlertDao {

    @Query("SELECT * FROM alerts")
    fun observeAlerts(): Flow<List<AlertDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alertDb: AlertDb)

    @Query("DELETE FROM alerts WHERE id = :alertId")
    suspend fun deleteAlert(alertId: String)
}