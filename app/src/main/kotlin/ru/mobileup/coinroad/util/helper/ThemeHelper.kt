package ru.mobileup.coinroad.util.helper

import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.util.ui.getColorFromAttr

@Suppress("DEPRECATION")
private fun Activity.setLightStatusBar(isDarkStatusBar: Boolean = false) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            if (isDarkStatusBar) 0 else APPEARANCE_LIGHT_STATUS_BARS,
            APPEARANCE_LIGHT_STATUS_BARS
        )
    } else {
        var flags = window.decorView.systemUiVisibility
        val mask = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        flags = if (isDarkStatusBar) flags and mask.inv() else flags or mask
        window.decorView.systemUiVisibility = flags
    }
}

@Suppress("DEPRECATION")
private fun Activity.setLightNavigationBar(isDarkNavbar: Boolean = false) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.insetsController?.setSystemBarsAppearance(
            if (isDarkNavbar) 0 else APPEARANCE_LIGHT_NAVIGATION_BARS,
            APPEARANCE_LIGHT_NAVIGATION_BARS
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        var flags = window.decorView.systemUiVisibility
        val mask = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        flags = if (isDarkNavbar) flags and mask.inv() else flags or mask
        window.decorView.systemUiVisibility = flags
    }
}

fun Activity.updateSystemUI(isNightMode: Boolean) {
    with(window) {
        navigationBarColor = getColorFromAttr(R.attr.navbarBackgroundColor)
        statusBarColor = getColorFromAttr(R.attr.statusBarColor)
    }
    setLightStatusBar(isNightMode)
    setLightNavigationBar(isNightMode)
}