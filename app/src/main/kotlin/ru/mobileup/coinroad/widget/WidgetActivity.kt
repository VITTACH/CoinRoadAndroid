package ru.mobileup.coinroad.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.aartikov.sesame.navigation.NavigationMessageDispatcher
import me.aartikov.sesame.property.PropertyObserver
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ActivityWidgetBinding
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.ui.BaseActivity
import ru.mobileup.coinroad.ui.base.BindingDelegate
import ru.mobileup.coinroad.ui.dashboard.content.GraphsContent
import ru.mobileup.coinroad.ui.dashboard.toGroupieItems
import ru.mobileup.coinroad.util.system.dataOrNull
import ru.mobileup.coinroad.util.ui.doOnClick
import ru.mobileup.coinroad.util.ui.getColorFromAttr
import ru.mobileup.coinroad.util.ui.textChanges

class WidgetActivity : BaseActivity(), PropertyObserver {

    override val propertyObserverLifecycleOwner: LifecycleOwner get() = this

    private val binding by viewBinding(ActivityWidgetBinding::bind, R.id.root)

    private val navigationMessageDispatcher: NavigationMessageDispatcher = getKoin().get()
    private val vm: WidgetActivityViewModel by viewModel()
    private val graphAdapter = GroupAdapter<GroupieViewHolder>()

    private val graphs = mutableListOf<Graph>()

    private var widgetId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget)

        widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0)

        BindingDelegate(this, binding.snackbarLayout).bind(vm)

        initRecyclerView(this)
        initViews(this)
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigationMessageDispatcher.resume()
    }

    override fun onPause() {
        navigationMessageDispatcher.pause()
        super.onPause()
    }

    override fun onBackPressed() = vm.navigateBack()

    private fun initViews(context: Context) = with(binding) {

        with(bottomView) {
            root.setBackgroundColor(context.getColorFromAttr(R.attr.navbarBackgroundColor))
            primaryButton.isVisible = false
            searchInput.isVisible = true

            searchEditText.hint = getString(R.string.search_charts_hint)
            searchEditText.textChanges()
                .onEach { vm.onSearchInput(it.toString()) }
                .launchIn(lifecycleScope)
        }

        additionalInfo.closeInfoBtn.doOnClick {
            vm.onCloseInfoClick()
        }

        vm.onWidgetHelpShown bind {
            additionalInfo.root.isVisible = it
        }

        vm.onCloseApp bind {
            setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
            vm.onClose(context)
        }

        vm::filteredGraphState bind { state ->
            if (state is GraphsContent.Data) {
                graphs.clear()
                graphs.addAll(state.graphs.sortedBy { it.id })
                graphAdapter.replaceAll(
                    graphs.toGroupieItems(
                        onGraphClicked = { vm.onGraphClicked(it, widgetId) },
                        isPrefsVisible = false
                    )
                )
            } else if (state !is GraphsContent.Loading) {
                graphAdapter.replaceAll(emptyList())
            }

            bottomView.searchInput.isVisible = graphs.isNotEmpty()
        }
    }

    private fun initRecyclerView(context: Context) {
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = graphAdapter
        }
    }
}