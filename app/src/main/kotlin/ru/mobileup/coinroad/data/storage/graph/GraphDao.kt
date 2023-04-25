package ru.mobileup.coinroad.data.storage.graph

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.data.storage.graph.entity.GraphDb

@Dao
interface GraphDao {

    @Query("SELECT * FROM graphs")
    fun observeGraphs(): Flow<List<GraphDb>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGraph(graphDb: GraphDb)

    @Query("DELETE FROM graphs WHERE id = :graphId")
    suspend fun deleteGraph(graphId: String)
}