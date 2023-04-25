package ru.mobileup.coinroad.ui.connection

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenConnectionsDialogBinding
import ru.mobileup.coinroad.domain.common.Connection
import ru.mobileup.coinroad.domain.common.Connection.Account
import ru.mobileup.coinroad.ui.base.BaseScreenBottomSheetDialog
import ru.mobileup.coinroad.ui.base.widget.CollapsingHeaderItem
import ru.mobileup.coinroad.ui.connection.account.AccountConnectionItem
import ru.mobileup.coinroad.ui.connection.account.EmptyAccountConnectionItem
import ru.mobileup.coinroad.util.ui.doOnClick

class SelectConnectionDialog(
    val onNewConnectionClicked: (Connection) -> Unit,
    val onConnectionClicked: (Connection) -> Unit
) : BaseScreenBottomSheetDialog<SelectConnectionViewModel>() {

    override val screenLayout: Int = R.layout.screen_connections_dialog

    override val vm by viewModel<SelectConnectionViewModel>()
    private val binding: ScreenConnectionsDialogBinding by viewBinding()

    private val connectionsGroupAdapter = GroupieAdapter()

    private var walletsExpandableGroup: ExpandableGroup? = null
    private var accountsExpandableGroup: ExpandableGroup? = null

    private var walletsSection = Section()
    private var accountsSection = Section()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = connectionsGroupAdapter

        syncBtn.doOnClick { vm.onSyncConnectionsClicked() }

        vm.onNewConnectionCommand bind {
            dismiss()
            onNewConnectionClicked(it)
        }

        vm.onConnectionCommand bind {
            dismiss()
            onConnectionClicked(it)
        }

        vm::walletDetailsItems bind { state ->
            setupWalletsExpandableGroup()
            updateExpandableGroups()
            walletsSection.replaceAll(emptyList())
        }

        vm::accountDetailsItems bind { state ->
            if (state == null) return@bind
            val groups = state.map {
                when (it) {
                    is EmptyConnectionItemData -> EmptyAccountConnectionItem()
                    else -> AccountConnectionItem(it.connection as Account, it.onClickListener)
                }
            }
            setupAccountsExpandableGroup(state.filterIsInstance<ConnectionItemData>().size)
            updateExpandableGroups()
            accountsSection.replaceAll(groups)
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }

    private fun setupAccountsExpandableGroup(counter: Int? = null) {
        val header = CollapsingHeaderItem(
            titleText = getString(R.string.portfolio_accounts_header),
            titleCounter = counter
        )
        accountsExpandableGroup = ExpandableGroup(header, true).apply { add(accountsSection) }
    }

    private fun setupWalletsExpandableGroup(counter: Int? = null) {
        val header = CollapsingHeaderItem(
            titleText = getString(R.string.portfolio_wallets_header),
            titleCounter = counter
        )
        walletsExpandableGroup = ExpandableGroup(header, true).apply { add(walletsSection) }
    }

    private fun updateExpandableGroups() {
        val groups = mutableListOf<ExpandableGroup>()
        accountsExpandableGroup?.let { groups.add(it) }
        walletsExpandableGroup?.let { groups.add(it) }
        connectionsGroupAdapter.replaceAll(groups)
    }
}