package ru.mobileup.coinroad.ui.dashboard

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.aartikov.sesame.dialog.DialogControl
import me.aartikov.sesame.loading.simple.*
import me.aartikov.sesame.property.computed
import me.aartikov.sesame.property.state
import me.aartikov.sesame.property.stateFromFlow
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.analytics.AnalyticsManager
import ru.mobileup.coinroad.analytics.exchange.ReportExchangeSelect
import ru.mobileup.coinroad.analytics.push.ReportPushVisibility
import ru.mobileup.coinroad.domain.analytics.PushModel
import ru.mobileup.coinroad.domain.common.DeepLink
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.domain.usecase.graph.EditGraphsInteractor
import ru.mobileup.coinroad.domain.usecase.graph.LoadGraphsInteractor
import ru.mobileup.coinroad.domain.usecase.settings.GetSettingsInteractor
import ru.mobileup.coinroad.domain.usecase.settings.FeaturesInteractor
import ru.mobileup.coinroad.navigation.graphs.OpenGraphCreateScreen
import ru.mobileup.coinroad.navigation.graphs.OpenGraphEditScreen
import ru.mobileup.coinroad.navigation.system.OpenSelectExchangeDialog
import ru.mobileup.coinroad.ui.base.BaseViewModel
import ru.mobileup.coinroad.ui.dashboard.content.GraphsContent

class DashboardViewModel(
    private val context: Context,
    private val analyticsManager: AnalyticsManager,
    private val loadGraphsInteractor: LoadGraphsInteractor,
    private val editGraphsInteractor: EditGraphsInteractor,
    private val getSettingsInteractor: GetSettingsInteractor,
    private val featuresInteractor: FeaturesInteractor
) : BaseViewModel() {

    private val graphLoading = FlowLoading(
        scope = viewModelScope,
        load = loadGraphsInteractor::load,
        observe = loadGraphsInteractor::observe
    )
    private val graphsState by stateFromFlow(graphLoading.stateFlow)

    private var currentFilter by state("")

    val filteredGraphState by computed(this::graphsState, this::currentFilter) { state, filter ->
        when (state) {
            is Loading.State.Data -> {
                val filteredGraphs =
                    state.data.filter { it.currencyPair.id.contains(filter, ignoreCase = true) }
                if (filteredGraphs.isEmpty()) {
                    GraphsContent.Empty
                } else GraphsContent.Data(filteredGraphs, state.refreshing)
            }

            is Loading.State.Loading -> GraphsContent.Loading

            else -> GraphsContent.Cancel
        }
    }

    val promoFeaturesDialog = DialogControl<Unit, Unit>()
    val selectDeepLinkDialog = DialogControl<Pair<Array<String>, Int>, Int>()

    val curGraphIds = mutableSetOf<String>()
    var isFirstOpen = true

    private val installedApps: List<ApplicationInfo> by lazy {
        context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.isCorrectApplication() }
            .toMutableList()
            .apply {
                sortBy { it.loadLabel(context.packageManager).toString() }
            }
    }

    private fun ApplicationInfo.isCorrectApplication(): Boolean {
        return !name.isNullOrBlank() && !packageName.isNullOrBlank() && isUserApplication()
    }

    private fun ApplicationInfo.isUserApplication(): Boolean {
        return flags and (ApplicationInfo.FLAG_UPDATED_SYSTEM_APP or ApplicationInfo.FLAG_SYSTEM) <= 0
    }

    private val installedAppNames: Array<String> by lazy {
        installedApps.map { it.loadLabel(context.packageManager).toString() }
            .toMutableList()
            .apply { add(0, context.getString(R.string.home_deeplink_nothing)) }
            .toTypedArray()
    }

    init {
        graphLoading.handleErrors(viewModelScope) { handleException(it.throwable) }

        safeLaunch {
            if (!getSettingsInteractor.load().isFeaturesShown) {
                featuresInteractor.execute()
                promoFeaturesDialog.showForResult(Unit)
            }
        }
    }

    override fun onActive() {
        super.onActive()

        graphLoading.load(false)
    }

    fun onSearchInput(filter: String) {
        currentFilter = filter
    }

    fun onNewGraphClicked() {
        navigate(OpenSelectExchangeDialog {
            analyticsManager.handleAnalyticMessage(ReportExchangeSelect(it))
            navigate(OpenGraphCreateScreen(it))
        })
    }

    fun onGraphClicked(graph: Graph) {
        navigate(OpenGraphEditScreen(graph))
    }

    fun onVisibleChanged(graph: Graph) = safeLaunch {
        val pushModel = PushModel(graph.currencyPair, graph.exchange, graph.timeStep)
        if (graph.isVisible) {
            analyticsManager.handleAnalyticMessage(ReportPushVisibility(pushModel, true))
        } else {
            analyticsManager.handleAnalyticMessage(ReportPushVisibility(pushModel, false))
        }

        editGraphsInteractor.updateGraph(graph)
    }

    fun onDeepLinkClicked(graph: Graph) {
        viewModelScope.launch(Dispatchers.IO) {
            val preSelectedIndex = graph.deepLink?.let { installedAppNames.indexOf(it.id) } ?: 0

            selectDeepLinkDialog.showForResult(installedAppNames to preSelectedIndex)
                ?.let { index ->
                    val deepLink = installedApps.getOrNull(index - 1)?.let {
                        DeepLink(it.loadLabel(context.packageManager).toString(), it.packageName)
                    }
                    editGraphsInteractor.updateGraph(graph.copy(deepLink = deepLink))
                }
        }
    }
}