package ru.mobileup.coinroad.ui.main

import ru.mobileup.coinroad.navigation.alerts.OpenAlertsScreen
import ru.mobileup.coinroad.navigation.settings.OpenSettingsScreen
import ru.mobileup.coinroad.ui.base.BaseViewModel

class MainViewModel : BaseViewModel() {

    fun onAlertsClicked() {
        navigate(OpenAlertsScreen)
    }

    fun onSettingsClicked() {
        navigate(OpenSettingsScreen)
    }
}