package ru.mobileup.coinroad.ui.graph_editing

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenGraphCreatingBinding
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.ui.advert.bindView
import ru.mobileup.coinroad.ui.advert.onPurchase
import ru.mobileup.coinroad.ui.base.BaseScreen
import ru.mobileup.coinroad.ui.graph.ChartType
import ru.mobileup.coinroad.ui.graph.CustomChartView.Companion.PREVIEW_ID
import ru.mobileup.coinroad.ui.graph.toGraphItem
import ru.mobileup.coinroad.ui.graph.toPreviewGraphItem
import ru.mobileup.coinroad.ui.graph_creating.PreviewContent
import ru.mobileup.coinroad.ui.graph_creating.toGroupieItems
import ru.mobileup.coinroad.util.system.dataOrNull
import ru.mobileup.coinroad.util.system.serializableArg
import ru.mobileup.coinroad.util.ui.*

class GraphEditingScreen : BaseScreen<GraphEditingViewModel>(R.layout.screen_graph_creating) {

    var graph: Graph by serializableArg()
    override val vm: GraphEditingViewModel by viewModel { parametersOf(graph) }

    private val binding by viewBinding(ScreenGraphCreatingBinding::bind)

    private val timeToggleAdapter = GroupAdapter<GroupieViewHolder>()
    private val timeToggleSection = Section()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(requireActivity())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews(activity: Activity) = with(binding) {
        graphTouchArea.setOnTouchListener { _, event -> graphPlot.dispatchTouchEvent(event); true }
        graphGroup.setOnScrollChangeListener { _, _, scrollY, _, _ -> onGraphScreenScroll(scrollY) }

        toolbar.title = graph.exchange.name
        toolbar.setNavigationIcon(R.drawable.ic_24_close)
        toolbar.setNavigationOnClickListener { vm.navigateBack() }

        with(timeToggleList) {
            timeToggleAdapter.clear()
            timeToggleAdapter.add(timeToggleSection)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = timeToggleAdapter
        }

        with(bottomView) {
            accentButton.isVisible = true
            accentButton.setText(R.string.editing_save_changes)
            accentButton.doOnClick { vm.onEditButtonClicked() }
        }

        with(chartTypeSpinner) {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    vm.onChartTypeChanged(ChartType.values()[pos])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }

        firstCurrencyButton.root.isVisible = false
        secondCurrencyButton.root.isVisible = false

        deleteButton.doOnClick { vm.onDeleteButtonClicked() }
        deleteButton.isVisible = true

        graphTouchArea.isVisible = true
        graphGroup.isVisible = true

        fullScreenBtn.doOnClick { onChartHeightChange(vm.getNewChartMode()) }

        createAlertBtn.doOnClick { vm.onNewAlertClicked() }

        vm::chartType bind { chartTypeSpinner.setSelection(it.ordinal) }

        vm::advertsData bind {
            banner.bindView(it.dataOrNull) { period, purchased ->
                activity.onPurchase(period, purchased)
            }
        }

        vm::previewGraphContent bind { state ->
            if (state is PreviewContent.PreviewGraph) {
                val graphDataItem = with(state.graph) {
                    if (this.graphData != null) {
                        this.toGraphItem()
                    } else this.toPreviewGraphItem()
                }
                graphPlot.updatePlot(PREVIEW_ID, graphDataItem)

                if (state.graph.isMinMaxVisible) {
                    minMaxBtn.setPrimaryColor()
                } else minMaxBtn.setSecondaryColor()
                if (state.graph.isAlertsVisible) {
                    alertsBtn.setPrimaryColor()
                } else alertsBtn.setSecondaryColor()
                if (state.graph.isTickerVisible) {
                    tickerBtn.setPrimaryColor()
                } else tickerBtn.setSecondaryColor()
            }
        }

        tickerBtn.doOnClick { vm.onTickerChanged() }
        alertsBtn.doOnClick { vm.onAlertsChanged() }
        minMaxBtn.doOnClick { vm.onMinMaxChanged() }

        vm::timeStepState bind { time ->
            val offset = resources.getDimension(R.dimen.medium_gap).toInt()
            val manager = timeToggleList.layoutManager
            (manager as LinearLayoutManager).scrollToPositionWithOffset(time.ordinal, offset)

            timeToggleSection.update(TimeStep.values().toGroupieItems(time) {
                vm.onTimeToggleButtonClicked(it)
            })
        }

        vm::editButtonVisible bind { bottomView.root.isVisible = it }

        vm.deleteDialog bind { _, dc ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_confirm)
                .setPositiveButton(R.string.editing_delete) { _, _ ->
                    dc.sendResult(Unit)
                }
                .setNegativeButton(R.string.cancel, null)
                .create()
        }
    }
}