package ru.mobileup.coinroad.ui.portfolio

import android.os.Bundle
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.Section
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenPortfolioBinding
import ru.mobileup.coinroad.domain.common.Connection
import ru.mobileup.coinroad.ui.base.BaseScreen
import ru.mobileup.coinroad.ui.base.widget.CollapsingHeaderItem
import ru.mobileup.coinroad.ui.connection.ConnectionItemData
import ru.mobileup.coinroad.ui.connection.EmptyConnectionItemData
import ru.mobileup.coinroad.ui.portfolio.account.AccountItem
import ru.mobileup.coinroad.ui.portfolio.account.EmptyAccountItem
import ru.mobileup.coinroad.util.ui.doOnClick

class PortfolioScreen : BaseScreen<PortfolioViewModel>(R.layout.screen_portfolio) {

    private val binding by viewBinding(ScreenPortfolioBinding::bind)
    override val vm: PortfolioViewModel by viewModel()

    private val portfolioGroupAdapter = GroupieAdapter()

    private var walletsExpandableGroup: ExpandableGroup? = null
    private var accountsExpandableGroup: ExpandableGroup? = null

    private var walletsSection = Section()
    private var accountsSection = Section()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() = with(binding) {
        recyclerView.adapter = portfolioGroupAdapter

        with(bottomView) {
            primaryButton.doOnClick { vm.onNewConnectionClicked() }
            primaryButton.setText(R.string.portfolio_new_connection)
            primaryButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_24_plus, 0)
        }

        vm::walletDetailsItems bind { state ->
            setupWalletsExpandableGroup()
            updateExpandableGroups()
            walletsSection.replaceAll(emptyList())
        }

        vm::accountDetailsItems bind { state ->
            val groups = state.map {
                when (it) {
                    is EmptyConnectionItemData -> EmptyAccountItem()
                    else -> AccountItem(it.connection as Connection.Account, it.onClickListener)
                }
            }
            setupAccountsExpandableGroup(state.filterIsInstance<ConnectionItemData>().size)
            updateExpandableGroups()
            accountsSection.replaceAll(groups)
        }
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
        portfolioGroupAdapter.replaceAll(groups)
    }
}