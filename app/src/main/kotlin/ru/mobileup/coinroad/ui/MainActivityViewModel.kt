package ru.mobileup.coinroad.ui

import androidx.lifecycle.viewModelScope
import me.aartikov.sesame.loading.simple.FlowLoading
import me.aartikov.sesame.loading.simple.handleErrors
import me.aartikov.sesame.property.stateFromFlow
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.analytics.AnalyticsManager
import ru.mobileup.coinroad.analytics.system.ReportFirstLaunch
import ru.mobileup.coinroad.data.storage.amplitude.AmplitudeStorage
import ru.mobileup.coinroad.domain.usecase.graph.HasGraphsInteractor
import ru.mobileup.coinroad.domain.usecase.settings.GetSettingsInteractor
import ru.mobileup.coinroad.navigation.system.OpenExchangeChoosingScreen
import ru.mobileup.coinroad.navigation.system.OpenMainScreen
import ru.mobileup.coinroad.ui.base.BaseViewModel
import ru.mobileup.coinroad.ui.base.SnackbarAction

class MainActivityViewModel(
    private val hasGraphsInteractor: HasGraphsInteractor,
    private val getSettingsInteractor: GetSettingsInteractor,
    private val analyticsManager: AnalyticsManager,
    private val amplitudeStorage: AmplitudeStorage
) : BaseViewModel() {

    private val settingsLoading = FlowLoading(
        scope = viewModelScope,
        load = getSettingsInteractor::load,
        observe = getSettingsInteractor::observe
    )
    val settingsState by stateFromFlow(settingsLoading.stateFlow)

    init {
        settingsLoading.handleErrors(viewModelScope) { handleException(it.throwable) }
    }

    override fun onActive() {
        super.onActive()

        settingsLoading.load(false)
    }

    fun showUpdateComplete(action: () -> Unit = {}) {
        val message = resourceHelper.getString(R.string.update_downloaded)
        val actionTitle = resourceHelper.getString(R.string.update_install)
        showActionSnackbar(SnackbarAction(message, actionTitle) { action.invoke() })
    }

    fun showUpdateRetry(action: () -> Unit = {}) {
        val message = resourceHelper.getString(R.string.unable_download)
        val actionTitle = resourceHelper.getString(R.string.update_retry)
        showActionSnackbar(SnackbarAction(message, actionTitle) { action.invoke() })
    }

    fun appLaunched() = safeLaunch {
        if (amplitudeStorage.isFirstLaunch()) {
            analyticsManager.handleAnalyticMessage(ReportFirstLaunch)
        }

        if (hasGraphsInteractor.invoke()) {
            navigate(OpenMainScreen)
        } else {
            navigate(OpenExchangeChoosingScreen)
        }
    }
}