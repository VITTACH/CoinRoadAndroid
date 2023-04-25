package ru.mobileup.coinroad.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.skydoves.balloon.ArrowOrientation
import com.xwray.groupie.GroupieAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenDashboardBinding
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.ui.base.BaseScreen
import ru.mobileup.coinroad.ui.dashboard.content.GraphsContent
import ru.mobileup.coinroad.util.isRunning
import ru.mobileup.coinroad.util.startWork
import ru.mobileup.coinroad.util.ui.createShowCases
import ru.mobileup.coinroad.util.ui.doOnClick
import ru.mobileup.coinroad.util.ui.textChanges

class DashboardScreen : BaseScreen<DashboardViewModel>(R.layout.screen_dashboard) {

    private val binding by viewBinding(ScreenDashboardBinding::bind)
    private val graphAdapter = GroupieAdapter()
    override val vm: DashboardViewModel by viewModel()

    private val pushVisibleShowCase by lazy {
        requireActivity().createShowCases(
            titleText = resources.getText(R.string.showcase_graph_visible),
            actionText = resources.getText(R.string.showcase_got_it),
            orientation = ArrowOrientation.TOP,
            arrowPosition = 0.9f,
            emsText = 10,
            preferenceName = "graph_visible_couching"
        )
    }

    private val graphs = mutableListOf<Graph>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val context = requireContext()
        initViews(context)
    }

    private fun initViews(context: Context) = with(binding) {

        recyclerView.adapter = graphAdapter

        with(bottomView) {
            primaryButton.doOnClick { vm.onNewGraphClicked() }
            primaryButton.setText(R.string.home_new_graph)
            primaryButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_24_plus, 0)

            searchEditText.hint = getString(R.string.search_charts_hint)
            searchEditText.textChanges()
                .onEach { vm.onSearchInput(it.toString()) }
                .launchIn(lifecycleScope)

            actionButton.isVisible = true
            actionButton.doOnClick {
                searchInput.isVisible = !searchInput.isVisible
            }
        }

        vm.selectDeepLinkDialog bind { (items, initIndex), dc ->
            installedAppsLoading.isVisible = false
            var resultIndex = initIndex

            MaterialAlertDialogBuilder(context, R.style.Widget_Coinroad_CustomAppsDialog)
                .setTitle(getString(R.string.home_deeplink_dialog_title))
                .setSingleChoiceItems(items, initIndex) { _, curIndex -> resultIndex = curIndex }
                .setPositiveButton(getString(R.string.ok)) { _, _ -> dc.sendResult(resultIndex) }
                .setNegativeButton(getString(R.string.cancel), null)
                .create()
        }

        vm.promoFeaturesDialog bind { _, dc ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.features_promo_title)
                .setMessage(R.string.features_promo_message)
                .setPositiveButton(R.string.ok) { _, _ ->
                    dc.sendResult(Unit)
                }
                .create()
        }

        vm::filteredGraphState bind { state ->
            if (state is GraphsContent.Data) {
                if (state.refreshing) restartWorkManager(state)
                graphs.clear()
                graphs.addAll(state.graphs.sortedBy { it.id })
                graphAdapter.replaceAll(
                    graphs.toGroupieItems(
                        pushVisibleShowCase = pushVisibleShowCase,
                        onGraphClicked = { vm.onGraphClicked(it) },
                        onVisibleChanged = {
                            vm.onVisibleChanged(it)
                            restartWorkManager(
                                state = state,
                                force = true
                            )
                        },
                        onDeepLinksClicked = {
                            installedAppsLoading.isVisible = true
                            vm.onDeepLinkClicked(it)
                        }
                    )
                )
            } else if (state !is GraphsContent.Loading) {
                if (state is GraphsContent.Cancel && graphs.isNotEmpty()) {
                    cancelNotificationChannel()
                    graphs.clear()
                }
                graphAdapter.replaceAll(emptyList())
            }

            bottomView.actionButton.isVisible = graphs.isNotEmpty()
        }
    }

    private fun restartWorkManager(state: GraphsContent.Data, force: Boolean = false) {
        val ids = state.graphs.map { it.id }

        val isUpdating = !(ids.containsAll(vm.curGraphIds) || vm.curGraphIds.isEmpty())
                || !workManager.isRunning()
                || vm.isFirstOpen
                || force

        if (isUpdating) {
            cancelNotificationChannel()
            workManager.startWork()
            vm.isFirstOpen = false
        }

        vm.curGraphIds.apply { clear(); addAll(ids) }
    }
}