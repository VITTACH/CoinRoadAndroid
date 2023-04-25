package ru.mobileup.coinroad.ui.exchange

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import me.aartikov.sesame.loading.simple.Loading
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenExchangesDialogBinding
import ru.mobileup.coinroad.domain.common.Exchange
import ru.mobileup.coinroad.ui.base.BaseScreenBottomSheetDialog
import ru.mobileup.coinroad.ui.base.widget.HeaderItem

class SelectExchangeDialog(
    val onRegistrationClicked: (Exchange) -> Unit,
    val onExchangeClicked: (Exchange) -> Unit
) : BaseScreenBottomSheetDialog<SelectExchangeViewModel>() {

    override val screenLayout: Int = R.layout.screen_exchanges_dialog

    override val vm by viewModel<SelectExchangeViewModel>()
    private val binding: ScreenExchangesDialogBinding by viewBinding()

    private val section = Section().apply {
        setHeader(HeaderItem(R.string.choosing_select_exchange))
    }
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
        .apply { add(section) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }

        vm::exchangesState bind { sate ->
            if (sate is Loading.State.Data) {
                section.update(sate.data.toGroupieItems(
                    { this.dismiss(); onRegistrationClicked(it) },
                    { this.dismiss(); onExchangeClicked(it) }
                ))
            }
        }
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        super.onDestroyView()
    }
}