package ru.mobileup.coinroad.ui.base.widget

import android.content.res.ColorStateList
import android.view.View
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.LayoutCurrencyButtonBinding
import ru.mobileup.coinroad.util.ui.doOnClick
import ru.mobileup.coinroad.util.ui.getColorFromAttr
import ru.mobileup.coinroad.util.ui.setClickableActive

enum class CurrencyButtonState {
    SELECTING,
    SELECTED,
    DISABLED
}

fun LayoutCurrencyButtonBinding.setState(state: CurrencyButtonState) = when(state) {
    CurrencyButtonState.SELECTING -> with(button) {
        setTextColor(button.context.getColorFromAttr(R.attr.textColor))
        setClickableActive(false)
        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        val secondaryColor = button.context.getColorFromAttr(R.attr.secondaryButtonBackgroundColor)
        backgroundTintList = ColorStateList.valueOf(secondaryColor)
    }
    CurrencyButtonState.SELECTED -> with(button) {
        setTextColor(button.context.getColorFromAttr(R.attr.textColorSelected))
        setClickableActive(true)
        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_24_close, 0)
        val primaryColor = button.context.getColorFromAttr(R.attr.buttonBackgroundColor)
        backgroundTintList = ColorStateList.valueOf(primaryColor)
    }
    CurrencyButtonState.DISABLED -> with(button) {
        setTextColor(button.context.getColorFromAttr(R.attr.fadeTextColor))
        setClickableActive(false)
        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
        val secondaryColor = button.context.getColorFromAttr(R.attr.secondaryButtonBackgroundColor)
        backgroundTintList = ColorStateList.valueOf(secondaryColor)
    }
}

fun LayoutCurrencyButtonBinding.doOnClick(listener: (view: View) -> Unit) {
    button.doOnClick { listener.invoke(button) }
}

fun LayoutCurrencyButtonBinding.setText(text: String) {
    button.text = text
}