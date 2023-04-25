package ru.mobileup.coinroad.ui.settings

import android.graphics.Bitmap
import androidx.lifecycle.viewModelScope
import me.aartikov.sesame.loading.simple.OrdinaryLoading
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.property.*
import ru.mobileup.coinroad.domain.usecase.graph.LoadGraphsInteractor
import ru.mobileup.coinroad.domain.usecase.settings.*
import ru.mobileup.coinroad.navigation.settings.ReOpenSettingsScreen
import ru.mobileup.coinroad.navigation.system.OpenAboutScreen
import ru.mobileup.coinroad.ui.base.BaseViewModel
import ru.mobileup.coinroad.ui.graph_creating.GraphPreview
import ru.mobileup.coinroad.util.system.dataOrNull

class SettingsViewModel(
    private val getSettingsInteractor: GetSettingsInteractor,
    private val editChartHeightInteractor: EditChartHeightInteractor,
    private val alertSensitiveInteractor: AlertSensitiveInteractor,
    private val updateThemeInteractor: UpdateThemeInteractor,
    private val updatePeriodInteractor: UpdatePeriodInteractor,
    private val saveScreenshotInteractor: SaveScreenshotInteractor,
    getScreenshotInteractor: GetScreenshotInteractor,
    loadGraphsInteractor: LoadGraphsInteractor
) : BaseViewModel() {

    private val settingsLoading = OrdinaryLoading(viewModelScope, getSettingsInteractor::load)
    val settingsState by stateFromFlow(settingsLoading.stateFlow)

    private val graphPreview = GraphPreview(viewModelScope, loadGraphsInteractor)
    val previewGraphContent by computed(graphPreview::graphPreview) { it }

    val onRestartWm = command<Unit>()

    var chartHeight by state(0)
    var updatePeriod by state(0)

    var screenshot: Bitmap? = getScreenshotInteractor.execute()
        set(value) {
            saveScreenshotInteractor.execute(value)
            field = value
        }

    init {
        settingsLoading.handleErrors(viewModelScope) { handleException(it.throwable) }

        autorun(::settingsState) {
            chartHeight = it.dataOrNull?.chartHeight ?: 0
            updatePeriod = it.dataOrNull?.updatePeriod ?: 0
        }
    }

    override fun onActive() {
        super.onActive()

        settingsLoading.load(false)
    }

    fun onAboutClicked() {
        navigate(OpenAboutScreen)
    }

    fun onUpdatePeriodChanged(progress: Int) = safeLaunch {
        updatePeriod = progress
        updatePeriodInteractor.execute(progress)
    }

    fun onThemeChanged(isDarkMode: Boolean) = safeLaunch {
        updateThemeInteractor.execute(isDarkMode)
        navigate(ReOpenSettingsScreen)
    }

    fun onAlertSensitiveChanged(progress: Int) = safeLaunch {
        alertSensitiveInteractor.execute(progress)
    }

    fun onChartHeightChanged(progress: Int) = safeLaunch {
        chartHeight = progress
        editChartHeightInteractor.execute(chartHeight)
        onRestartWm(Unit)
    }
}