package ru.mobileup.coinroad.ui.connection.account

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemEmptyAccountConnectionBinding
import kotlin.random.Random

class EmptyAccountConnectionItem : BindableItem<ItemEmptyAccountConnectionBinding>() {

    override fun getId() = Random.nextLong()

    override fun getLayout() = R.layout.item_empty_account_connection

    override fun initializeViewBinding(view: View) = ItemEmptyAccountConnectionBinding.bind(view)

    override fun bind(binding: ItemEmptyAccountConnectionBinding, position: Int) = Unit
}