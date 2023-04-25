package ru.mobileup.coinroad.data.storage.widget

import kotlinx.coroutines.flow.Flow
import ru.mobileup.coinroad.domain.common.Widget

interface WidgetStorage {

    fun observeWidgets(): Flow<List<Widget>>

    suspend fun saveWidget(widget: Widget)

    suspend fun deleteWidget(widgetId: Int)

    fun readPeriod(): Flow<Int>

    suspend fun savePeriod(period: Int)
}