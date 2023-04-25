package ru.mobileup.coinroad.ui.connection.account

import android.view.View
import androidx.core.view.isVisible
import com.xwray.groupie.viewbinding.BindableItem
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemAccountConnectionBinding
import ru.mobileup.coinroad.domain.common.Connection
import ru.mobileup.coinroad.util.formatDiffTime
import ru.mobileup.coinroad.util.iconResource
import ru.mobileup.coinroad.util.ui.doOnClick

class AccountConnectionItem(
    private val account: Connection.Account,
    private val onAccountClicked: (account: Connection.Account) -> Unit
) : BindableItem<ItemAccountConnectionBinding>() {

    override fun getLayout() = R.layout.item_account_connection

    override fun initializeViewBinding(view: View) = ItemAccountConnectionBinding.bind(view)

    override fun bind(binding: ItemAccountConnectionBinding, position: Int) = with(binding) {
        exchangeIcon.setImageResource(account.exchange.iconResource)
        exchangeName.text = account.exchange.name

        syncTime.isVisible = account.lastSyncTime != null
        syncTime.text = account.lastSyncTime?.let {
            root.context.getString(R.string.connection_sync_time, root.context.formatDiffTime(it))
        }

        root.doOnClick { onAccountClicked.invoke(account) }
    }
}
