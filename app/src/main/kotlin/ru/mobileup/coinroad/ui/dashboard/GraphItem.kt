package ru.mobileup.coinroad.ui.dashboard

import android.annotation.SuppressLint
import android.view.View
import androidx.core.view.isVisible
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.showAlignBottom
import com.xwray.groupie.viewbinding.BindableItem
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ItemGraphBinding
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.ui.graph.CustomChartView.Companion.PREVIEW_ID
import ru.mobileup.coinroad.ui.graph.toGraphItem
import ru.mobileup.coinroad.ui.graph.toPreviewGraphItem
import ru.mobileup.coinroad.util.ui.doOnClick

class GraphItem(
    private val graph: Graph,
    private val onGraphClicked: (graph: Graph) -> Unit,
    private val onVisibleChanged: (graph: Graph) -> Unit,
    private val onDeepLinksClicked: (graph: Graph) -> Unit,
    private val pushVisibleShowCase: Balloon?,
    private val isPrefsVisible: Boolean
) : BindableItem<ItemGraphBinding>() {

    override fun initializeViewBinding(view: View) = ItemGraphBinding.bind(view)

    override fun getId(): Long = graph.id.hashCode().toLong()

    override fun getLayout() = R.layout.item_graph

    @SuppressLint("ClickableViewAccessibility")
    override fun bind(viewBinding: ItemGraphBinding, position: Int) {
        val dataItem =
            if (graph.graphData != null) graph.toGraphItem() else graph.toPreviewGraphItem()

        with(viewBinding) {
            val context = prefs.appName.context

            graphPlot.updatePlot(curChartId = PREVIEW_ID, dataItem = dataItem)
            graphTouchArea.doOnClick { onGraphClicked.invoke(graph) }

            prefs.root.isVisible = isPrefsVisible
            // On tap application
            prefs.deepLinkButton.doOnClick { onDeepLinksClicked.invoke(graph) }
            prefs.appName.text = graph.deepLink?.id ?: context.getString(R.string.add_app)

            // Push visibility
            with(prefs.pushVisible) {
                setOnCheckedChangeListener(null)
                isChecked = graph.isVisible
                setOnCheckedChangeListener { _, isChecked ->
                    onVisibleChanged.invoke(graph.copy(isVisible = isChecked))
                }
                prefs.visibilityButton.doOnClick { isChecked = !isChecked }

                pushVisibleShowCase?.let {
                    showAlignBottom(
                        balloon = it,
                        xOff = 0,
                        yOff = -context.resources.getDimension(R.dimen.medium_gap).toInt()
                    )
                }
            }

            shadowView.isVisible = !graph.isVisible
        }
    }
}

fun List<Graph>.toGroupieItems(
    pushVisibleShowCase: Balloon? = null,
    onGraphClicked: (graph: Graph) -> Unit = {},
    onVisibleChanged: (graph: Graph) -> Unit = {},
    onDeepLinksClicked: (graph: Graph) -> Unit = {},
    isPrefsVisible: Boolean = true
): List<GraphItem> = mapIndexed { index, graph ->
    GraphItem(
        graph = graph,
        onGraphClicked = onGraphClicked,
        onVisibleChanged = onVisibleChanged,
        onDeepLinksClicked = onDeepLinksClicked,
        pushVisibleShowCase = if (index == 0) pushVisibleShowCase else null,
        isPrefsVisible = isPrefsVisible
    )
}