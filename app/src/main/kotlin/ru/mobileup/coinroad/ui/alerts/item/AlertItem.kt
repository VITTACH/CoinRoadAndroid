package ru.mobileup.coinroad.ui.alerts.item

import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.view.isVisible
import com.xwray.groupie.viewbinding.BindableItem
import kotlinx.datetime.Instant
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemAlertBinding
import ru.mobileup.coinroad.domain.common.Alert
import ru.mobileup.coinroad.util.formatLocale
import ru.mobileup.coinroad.util.formatNumber
import ru.mobileup.coinroad.util.iconResource
import ru.mobileup.coinroad.util.ui.doOnClick
import ru.mobileup.coinroad.util.ui.getColorFromAttr

class AlertItem(
    private val alert: Alert,
    private val updateAlert: (alert: Alert) -> Unit
) : BindableItem<ItemAlertBinding>() {

    override fun getLayout() = R.layout.item_alert

    override fun getId(): Long = alert.id.toLong()

    override fun initializeViewBinding(view: View) = ItemAlertBinding.bind(view)

    override fun bind(binding: ItemAlertBinding, itemPosition: Int) = with(binding) {
        exchangeIcon.setImageResource(alert.exchange.iconResource)
        exchange.text = alert.exchange.name

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.LEFT_RIGHT,
            intArrayOf(alert.color, exchange.context.getColorFromAttr(R.attr.windowBackgroundColor))
        ).apply {
            cornerRadius = exchange.context.resources.getDimension(R.dimen.small_gap)
        }

        colorView.background = gradientDrawable

        symbol.text = alert.currencyPair.toString()
        time.text = formatLocale(time.context, alert.time.toEpochMilliseconds(), " ")
        price.text = formatNumber(alert.price, alert.precision)

        message.isVisible = alert.message.isNotEmpty()
        message.text = alert.message

        enableSwitch.setOnCheckedChangeListener(null)
        enableSwitch.isChecked = alert.isActive
        enableSwitch.jumpDrawablesToCurrentState()
        enableSwitch.setOnCheckedChangeListener { _, isChecked ->
            val alertStatus = if (isChecked) {
                Alert.Status.ENABLED
            } else Alert.Status.DISABLED
            updateAlert.invoke(
                alert.copy(
                    isActive = isChecked,
                    status = alertStatus,
                    time = Instant.fromEpochMilliseconds(System.currentTimeMillis())
                )
            )

            updateStatus(binding, alertStatus)
        }

        updateStatus(binding, alert.status)

        root.doOnClick { enableSwitch.isChecked = !enableSwitch.isChecked }
    }

    private fun updateStatus(binding: ItemAlertBinding, alertStatus: Alert.Status) = with(binding) {
        when (alertStatus) {
            Alert.Status.ENABLED -> status.setText(R.string.alert_enabled)
            Alert.Status.DISABLED -> status.setText(R.string.alert_disabled)
            Alert.Status.FILLED -> status.setText(R.string.alert_filled)
            else -> {
                // Nothing
            }
        }
    }
}

fun List<Alert>.toGroupieItems(
    updateAlert: (alert: Alert) -> Unit
): List<AlertItem> {
    return map { AlertItem(it, updateAlert) }
}