package ru.mobileup.coinroad.ui.alerts

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenAlertNewBinding
import ru.mobileup.coinroad.domain.common.Alert
import ru.mobileup.coinroad.ui.alerts.content.AlertsContent
import ru.mobileup.coinroad.ui.alerts.content.GraphDataItemContent
import ru.mobileup.coinroad.ui.alerts.item.PricePercent
import ru.mobileup.coinroad.ui.alerts.item.toGroupieItems
import ru.mobileup.coinroad.ui.base.BaseScreen
import ru.mobileup.coinroad.ui.base.widget.HeaderItem
import ru.mobileup.coinroad.util.formatPrice
import ru.mobileup.coinroad.util.getPricePattern
import ru.mobileup.coinroad.util.helper.SwipeHelper
import ru.mobileup.coinroad.util.iconResource
import ru.mobileup.coinroad.util.system.serializableArg
import ru.mobileup.coinroad.util.ui.applyTextWatcher
import ru.mobileup.coinroad.util.ui.doOnClick
import ru.mobileup.coinroad.util.ui.getColorFromAttr
import ru.mobileup.coinroad.util.ui.textChanges

class NewAlertScreen : BaseScreen<NewAlertViewModel>(R.layout.screen_alert_new) {

    var data by serializableArg<NewAlertData>()

    private val handler = Handler(Looper.getMainLooper())

    private val binding by viewBinding(ScreenAlertNewBinding::bind)
    override val vm: NewAlertViewModel by viewModel { parametersOf(data) }

    private val alertsAdapter = GroupAdapter<GroupieViewHolder>()
    private val alertsSection = Section()

    private val pricePercentAdapter = GroupAdapter<GroupieViewHolder>()
    private val pricePercentSection = Section()

    private val alerts = mutableListOf<Alert>()

    private val swipeHelper: SwipeHelper by inject()

    private var isNeedScrollToEnd = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        initViews(context)
    }

    private fun initViews(context: Context) = with(binding) {
        toolbar.setNavigationOnClickListener { vm.navigateBack() }

        priceEditText.textChanges()
            .onEach { text ->
                val isEmpty = text.isNullOrBlank()
                bottomView.accentButton.isVisible = !isEmpty
                val price = if (!isEmpty && text.toString() != ".") {
                    text.toString().toBigDecimal()
                } else vm.initPrice
                vm.onPriceChange(price)
            }
            .launchIn(lifecycleScope)

        swipeHelper.onSetListener(context, true, object : SwipeHelper.SwipeListener {
            override fun onSwipeItem(position: Int) {
                handler.postDelayed({ swipeRefresh.isEnabled = true }, 1000)
                vm.onAlertDelete(alerts[position - 1].id)
            }

            override fun onMove() {
                if (swipeRefresh.isEnabled) swipeRefresh.isEnabled = false
            }

            override fun onMoved() {
                swipeRefresh.isEnabled = true
            }
        })

        with(percentList) {
            pricePercentAdapter.clear()
            pricePercentAdapter.add(pricePercentSection)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = pricePercentAdapter
        }

        with(exchangeInput) {
            startIconDrawable = ContextCompat.getDrawable(context, data.exchange.iconResource)
            exchangeEditText.setText(data.currencyPair.toString())
            exchangeEditText.doOnClick { vm.onCurrencyPairClicked() }
            hint = data.exchange.name.uppercase()
        }

        with(swipeRefresh) {
            setOnRefreshListener { vm.onRefresh() }
            setColorSchemeColors(context.getColorFromAttr(R.attr.colorAccent))
        }

        with(alertsList) {
            alertsSection.setHeader(HeaderItem(R.string.alert_recent_alerts))
            alertsAdapter.clear()
            alertsAdapter.add(alertsSection)
            layoutManager = LinearLayoutManager(context)
            adapter = alertsAdapter
            /*addItemDecoration(
                DividerWithMarginItemDecoration(
                    context = context,
                    leftMargin = resources.getDimension(R.dimen.normal_gap).toInt(),
                    afterFirst = false
                )
            )*/
            ItemTouchHelper(swipeHelper.swipeHandler).attachToRecyclerView(this)
        }

        with(bottomView) {
            accentButton.setText(R.string.alert_create_alert)
            accentButton.doOnClick {
                vm.onNewAlertClicked()
                isNeedScrollToEnd = true
            }

            actionButton.setCardBackgroundColor(context.getColor(R.color.green_a))
            actionButton.doOnClick {
                searchInput.isVisible = !searchInput.isVisible && alerts.isNotEmpty()
            }
            actionButton.isVisible = true
            primaryButton.isVisible = false

            searchEditText.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            searchEditText.hint = getString(R.string.search_price_hint)
            searchEditText.textChanges()
                .filterNot { it.toString() == "." }
                .onEach { vm.onSearchInput(it.toString()) }
                .launchIn(lifecycleScope)

        }

        vm.onPriceChanged bind { price ->
            when (val item = vm.previewGraphContent) {
                is GraphDataItemContent.Data -> {
                    priceEditText.setText(item.dataItem.formatPrice(price!!, false))
                }
            }
        }

        vm::pricePercentChange bind { curPercent ->
            pricePercentChange.text = getString(R.string.alert_price_percent_change, curPercent)
        }

        vm::previewGraphContent bind { content ->
            if (content is GraphDataItemContent.Data) {
                val formattedPrice = content.dataItem.formatPrice(vm.initPrice, false)
                val percents = PricePercent.values().map { it.value }.toMutableList().apply {
                    add(this.size / 2, formattedPrice)
                }

                pricePercentSection.update(percents.toGroupieItems { vm.onPercentClicked(it) })
                bottomView.accentButton.isVisible = true
                with(priceEditText) {
                    applyTextWatcher(getPricePattern(content.dataItem.ticker.precision)) { it }
                }
            }

            swipeRefresh.isRefreshing = content is GraphDataItemContent.Empty
        }

        vm::filteredAlertState bind { state ->
            if (state is AlertsContent.Data) {
                alerts.clear()
                alerts.addAll(state.alerts.sortedBy { it.id })
                alertsSection.update(alerts.toGroupieItems { vm.onAlertUpdated(it) })
                if (isNeedScrollToEnd) {
                    val layoutManager = (alertsList.layoutManager as LinearLayoutManager)
                    layoutManager.scrollToPositionWithOffset(alerts.size - 1, 1)
                    isNeedScrollToEnd = false
                }
            } else if (state is AlertsContent.Empty) {
                if (!state.hasFilter) alerts.clear()
                alertsSection.update(emptyList())
            }

            bottomView.actionButton.isVisible = alerts.isNotEmpty()
        }
    }
}