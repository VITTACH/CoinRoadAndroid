package ru.mobileup.coinroad.data.storage.graph.chart

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class InMemoryChartBitmapStorage : ChartBitmapStorage {

    private val bitmapMap = MutableStateFlow<Map<Int, ByteArray>>(emptyMap())
    private val mutex = Mutex()

    override fun observeBitmap(id: Int): Flow<ByteArray> =
        bitmapMap.map { it[id] }
            .filterNotNull()
            .distinctUntilChanged()

    override fun observeIds() = bitmapMap.map { it.keys }

    override suspend fun saveBitmap(id: Int, bytes: ByteArray) {
        mutex.withLock {
            bitmapMap.value = bitmapMap.value.toMutableMap()
                .apply { put(id, bytes) }
        }
    }

    override suspend fun removeById(id: Int) {
        mutex.withLock {
            bitmapMap.value = bitmapMap.value.toMutableMap()
                .apply { remove(id) }
        }
    }
}