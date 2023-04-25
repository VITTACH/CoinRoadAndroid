package ru.mobileup.coinroad.ui.graph_creating

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenGraphCreatingBinding
import ru.mobileup.coinroad.domain.common.TimeStep
import ru.mobileup.coinroad.ui.advert.bindView
import ru.mobileup.coinroad.ui.advert.onPurchase
import ru.mobileup.coinroad.ui.base.BaseScreen
import ru.mobileup.coinroad.ui.base.widget.HeaderItem
import ru.mobileup.coinroad.ui.base.widget.doOnClick
import ru.mobileup.coinroad.ui.base.widget.setState
import ru.mobileup.coinroad.ui.base.widget.setText
import ru.mobileup.coinroad.ui.graph.ChartType
import ru.mobileup.coinroad.ui.graph.CustomChartView.Companion.PREVIEW_ID
import ru.mobileup.coinroad.ui.graph.toGraphItem
import ru.mobileup.coinroad.ui.graph.toPreviewGraphItem
import ru.mobileup.coinroad.util.system.dataOrNull
import ru.mobileup.coinroad.util.system.serializableArg
import ru.mobileup.coinroad.util.ui.*

class GraphCreatingScreen : BaseScreen<GraphCreatingViewModel>(R.layout.screen_graph_creating) {

    var data by serializableArg<GraphCreatingData>()

    override val vm: GraphCreatingViewModel by viewModel { parametersOf(data) }
    private val binding by viewBinding(ScreenGraphCreatingBinding::bind)

    private val secondCurrencyAdapter = GroupAdapter<GroupieViewHolder>()
    private val secondCurrencySection = Section()

    private val firstCurrencyAdapter = GroupAdapter<GroupieViewHolder>()
    private val firstCurrencySection = Section()

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

        toolbar.title = data.exchange.name
        toolbar.setNavigationIcon(R.drawable.ic_24_back)
        toolbar.setNavigationOnClickListener { vm.navigateBack() }

        with(timeToggleList) {
            timeToggleAdapter.clear()
            timeToggleAdapter.add(timeToggleSection)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = timeToggleAdapter
        }

        with(firstCurrencyList) {
            firstCurrencySection.setHeader(HeaderItem(R.string.creating_select_first_currency))
            firstCurrencyAdapter.clear()
            firstCurrencyAdapter.add(firstCurrencySection)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = firstCurrencyAdapter
        }

        with(secondCurrencyList) {
            secondCurrencySection.setHeader(HeaderItem(R.string.creating_select_second_currency))
            secondCurrencyAdapter.clear()
            secondCurrencyAdapter.add(secondCurrencySection)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = secondCurrencyAdapter
        }

        with(bottomView) {
            primaryButton.isVisible = false
            accentButton.setText(R.string.creating_add_graph)
            accentButton.doOnClick { vm.onAddGraphClicked() }

            searchEditText.textChanges()
                .onEach { vm.onSearchInput(it.toString()) }
                .launchIn(lifecycleScope)
        }

        vm::isTickerVisible bind {
            if (it) tickerBtn.setPrimaryColor() else tickerBtn.setSecondaryColor()
        }

        vm::isAlertsVisible bind {
            if (it) alertsBtn.setPrimaryColor() else alertsBtn.setSecondaryColor()
        }

        vm::isMinMaxVisible bind {
            if (it) minMaxBtn.setPrimaryColor() else minMaxBtn.setSecondaryColor()
        }

        tickerBtn.doOnClick { vm.onTickerChanged() }
        alertsBtn.doOnClick { vm.onAlertsChanged() }
        minMaxBtn.doOnClick { vm.onMinMaxChanged() }

        with(chartTypeSpinner) {
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(view: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                    vm.onChartTypeChanged(ChartType.values()[pos])
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }

        vm::previewGraphContent bind { state ->
            if (state is PreviewContent.PreviewGraph) {
                val graphDataItem = with(state.graph) {
                    if (this.graphData != null) this.toGraphItem() else this.toPreviewGraphItem()
                }

                graphPlot.updatePlot(PREVIEW_ID, graphDataItem, isHelpVisible = true)
            }
        }

        vm::chartType bind { chartTypeSpinner.setSelection(it.ordinal) }

        vm::firstCurrencyButtonText bind firstCurrencyButton::setText
        vm::firstCurrencyButtonState bind firstCurrencyButton::setState
        firstCurrencyButton.doOnClick { vm.onFirstCurrencyButtonClicked() }

        vm::secondCurrencyButtonText bind secondCurrencyButton::setText
        vm::secondCurrencyButtonState bind secondCurrencyButton::setState
        secondCurrencyButton.doOnClick { vm.onSecondCurrencyButtonClicked() }

        vm::firstCurrencyListVisible bind { firstCurrencyList.isVisible = it }
        vm::firstCurrencyContent bind { content ->
            if (content is CurrencyContent.Data) {
                firstCurrencySection.update(content.list.toGroupieItems {
                    vm.onFirstCurrencyItemClicked(it)
                })
            }
        }

        fullScreenBtn.doOnClick { onChartHeightChange(vm.getNewChartMode()) }

        createAlertBtn.doOnClick { vm.onNewAlertClicked() }

        vm::isProgressBarVisible bind { progressBar.isVisible = it }

        vm::secondCurrencyListVisible bind { secondCurrencyList.isVisible = it }
        vm::secondCurrencyContent bind { content ->
            if (content is CurrencyContent.Data) {
                secondCurrencySection.update(content.list.toGroupieItems {
                    vm.onSecondCurrencyItemClicked(it)
                })
            }
        }

        vm::advertsData bind {
            banner.bindView(it.dataOrNull) { period, purchased ->
                activity.onPurchase(period, purchased)
            }
        }

        vm.onCoinClicked bind {
            view?.hideKeyboard()
            bottomView.searchEditText.setText("")
        }

        vm::isGraphElementsVisible bind {
            bottomView.accentButton.isVisible = it
            bottomView.searchInput.isVisible = !it
            graphGroup.isVisible = it
            graphTouchArea.isVisible = it
        }

        vm::timeStepState bind { time ->
            timeToggleSection.update(TimeStep.values().toGroupieItems(time) {
                vm.onTimeToggleButtonClicked(it)
            })
        }
    }
}