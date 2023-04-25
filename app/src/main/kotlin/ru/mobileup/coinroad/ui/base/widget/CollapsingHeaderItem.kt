package ru.mobileup.coinroad.ui.base.widget

import android.view.View
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.viewbinding.BindableItem
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemListCollapsingHeaderBinding
import ru.mobileup.coinroad.util.ui.doOnClick

class CollapsingHeaderItem(
    private val titleText: String,
    private val titleCounter: Int? = null
) : BindableItem<ItemListCollapsingHeaderBinding>(), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun getLayout() = R.layout.item_list_collapsing_header

    override fun initializeViewBinding(view: View) = ItemListCollapsingHeaderBinding.bind(view)

    override fun bind(viewBinding: ItemListCollapsingHeaderBinding, position: Int) =
        with(viewBinding) {
            title.text = titleText
            counter.text = titleCounter?.toString() ?: "0"
            root.doOnClick {
                expandableGroup.onToggleExpanded()
                onCollapse(viewBinding)
            }
        }

    private fun onCollapse(binding: ItemListCollapsingHeaderBinding) {
        val icon = if (expandableGroup.isExpanded) {
            R.drawable.ic_24_arrow_drop_up
        } else R.drawable.ic_24_arrow_drop_down

        binding.arrowIcon.setImageResource(icon)
    }

    override fun setExpandableGroup(expandableGroup: ExpandableGroup) {
        this.expandableGroup = expandableGroup
    }
}