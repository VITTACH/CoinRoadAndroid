package ru.mobileup.coinroad.domain.usecase.widget

import ru.mobileup.coinroad.data.storage.widget.WidgetStorage
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.Exchange
import ru.mobileup.coinroad.domain.common.Widget

class EditWidgetsInteractor(
    private val storage: WidgetStorage
) {

    suspend fun updateWidget(widget: Widget) = storage.saveWidget(widget)

    suspend fun deleteWidget(widgetId: Int) = storage.deleteWidget(widgetId)

    suspend fun createWidget(
        id: Int,
        exchange: Exchange,
        currencyPair: CurrencyPair
    ): Widget {
        val widget = Widget(
            id = id,
            exchange = exchange,
            currencyPair = currencyPair
        )
        storage.saveWidget(widget)
        return widget
    }
}