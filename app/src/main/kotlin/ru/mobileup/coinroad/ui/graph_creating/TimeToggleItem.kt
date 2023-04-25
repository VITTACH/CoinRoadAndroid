package ru.mobileup.coinroad.ui.graph_creating

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemTimeToggleBinding
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.util.ui.doOnClick
import ru.mobileup.coinroad.util.ui.setPrimaryColor
import ru.mobileup.coinroad.util.ui.setSecondaryColor

class TimeToggleItem(
    private val timeStep: TimeStep,
    private val currentTimeStep: TimeStep,
    private val onTimeClicked: (timeStep: TimeStep) -> Unit,
) : BindableItem<ItemTimeToggleBinding>() {

    override fun getLayout() = R.layout.item_time_toggle

    override fun getId(): Long = timeStep.ordinal.toLong()

    override fun initializeViewBinding(view: View) = ItemTimeToggleBinding.bind(view)

    override fun bind(item: ItemTimeToggleBinding, position: Int) = with(item) {
        button.text = timeStep.toString(button.context)
        button.doOnClick { onTimeClicked.invoke(timeStep) }
        if (timeStep == currentTimeStep) {
            button.setPrimaryColor()
        } else {
            button.setSecondaryColor()
        }
    }
}

fun Array<TimeStep>.toGroupieItems(
    currentTimeStep: TimeStep,
    onTimeClicked: (timeStep: TimeStep) -> Unit
): List<TimeToggleItem> {
    return map { TimeToggleItem(it, currentTimeStep, onTimeClicked) }
}
