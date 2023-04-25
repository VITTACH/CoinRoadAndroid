package ru.mobileup.coinroad.system

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.Bitmap
import android.graphics.BitmapFactory.decodeByteArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.github.ajalt.timberkt.e
import com.github.ajalt.timberkt.w
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.analytics.AnalyticsManager
import ru.mobileup.coinroad.analytics.system.ReportWorkManagerStatus
import ru.mobileup.coinroad.data.storage.graph.chart.ChartBitmapStorage
import ru.mobileup.coinroad.domain.common.Alert
import ru.mobileup.coinroad.domain.common.Graph
import ru.mobileup.coinroad.domain.usecase.alert.EditAlertsInteractor
import ru.mobileup.coinroad.domain.usecase.alert.LoadAlertsInteractor
import ru.mobileup.coinroad.domain.usecase.graph.LoadGraphsInteractor
import ru.mobileup.coinroad.domain.usecase.settings.GetSettingsInteractor
import ru.mobileup.coinroad.ui.graph.ChartInitActivity.Companion.plot
import ru.mobileup.coinroad.ui.graph.GraphDataItem
import ru.mobileup.coinroad.ui.graph.toGraphItem
import ru.mobileup.coinroad.util.*
import ru.mobileup.coinroad.util.ui.getColorFromAttr
import ru.mobileup.coinroad.util.ui.toByteArray
import ru.mobileup.coinroad.util.ui.toNotificationSize
import java.lang.Integer.min
import kotlin.time.seconds


class NotificationWorkManager(
    private val context: Context,
    parameters: WorkerParameters,
    private val loadAlertsInteractor: LoadAlertsInteractor,
    private val editAlertsInteractor: EditAlertsInteractor,
    private val getSettingsInteractor: GetSettingsInteractor,
    private val loadGraphsInteractor: LoadGraphsInteractor,
    private val chartBitmapStorage: ChartBitmapStorage,
    private val analyticsManager: AnalyticsManager
) : CoroutineWorker(context, parameters) {

    companion object {
        const val ALERTS_CHANNEL_ID = "ALERTS_CHANNEL_ID"
        const val GRAPHS_CHANNEL_ID = "GRAPHS_CHANNEL_ID"
        const val GRAPHS_CHANNEL_OLD = "COIN_ROAD_CHANNEL_ID"

        private const val COMPRESS_IMAGE_VALUE = 590
        private const val CHANNEL_BUFFER_SIZE = 3600

        private const val ANALYTIC_PERIOD_MS = 3_600_000L

        private var NOTIFICATION_ID = 1
    }

    private val pushManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    private var oldAlertAnalyticTime = 0L

    private val completedAlertTimes = mutableSetOf<Long>()

    override suspend fun doWork(): Result {
        val signalChannel = Channel<Unit>(CHANNEL_BUFFER_SIZE)
        val signalFlow = flow { signalChannel.consumeEach { emit(it) } }
        var delaySec = 1

        return try {
            coroutineScope {
                launch {
                    while (true) {
                        val updatePeriodSec: Int
                        withContext(Dispatchers.IO) {
                            loadGraphsInteractor.load()
                            updatePeriodSec = getSettingsInteractor.load().updatePeriod
                        }
                        signalChannel.send(Unit)
                        delay(delaySec.seconds)
                        delaySec = min(delaySec + updatePeriodSec / 6, updatePeriodSec)
                    }
                }

                launch { subscribeOnUpdateSignal(signalFlow) }

                launch { subscribeOnAlertsInteractor(signalFlow) }

                subscribeOnGraphsInteractor(signalFlow)
            }

            Result.success()
        } catch (e: Exception) {
            e { "Retry exception: $e" }
            FirebaseCrashlytics.getInstance().recordException(e)
            Result.retry()
        }
    }

    private suspend fun subscribeOnGraphsInteractor(signalFlow: Flow<Unit>) {
        signalFlow.combine(loadGraphsInteractor.observe()) { _, graphList -> graphList }
            .collect { graphs ->
                val currentTime = System.currentTimeMillis()
                if (currentTime - oldAlertAnalyticTime >= ANALYTIC_PERIOD_MS) {
                    oldAlertAnalyticTime = currentTime
                    graphs.forEach {
                        analyticsManager.handleAnalyticMessage(ReportWorkManagerStatus(it))
                    }
                }
                graphs.updateNotifications()
            }
    }

    private suspend fun List<Graph>.updateNotifications() {
        filter { it.graphData != null && it.isVisible }.forEach {
            val id = it.id.hashCode()
            plot.updatePlot(id, it.toGraphItem())
            chartBitmapStorage.saveBitmap(id, plot.getBitmap().toByteArray())
            setForeground(createForegroundInfo(id, it.deepLink?.packageName))
        }
    }

    private suspend fun subscribeOnAlertsInteractor(signalFlow: Flow<Unit>) {
        signalFlow.combine(loadAlertsInteractor.observe()) { _, alertList -> alertList }
            .zip(loadGraphsInteractor.observe()) { alertList, graphList -> alertList to graphList }
            .collect { (alertList, graphList) ->
                graphList.filter { it.graphData != null }.forEach { graph ->
                    val dataItem = graph.toGraphItem()
                    val price = dataItem.candlestickSeries.getTickerPrice().toBigDecimal()
                    alertList.filter {
                        !completedAlertTimes.contains(it.time.toEpochMilliseconds())
                            && it.exchange == graph.exchange
                            && it.currencyPair == graph.currencyPair
                            && it.isActive
                    }.forEach {
                        val priceDiff = calculatePercentDifference(it.price, price).abs().toFloat()
                        val priceSensitive =
                            getSettingsInteractor.load().alertSensitive.toFloat()
                        if (priceDiff in 0f..priceSensitive) {
                            completedAlertTimes.add(it.time.toEpochMilliseconds())
                            w { "NotificationWorkManager: Create new alert = $it" }
                            createAlertNotification(it, dataItem)
                        }
                    }
                }
            }
    }

    private suspend fun createAlertNotification(alert: Alert, dataItem: GraphDataItem) {
        val content = RemoteViews(context.packageName, R.layout.alert_notification_small)

        val backView = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        Canvas(backView).drawPaint(Paint().apply { color = alert.color; style = Paint.Style.FILL })
        content.setImageViewBitmap(R.id.backgroundView, backView)

        content.setImageViewResource(R.id.exchangeView, alert.exchange.iconResource)

        content.setTextViewText(R.id.titleText, alert.currencyPair.toString())
        content.setTextViewText(R.id.exchangeText, alert.exchange.name.uppercase())
        content.setTextViewText(R.id.bodyText, dataItem.formatPrice(alert.price))
        content.setTextViewText(
            R.id.timeText,
            formatLocale(context, System.currentTimeMillis(), " ")
        )

        content.setTextColor(R.id.titleText, Color.WHITE)
        content.setTextColor(R.id.bodyText, Color.LTGRAY)
        content.setTextColor(R.id.exchangeText, Color.WHITE)
        content.setTextColor(R.id.timeText, Color.LTGRAY)

        val builder = NotificationCompat.Builder(context, getAlertsChanelId())
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(context.getColorFromAttr(R.attr.notificationBackgroundColor))
            .setOngoing(false)
            .setColorized(true)
            .setAutoCancel(false)
            .setCustomContentView(content)

        editAlertsInteractor.updateAlert(alert.copy(isActive = false, status = Alert.Status.FILLED))

        pushManager.notify(NOTIFICATION_ID++, builder.build())
    }

    private suspend fun subscribeOnUpdateSignal(signalFlow: Flow<Unit>) {
        signalFlow.combine(loadGraphsInteractor.observe()) { _, graphList -> graphList }
            .zip(chartBitmapStorage.observeIds()) { graphs, ids -> graphs to ids }
            .collect { (graphs, ids) -> graphs.cleanupBitmapStorage(ids) }
    }

    private suspend fun List<Graph>.cleanupBitmapStorage(ids: Set<Int>) {
        (ids - filter { it.graphData != null }.map { it.id.hashCode() })
            .forEach { chartBitmapStorage.removeById(it) }
    }

    private suspend fun createForegroundInfo(id: Int, `package`: String?) =
        ForegroundInfo(id, createNotification(id, `package`))

    private suspend fun createNotification(id: Int, `package`: String?): Notification {
        val content = RemoteViews(context.packageName, R.layout.item_notification_small)

        val bytes = chartBitmapStorage.observeBitmap(id).first()
        val bitmap = decodeByteArray(bytes, 0, bytes.size).toNotificationSize(COMPRESS_IMAGE_VALUE)
        val background = Bitmap.createBitmap(bitmap, 2, 0, 1, bitmap.height)
        w { "NotificationWorkManager: Bitmap size in KB = ${bitmap.byteCount / 1024}" }
        content.setImageViewBitmap(R.id.chartView, bitmap)
        content.setImageViewBitmap(R.id.backgroundView, background)

        val builder = NotificationCompat.Builder(context, getGraphsChanelId())
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(context.getColorFromAttr(R.attr.notificationBackgroundColor))
            .setOngoing(true)
            .setColorized(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setCustomContentView(content)

        `package`?.let {
            val intent = context.packageManager.getLaunchIntentForPackage(it)
            builder.setContentIntent(
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            )
        }

        return builder.build()
    }

    private fun getGraphsChanelId(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            pushManager.deleteNotificationChannel(GRAPHS_CHANNEL_OLD) // Migrate from old channel
            createNotificationChannel(
                pushManager,
                GRAPHS_CHANNEL_ID,
                context.getString(R.string.graphs_channel_name),
                context.getString(R.string.graphs_channel_description)
            )
        } else ""
    }

    private fun getAlertsChanelId(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                pushManager,
                ALERTS_CHANNEL_ID,
                context.getString(R.string.alerts_channel_name),
                context.getString(R.string.alerts_channel_description)
            )
        } else ""
    }
}
