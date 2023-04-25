package ru.mobileup.coinroad.ui.portfolio.account

import android.view.View
import com.xwray.groupie.viewbinding.BindableItem
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemEmptyAccountBinding
import kotlin.random.Random

class EmptyAccountItem : BindableItem<ItemEmptyAccountBinding>() {

    override fun getId() = Random.nextLong()

    override fun getLayout() = R.layout.item_empty_account

    override fun initializeViewBinding(view: View) = ItemEmptyAccountBinding.bind(view)

    override fun bind(binding: ItemEmptyAccountBinding, position: Int) = Unit
}