package ru.mobileup.coinroad.ui.base.widget

import android.view.View
import androidx.annotation.StringRes
import com.xwray.groupie.viewbinding.BindableItem
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemListHeaderBinding


class HeaderItem(
    @StringRes private val titleId: Int = 0
) : BindableItem<ItemListHeaderBinding>() {

    override fun getLayout() = R.layout.item_list_header

    override fun initializeViewBinding(view: View) = ItemListHeaderBinding.bind(view)

    override fun bind(viewBinding: ItemListHeaderBinding, position: Int) = with(viewBinding) {
        title.setText(titleId)
    }
}