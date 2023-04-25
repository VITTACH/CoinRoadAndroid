package ru.mobileup.coinroad.data.storage.widget

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.data.storage.widget.entity.WidgetDb

@Dao
interface WidgetDao {

    @Query("SELECT * FROM widgets")
    fun observeWidgets(): Flow<List<WidgetDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widgetDb: WidgetDb)

    @Query("DELETE FROM widgets WHERE id = :widgetId")
    suspend fun deleteWidget(widgetId: Int)
}