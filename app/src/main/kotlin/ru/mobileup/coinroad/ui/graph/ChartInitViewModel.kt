package ru.mobileup.coinroad.ui.graph

import me.aartikov.sesame.property.command
import ru.mobileup.coinroad.domain.usecase.settings.GetSettingsInteractor
import ru.mobileup.coinroad.ui.base.BaseViewModel

class ChartInitViewModel(
    private val getSettingsInteractor: GetSettingsInteractor
) : BaseViewModel() {

    val onChartHeightInited = command<Int>()

    init {
        safeLaunch {
            onChartHeightInited(getSettingsInteractor.load().chartHeight)
        }
    }
}