package ru.mobileup.coinroad.data.storage.graph.chart

import kotlinx.coroutines.flow.Flow

interface ChartBitmapStorage {
    fun observeIds(): Flow<Set<Int>>
    fun observeBitmap(id: Int): Flow<ByteArray>
    suspend fun saveBitmap(id: Int, bytes: ByteArray)
    suspend fun removeById(id: Int)
}
