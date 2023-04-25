package ru.mobileup.coinroad.domain.usecase.alert

import android.graphics.Color
import kotlinx.datetime.Instant
import ru.mobileup.coinroad.data.storage.alert.AlertStorage
import ru.mobileup.coinroad.domain.common.Alert
import ru.mobileup.coinroad.domain.common.CurrencyPair
import ru.mobileup.coinroad.domain.common.Exchange
import java.math.BigDecimal

class EditAlertsInteractor(
    private val storage: AlertStorage
) {

    suspend fun updateAlert(alert: Alert) = storage.saveAlert(alert)

    suspend fun deleteAlert(alertId: String) = storage.deleteAlert(alertId)

    suspend fun createAlert(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        price: BigDecimal,
        precision: Int,
        message: String = "",
    ): Alert {
        val time = System.currentTimeMillis()
        val alert = Alert(
            id = time.toString(),
            exchange = exchange,
            currencyPair = currencyPair,
            time = Instant.fromEpochMilliseconds(time),
            price = price,
            color = Color.rgb((30..200).random(), (30..200).random(), (30..200).random()),
            precision = precision,
            status = Alert.Status.ENABLED,
            message = message,
            isActive = true
        )
        storage.saveAlert(alert)
        return alert
    }
}