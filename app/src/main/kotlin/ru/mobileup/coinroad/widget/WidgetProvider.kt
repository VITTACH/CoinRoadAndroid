package ru.mobileup.coinroad.widget

import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.RemoteViews
import com.github.ajalt.timberkt.d
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.w
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.data.storage.graph.GraphStorage
import ru.mobileup.coinroad.data.storage.graph.chart.ChartBitmapStorage
import ru.mobileup.coinroad.data.storage.widget.WidgetStorage
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.ui.graph.GraphDataItem
import ru.mobileup.coinroad.ui.graph.toGraphItem
import ru.mobileup.coinroad.ui.graph.toPreviewGraphItem
import ru.mobileup.coinroad.util.formatPrice
import ru.mobileup.coinroad.util.getTickerPrice
import ru.mobileup.coinroad.util.iconResource
import ru.mobileup.coinroad.util.system.zip
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration

abstract class WidgetProvider : AppWidgetProvider() {

    private data class WidgetModel(val graph: Graph?)

    enum class WidgetType { Large, Small }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        CoroutineScope(Dispatchers.Default).launch {
            val ids = widgetStorage.observeWidgets().first().map { it.id }
            (ids - appWidgetIds.toList()).forEach { widgetStorage.deleteWidget(it) }
        }
    }

    override fun onUpdate(context: Context?, appManager: AppWidgetManager?, widgetIds: IntArray?) {
        super.onUpdate(context, appManager, widgetIds)

        w { "WidgetProvider onUpdate, widgetIds = ${widgetIds?.size}" }
        startUpdateJob(context, true)
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
        context?.getSystemService(JobScheduler::class.java)?.cancelAll()
        w { "WidgetProvider onDisabled" }
    }

    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        w { "WidgetProvider onEnabled" }
    }

    companion object {
        private val UPDATE_PERIOD_SEC = Duration.seconds(1)

        private val widgetStorage: WidgetStorage = getKoin().get()
        private val chartBitmapStorage: ChartBitmapStorage = getKoin().get()
        private val graphStorage: GraphStorage = getKoin().get()

        private var updateJob: Job? = null

        fun startUpdateJob(context: Context?, force: Boolean) {
            if (context == null) return
            if (updateJob == null) {
                updateJob = Job()
            } else {
                updateJob!!.cancel()
            }
            with(CoroutineScope(updateJob!! + Dispatchers.Default)) {
                launch {
                    zip<Boolean, List<Boolean>>(
                        updWidgets(context, WidgetType.Large), updWidgets(context, WidgetType.Small)
                    ).collect { }
                }
                launch {
                    scheduleUpdJob(context, force)
                }
            }
        }

        suspend fun scheduleUpdJob(context: Context, force: Boolean = false) {
            val intervalSec = widgetStorage.readPeriod().first().let {
                when {
                    force -> 1
                    it == 0 -> UPDATE_PERIOD_SEC.inWholeSeconds.toInt()
                    else -> it
                }
            }

            context.getSystemService(JobScheduler::class.java).cancelAll()

            val builder = JobInfo.Builder(0, ComponentName(context, WidgetJobService::class.java))
            builder.setPersisted(true)
            builder.setMinimumLatency(intervalSec * 1000L)
            builder.setOverrideDeadline(intervalSec * 1000L)

            val job = context.getSystemService(JobScheduler::class.java)?.schedule(builder.build())
            if (job == JobScheduler.RESULT_SUCCESS) {
                d { "WidgetProvider Job scheduled successfully" }
            }
        }

        class WidgetJobService : JobService(), CoroutineScope {

            private lateinit var job: Job
            override val coroutineContext: CoroutineContext
                get() = job + Dispatchers.Default

            override fun onCreate() {
                super.onCreate()
                job = Job()
            }

            override fun onStartJob(params: JobParameters): Boolean {
                w { "WidgetJobService Started" }
                launch {
                    val context = applicationContext
                    zip<Boolean, List<Boolean>>(
                        updWidgets(context, WidgetType.Large), updWidgets(context, WidgetType.Small)
                    ).collect { }

                    scheduleUpdJob(context)
                }
                return true
            }

            override fun onStopJob(params: JobParameters): Boolean {
                w { "WidgetJobService Stopped" }
                job.cancel()
                return false
            }

            override fun onDestroy() {
                super.onDestroy()
                job.cancel()
            }
        }

        private fun updWidgets(context: Context, type: WidgetType, ids: IntArray? = null) = flow {
            var widgetIds = ids
            val myManager = AppWidgetManager.getInstance(context)

            if (widgetIds == null) {
                try {
                    val component = when (type) {
                        WidgetType.Large -> ComponentName(context, WidgetProviderLarge::class.java)
                        WidgetType.Small -> ComponentName(context, WidgetProviderSmall::class.java)
                    }
                    widgetIds = myManager.getAppWidgetIds(component)
                } catch (e: RuntimeException) {
                    e { "Runtime Exception when request getAppWidgetId: $e" }
                    emit(false)
                }
            }

            if (widgetIds?.isEmpty() == false) {
                val flows = mutableListOf<Flow<RemoteViews>>()
                for (widgetId in widgetIds) {
                    if (widgetId > AppWidgetManager.INVALID_APPWIDGET_ID) {
                        flows.add(
                            buildUpdate(context, widgetId, type).onEach { view ->
                                myManager.updateAppWidget(widgetId, view)
                            }
                        )
                    }
                }

                zip(*flows.toTypedArray()) { }.collect { emit(true) }
            } else {
                e { "Can't update widget, updateWidgetIds is null" }
                emit(false)
            }
        }

        private fun buildUpdate(context: Context?, id: Int, type: WidgetType): Flow<RemoteViews> =
            flow {
                val widget = widgetStorage.observeWidgets().first().find { it.id == id }
                if (widget == null) {
                    emit(getView(context, id, type))
                    return@flow
                }

                val graph = graphStorage.observeGraphs().first().find {
                    it.exchange == widget.exchange && it.currencyPair == widget.currencyPair
                }

                emit(getView(context, id, type, WidgetModel(graph)))
            }

        private suspend fun getView(
            context: Context?,
            widgetId: Int,
            type: WidgetType,
            model: WidgetModel? = null
        ) = RemoteViews(
            context?.packageName,
            when (type) {
                WidgetType.Large -> R.layout.widget_large_layout
                WidgetType.Small -> R.layout.widget_small_layout
            }
        ).apply {
            val intent = Intent(context, WidgetActivity::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }

            setOnClickPendingIntent(
                R.id.layout,
                getActivity(context, widgetId, intent, PendingIntent.FLAG_IMMUTABLE)
            )

            model?.let {
                it.graph?.let { graph ->
                    when (type) {
                        WidgetType.Large -> {
                            val id = graph.id.hashCode()
                            chartBitmapStorage.observeBitmap(id).first().let {
                                BitmapFactory.decodeByteArray(it, 0, it.size)
                            }.also { bitmap ->
                                WidgetProviderLarge.bindView(bitmap, this)
                            }
                        }
                        WidgetType.Small -> {
                            val graphDataItem = if (graph.graphData != null) {
                                graph.toGraphItem()
                            } else graph.toPreviewGraphItem()
                            WidgetProviderSmall.bindView(graphDataItem, this)
                        }
                    }

                    setViewVisibility(R.id.errorView, View.GONE)
                    setViewVisibility(R.id.widget_progress, View.GONE)
                    return@apply
                } ?: setTextViewText(
                    R.id.errorView, context?.getString(R.string.widget_failed_connect)
                )
            } ?: setTextViewText(
                R.id.errorView, context?.getString(R.string.widget_default_info)
            )

            setViewVisibility(R.id.errorView, View.VISIBLE)
            setViewVisibility(R.id.widget_progress, View.VISIBLE)
        }
    }
}

class WidgetProviderSmall : WidgetProvider() {
    companion object {
        private val timeFormatter = SimpleDateFormat("HH:mm:ss")

        fun bindView(dataItem: GraphDataItem, view: RemoteViews) = view.apply {
            setImageViewResource(R.id.exchange_image, dataItem.ticker.exchange.iconResource)
            setTextViewText(R.id.exchange, dataItem.ticker.exchange.toString())
            setTextViewText(R.id.pair, dataItem.ticker.currencyPair.toString())
            setTextViewText(R.id.time, timeFormatter.format(Date()))

            val series = dataItem.candlestickSeries
            setTextViewText(
                R.id.price,
                dataItem.formatPrice(series.getTickerPrice().toBigDecimal())
            )
            setTextViewText(
                R.id.open,
                dataItem.formatPrice(BigDecimal(series.openSeries.getyVals().last as Double))
            )
            setTextViewText(
                R.id.high,
                dataItem.formatPrice(BigDecimal(series.highSeries.getyVals().last as Double))
            )
            setTextViewText(
                R.id.low,
                dataItem.formatPrice(BigDecimal(series.lowSeries.getyVals().last as Double))
            )
        }
    }
}

class WidgetProviderLarge : WidgetProvider() {
    companion object {
        fun bindView(bitmap: Bitmap, view: RemoteViews) = view.apply {
            setImageViewBitmap(R.id.chartView, bitmap)
        }
    }
}
