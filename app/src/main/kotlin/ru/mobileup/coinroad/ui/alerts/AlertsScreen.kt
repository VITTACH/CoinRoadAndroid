package ru.mobileup.coinroad.ui.alerts

import android.content.Context
import android.os.Bundle
import android.view.View
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
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenAlertsBinding
import ru.mobileup.coinroad.domain.common.Alert
import ru.mobileup.coinroad.ui.alerts.content.AlertsContent
import ru.mobileup.coinroad.ui.alerts.item.toGroupieItems
import ru.mobileup.coinroad.ui.base.BaseScreen
import ru.mobileup.coinroad.util.helper.SwipeHelper
import ru.mobileup.coinroad.util.ui.doOnClick
import ru.mobileup.coinroad.util.ui.textChanges

class AlertsScreen : BaseScreen<AlertsViewModel>(R.layout.screen_alerts) {

    private val binding by viewBinding(ScreenAlertsBinding::bind)
    override val vm: AlertsViewModel by viewModel()

    private val alertsAdapter = GroupAdapter<GroupieViewHolder>()
    private val alertsSection = Section()

    private val alerts = mutableListOf<Alert>()

    private val swipeHelper: SwipeHelper by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(requireContext())
    }

    private fun initViews(context: Context) = with(binding) {
        toolbar.setNavigationOnClickListener { vm.navigateBack() }

        swipeHelper.onSetListener(context, false, object : SwipeHelper.SwipeListener {
            override fun onSwipeItem(position: Int) {
                vm.onAlertDelete(alerts[position].id)
            }
        })

        with(alertsList) {
            alertsAdapter.clear()
            alertsAdapter.add(alertsSection)
            layoutManager = LinearLayoutManager(context)
            adapter = alertsAdapter
            ItemTouchHelper(swipeHelper.swipeHandler).attachToRecyclerView(this)
        }

        with(bottomView) {
            primaryButton.doOnClick { vm.onNewAlertClicked() }
            primaryButton.setText(R.string.alerts_create)
            primaryButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_24_plus, 0)

            searchEditText.textChanges()
                .filterNot { it.toString() == "." }
                .onEach { vm.onSearchInput(it.toString()) }
                .launchIn(lifecycleScope)
        }

        vm::filteredAlertState bind { state ->
            if (state is AlertsContent.Data) {
                alerts.clear()
                alerts.addAll(state.alerts.sortedBy { it.id })
                alertsSection.update(alerts.toGroupieItems { vm.onAlertUpdated(it) })
            } else if (state is AlertsContent.Empty) {
                if (!state.hasFilter) alerts.clear()
                alertsSection.update(emptyList())
            }

            bottomView.searchInput.isVisible = alerts.isNotEmpty()
        }
    }
}