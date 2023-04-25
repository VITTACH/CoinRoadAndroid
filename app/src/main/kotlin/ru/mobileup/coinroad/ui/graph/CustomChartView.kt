package ru.mobileup.coinroad.ui.graph

import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.*
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.androidplot.ui.Insets
import com.androidplot.ui.Size
import com.androidplot.ui.SizeMode
import com.androidplot.util.PixelUtils
import com.androidplot.util.SeriesUtils
import com.androidplot.xy.*
import com.androidplot.xy.PanZoom.*
import com.androidplot.xy.SimpleXYSeries.ArrayFormat.Y_VALS_ONLY
import com.github.ajalt.timberkt.d
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.util.*
import ru.mobileup.coinroad.util.ui.getColorFromAttr
import java.math.BigDecimal
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration


class CustomChartView(context: Context, attrs: AttributeSet?) : XYPlot(context, attrs) {

    companion object {
        const val MAX_CHART_CANDLES = 31

        private const val RANGE_STEP = 4
        private const val DOMAIN_STEP = 4f
        private const val MIN_RANGE_STEP = 1
        const val PREVIEW_ID = 0
    }

    constructor(context: Context) : this(context, null)

    private lateinit var formatter: XYSeriesFormatter<XYRegionFormatter>
    private var invisibleFormatter: XYSeriesFormatter<XYRegionFormatter>? = null

    // Paddings
    private var viewPaddingTop = PixelUtils.dpToPix(8f)

    // Margins
    private var chartMarginTop = PixelUtils.dpToPix(44f)
    private var chartMarginLeft = PixelUtils.dpToPix(12f)
    private var chartMarginRight = PixelUtils.dpToPix(12f)
    private var chartMarginBottom = PixelUtils.dpToPix(4f)

    private var tickerTextSize = PixelUtils.spToPix(12f)
    private var bigTextSize = PixelUtils.spToPix(18f)

    // View size
    private var minWidth = PixelUtils.dpToPix(256f)
    private var minHeight = PixelUtils.dpToPix(132f)
        set(value) {
            field = value
            requestLayout()
            invalidate()
        }

    private var isHelpVisible = false

    // Lines
    private var lineWidth = PixelUtils.dpToPix(0.6f)

    // Labels
    private lateinit var lineLabelTextPaint: TextPaint
    private var lineLabelTextSize = PixelUtils.spToPix(10f)
    private var lineLabelColor = 0

    // Background corner radius
    private val bgRadius = PixelUtils.dpToPix(8f)

    // Bars
    private val barMaxWidth = PixelUtils.dpToPix(7f)
    private var barWidth = barMaxWidth
    private var barBodyPaddings = PixelUtils.dpToPix(0.5f)
    private var wickWidth = PixelUtils.dpToPix(1.2f)

    // Chart
    private var gridInsetLeft = 0f
    private lateinit var chartBorderPaint: Paint
    private var domainUpperBoundary = 0
    private var domainLowerBoundary = 0
    private var rangeIndex = -1
    private var rangeLineStroke = PixelUtils.dpToPix(0.6f)
    private var rangeUpperBoundary = 0.0
    private var rangeLowerBoundary = 0.0
    private var barRisingColor = 0
    private var barFallingColor = 0
    private var barFlatColor = 0

    // Texts
    private val textPadding = PixelUtils.dpToPix(8f)
    private lateinit var primaryTextPaint: TextPaint
    private lateinit var secondaryTextPaint: TextPaint
    private lateinit var bigPrimaryTextPaint: TextPaint
    private lateinit var bigSecondaryTextPaint: TextPaint
    private lateinit var helpTextPaint: TextPaint
    private lateinit var fallingTextPaint: TextPaint
    private lateinit var risingTextPaint: TextPaint
    private lateinit var flatTextPaint: TextPaint

    // Payload
    private var curChartId = PREVIEW_ID
    private var lastUpdateTime = mutableMapOf<Int, Long>()
    private var graphDataItem = mutableMapOf<Int, GraphDataItem>()
        .apply { put(PREVIEW_ID, GraphDataItem.DEFAULT) }

    // Alerts
    private val trianglePath = Path()
    private val alertPaint = Paint().apply { style = Paint.Style.FILL }
    private val alertSize = PixelUtils.dpToPix(12f)
    private val alertImage: Bitmap? by lazy {
        AppCompatResources.getDrawable(context, R.drawable.ic_24_alerts)?.toBitmap()?.let {
            Bitmap.createScaledBitmap(it, alertSize.toInt(), alertSize.toInt(), false)
        }
    }

    // Logo
    private var isLogoVisible = true
    private val logoSize = PixelUtils.dpToPix(28f)
    private val logoImage: Bitmap? by lazy {
        AppCompatResources.getDrawable(context, R.mipmap.ic_launcher)?.toBitmap()?.let {
            Bitmap.createScaledBitmap(it, logoSize.toInt(), logoSize.toInt(), false)
        }
    }

    // Gradients
    private val gradientHeight = logoSize + viewPaddingTop
    private val gradientPaintRed: Paint by lazy {
        Paint().apply { shader = createGradient(context.getColorFromAttr(R.attr.gradientRedColor)) }
    }
    private val gradientPaintGreen: Paint by lazy {
        Paint().apply {
            shader = createGradient(context.getColorFromAttr(R.attr.gradientGreenColor))
        }
    }
    private val gradientPaintBlack: Paint by lazy {
        Paint().apply {
            shader = createGradient(context.getColorFromAttr(R.attr.gradientBlackColor))
        }
    }
    private val gradientRect: Rect by lazy {
        Rect(0, 0, width, gradientHeight.toInt())
    }

    private val stateListener = object : CustomPanZoom.StateListener {
        override fun onSetDomainBoundaries(lowerBoundary: Float, upperBoundary: Float) {
            domainUpperBoundary = upperBoundary.toInt()
            domainLowerBoundary = lowerBoundary.toInt()
            barWidth = barMaxWidth / (upperBoundary - lowerBoundary) * MAX_CHART_CANDLES

            when (formatter) {
                is CustomCandlestickFormatter -> {
                    (formatter as CustomCandlestickFormatter).calcBarBodyWidth()
                }
            }

            calcMinMaxAndRedraw()
        }

        override fun onSetRangeBoundaries(lowerBoundary: Float, upperBoundary: Float) {
            rangeIndex = -1
            rangeUpperBoundary = upperBoundary.toDouble()
            rangeLowerBoundary = lowerBoundary.toDouble()

            calcMinMaxAndRedraw()
        }
    }

    init {
        initAttributes(context, attrs)
        initView()

        // Show preview
        updatePlot(PREVIEW_ID, graphDataItem[PREVIEW_ID]!!, isHelpVisible = true)

        // Zoom and scroll
        attachZoom()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = minWidth.toInt() + paddingLeft + paddingRight
        val desiredHeight = minHeight.toInt() + paddingTop + paddingBottom

        val curHeight = measureDimension(desiredHeight, heightMeasureSpec)
        val curWidth = measureDimension(desiredWidth, widthMeasureSpec)
        setMeasuredDimension(curWidth, curHeight)

        if (formatter is LineAndPointFormatter) {
            (formatter as LineAndPointFormatter).updateChartLinearGradient(curHeight.toFloat())
        }

        val dataItem = graphDataItem[curChartId] ?: return
        val series = dataItem.candlestickSeries
        val (maxPrice, minPrice) = series.calcMinMax()
        updateRangeStep(dataItem, maxPrice, minPrice, curHeight)

        setTickerPosition(series.getTickerPrice(), dataItem.isTickerVisible, curHeight)
    }

    override fun onDraw(canvas: Canvas) {
        with(canvas) {
            super.onDraw(this)

            // draw corners
            drawRoundRect(
                0f, 0f, width.toFloat(), height.toFloat(), bgRadius, bgRadius, chartBorderPaint
            )

            // draw gradient
            drawBackgroundGradient()

            // draw Logo
            if (isLogoVisible) {
                logoImage?.let { drawBitmap(it, chartMarginLeft, viewPaddingTop, null) }
            }

            drawAlerts()
            drawTickerData()
        }
    }

    fun getBitmap(): Bitmap {
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = resources.getDimension(R.dimen.graph_chart_height).toInt()

        val w = if (measuredWidth == 0) width else measuredWidth
        val h = if (measuredHeight == 0) height else measuredHeight
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)

        return bitmap
    }

    fun updatePlot(curChartId: Int, dataItem: GraphDataItem, isHelpVisible: Boolean = false) {

        val series = dataItem.candlestickSeries
        domainLowerBoundary = series.closeSeries.size() - MAX_CHART_CANDLES
        domainUpperBoundary = series.closeSeries.size()

        val (maxPrice, minPrice) = series.calcMinMax(force = true)

        this.curChartId = curChartId
        this.isHelpVisible = isHelpVisible

        updateRangeStep(dataItem, maxPrice, minPrice)
        setDomainStep(StepMode.INCREMENT_BY_VAL, (MAX_CHART_CANDLES / DOMAIN_STEP).toDouble())
        setDomainBoundaries(
            domainLowerBoundary,
            min(domainUpperBoundary, series.highSeries.size()),
            BoundaryMode.FIXED
        )

        val precision = dataItem.ticker.precision
        val formattedPrice = formatNumber(BigDecimal(maxPrice), precision)
        graph.gridInsets = Insets(
            0f,
            chartMarginRight,
            primaryTextPaint.measureText(formattedPrice) + gridInsetLeft,
            0f
        )

        setTickerPosition(series.getTickerPrice(), dataItem.isTickerVisible)

        when (dataItem.chartType) {
            ChartType.CandleStick -> initCustomCandlestickFormatter()
            ChartType.Linear -> initLineAndPointFormatter()
        }

        formatter.updateAndDrawMinMaxPrices(maxPrice, minPrice, dataItem, precision)
        invisibleFormatter?.updateAndDrawMinMaxPrices(maxPrice, minPrice, dataItem, precision)

        clear()

        when (formatter) {
            is LineAndPointFormatter -> {
                addSeries(
                    SimpleXYSeries(series.lowSeries.getyVals(), Y_VALS_ONLY, ""), invisibleFormatter
                )
                addSeries(
                    SimpleXYSeries(series.highSeries.getyVals(), Y_VALS_ONLY, ""), formatter
                )
            }

            is CustomCandlestickFormatter -> {
                (formatter as CustomCandlestickFormatter).calcBarBodyWidth()
                CandlestickMaker.make(this, formatter as CustomCandlestickFormatter, series)
            }
        }

        graphDataItem[this.curChartId] = dataItem
        lastUpdateTime[this.curChartId] = dataItem.updateTime

        redraw()
    }

    private fun Canvas.drawAlerts() {
        val dataItem = graphDataItem[curChartId] ?: return
        val alertLeft = gridInsetLeft + PixelUtils.dpToPix(2f)
        val alertRight = graph.gridInsets.left - alertSize / 2
        val leftOffset = PixelUtils.dpToPix(0.5f)

        val oldLineLabelColor = lineLabelTextPaint.color
        lineLabelTextPaint.color = Color.WHITE

        dataItem.alerts.forEach { alert ->
            val alertPositionY = calcPricePositionY(alert.price.toDouble(), height) - alertSize / 2
            alertPaint.colorFilter = PorterDuffColorFilter(alert.color, PorterDuff.Mode.SRC_IN)

            // Draw alert bell
            alertImage?.let { drawBitmap(it, leftOffset, alertPositionY, alertPaint) }

            trianglePath.reset()
            trianglePath.moveTo(graph.gridInsets.left - alertSize / 2, alertPositionY)
            trianglePath.lineTo(graph.gridInsets.left, alertPositionY + alertSize / 2)
            trianglePath.lineTo(graph.gridInsets.left - alertSize / 2, alertPositionY + alertSize)
            trianglePath.lineTo(graph.gridInsets.left - alertSize / 2, alertPositionY)
            trianglePath.close()
            drawPath(trianglePath, alertPaint)

            drawRect(alertLeft, alertPositionY, alertRight, alertPositionY + alertSize, alertPaint)

            // Draw alert price
            val price = formatNumber(alert.price, dataItem.ticker.precision)
            val alertTextY = alertPositionY + lineLabelTextPaint.textSize + leftOffset
            drawText(price, alertLeft + leftOffset, alertTextY, lineLabelTextPaint)
        }

        lineLabelTextPaint.color = oldLineLabelColor
    }

    private fun setTickerPosition(
        price: Double,
        isTickerVisible: Boolean,
        currentHeight: Int = height
    ) {
        graph.rangeCursorPosition = if (isTickerVisible) {
            calcPricePositionY(price, currentHeight) - rangeLineStroke
        } else 0f
    }

    private fun calcPricePositionY(price: Double, currentHeight: Int): Float {
        val percentDiff = (price - rangeUpperBoundary) / (rangeLowerBoundary - rangeUpperBoundary)
        val bottom = currentHeight.toFloat() - graph.gridInsets.bottom - chartMarginBottom
        val top = graph.gridInsets.top + chartMarginTop
        return max(top, min(bottom, top + (bottom - top) * percentDiff.toFloat()))
    }

    private fun CustomCandlestickFormatter.calcBarBodyWidth() {
        val maxWidth = fetchViewWidth() - chartMarginLeft - graph.gridInsets.left - chartMarginRight
        bodyWidth = min(max(barWidth / 2, maxWidth / MAX_CHART_CANDLES - barBodyPaddings), barWidth)
    }

    private fun List<Number>.filterPriceSeries(force: Boolean): List<Number> {
        return if (!force) {
            filter { it.toDouble() in rangeLowerBoundary..rangeUpperBoundary }
        } else this
    }

    private fun calcMinMaxAndRedraw() {
        val dataItem = graphDataItem[curChartId] ?: return
        val precision = dataItem.ticker.precision
        val series = dataItem.candlestickSeries

        setTickerPosition(series.getTickerPrice(), dataItem.isTickerVisible)

        series.calcMinMax().let {
            formatter.updateAndDrawMinMaxPrices(it.first, it.second, dataItem, precision)
            invisibleFormatter?.updateAndDrawMinMaxPrices(it.first, it.second, dataItem, precision)
        }
    }

    private fun CandlestickSeries.calcMinMax(force: Boolean = false): Pair<Double, Double> {
        val lower = max(0, domainLowerBoundary)
        val upper = min(domainUpperBoundary, highSeries.size())
        if (lower >= upper) return 0.0 to 0.0

        val highSeries = highSeries.getyVals().subList(lower, upper).filterPriceSeries(force)
        val lowSeries = lowSeries.getyVals().subList(lower, upper).filterPriceSeries(force)

        return SeriesUtils.minMax(highSeries, lowSeries).let {
            (it.max?.toDouble() ?: 0.0) to (it.min?.toDouble() ?: 0.0)
        }
    }

    private fun updateRangeStep(
        dataItem: GraphDataItem,
        maxPrice: Double,
        minPrice: Double,
        currentHeight: Int = height
    ) {
        val rangeStep = (RANGE_STEP * currentHeight / minHeight).toInt()
        val priceStep = ((maxPrice - minPrice) / max(MIN_RANGE_STEP, rangeStep)).let {
            return@let when {
                it > 0 -> it
                minPrice == maxPrice -> minPrice / 2
                else -> minPrice
            }
        }

        val heightStep = (currentHeight / minHeight).toInt()
        val priceChangeFor24h = dataItem.ticker.priceChangeForLastDay
        val offsetDown = priceStep * heightStep
        val offsetUp = if (priceChangeFor24h > BigDecimal.ZERO) {
            priceStep * max(1, heightStep)
        } else (maxPrice - minPrice)


        rangeUpperBoundary = maxPrice + offsetUp
        rangeLowerBoundary = max(0.0, minPrice - offsetDown)

        setRangeBoundaries(rangeLowerBoundary, rangeUpperBoundary, BoundaryMode.FIXED)
        setRangeStep(StepMode.INCREMENT_BY_VAL, priceStep)
    }

    // Draw labels for min/max prices:
    private fun XYSeriesFormatter<XYRegionFormatter>.updateAndDrawMinMaxPrices(
        maxPrice: Double,
        minPrice: Double,
        dataItem: GraphDataItem,
        precision: Int
    ) {
        val closeCandlesSeriesSize = dataItem.candlestickSeries.closeSeries.size() - 1

        var isInited = false
        var isFoundMin = false
        var isFoundMax = false

        pointLabelFormatter.hOffset = if (this is CustomCandlestickFormatter) this.bodyWidth else 0f

        pointLabeler = PointLabeler<XYSeries> { series, index ->
            val price = series.getY(index).toDouble()
            if (index == 0 && !isInited) {
                isFoundMin = false
                isFoundMax = false
                isInited = true
            } else if (index == closeCandlesSeriesSize) {
                isInited = false
            }

            if (index < domainLowerBoundary || index > domainUpperBoundary) {
                ""
            } else if (price == minPrice && !isFoundMax && dataItem.isMinMaxVisible) {
                isFoundMax = true
                formatNumber(BigDecimal(price), precision)
            } else if (price == maxPrice && !isFoundMin && dataItem.isMinMaxVisible) {
                isFoundMin = true
                formatNumber(BigDecimal(price), precision)
            } else ""
        }
    }

    private fun Canvas.drawBackgroundGradient() =
        graphDataItem[curChartId]?.run {
            val lastClose = candlestickSeries.closeSeries.getyVals().lastOrNull() as? Double ?: 0.0
            val openSeries = candlestickSeries.openSeries.getyVals()
            val penultOpen = if (openSeries.size > 1) {
                openSeries[openSeries.size - 2] as Double
            } else 0.0

            val paint = when {
                lastClose > penultOpen -> gradientPaintGreen
                lastClose < penultOpen -> gradientPaintRed
                else -> gradientPaintBlack
            }
            drawRect(gradientRect, paint)
        }

    private fun Canvas.drawTickerData() {
        val x = (if (isLogoVisible) logoSize + textPadding else 0f) + chartMarginLeft
        var y = viewPaddingTop + primaryTextPaint.textSize

        // First Line
        y = drawTickerPairAndExchange(x, y)

        // Second line
        y += primaryTextPaint.textSize + textPadding / 2f
        drawTickerPercentChanges(x, y)

        // Timestamp
        drawTimestamp()
    }

    private fun Canvas.drawTickerPairAndExchange(x: Float, y: Float): Float {
        var leftX = x
        var rightX = x
        var x = x
        var y = y
        with(graphDataItem[curChartId] ?: return y) {
            val price = this.formatPrice(BigDecimal(candlestickSeries.getTickerPrice()))
            x = width.toFloat() - bigPrimaryTextPaint.measureText(price) - chartMarginRight
            drawText(price, x, y, bigPrimaryTextPaint)

            val lastUpdate = lastUpdateTime[curChartId] ?: return@with
            val text =
                if (System.currentTimeMillis() - lastUpdate < timeStep.duration.inMilliseconds) {
                    resources.getString(R.string.actual).uppercase()
                } else {
                    formatLocale(context, lastUpdate)
                }
            x -= primaryTextPaint.measureText(text) + textPadding
            drawText(text, x, y, secondaryTextPaint)
            rightX = x

            x = leftX
            drawText(ticker.currencyPair.baseCurrency.name, x, y, bigPrimaryTextPaint)
            x += bigPrimaryTextPaint.measureText(ticker.currencyPair.baseCurrency.name) + textPadding / 2
            drawText("/", x, y, bigSecondaryTextPaint)
            x += bigPrimaryTextPaint.measureText("/") + textPadding / 2
            drawText(ticker.currencyPair.quoteCurrency.name, x, y, bigPrimaryTextPaint)
            x += bigPrimaryTextPaint.measureText(ticker.currencyPair.quoteCurrency.name) + textPadding

            val exchange = TextUtils.ellipsize(
                ticker.exchange.name.uppercase(),
                secondaryTextPaint,
                rightX - x - primaryTextPaint.measureText("[") * 2 - textPadding / 2,
                TextUtils.TruncateAt.END
            )

            drawText("[$exchange]", x, y, secondaryTextPaint)
        }

        return y
    }

    private fun Canvas.drawTickerPercentChanges(x: Float, y: Float) {
        var x = x
        var y = y
        with(graphDataItem[curChartId] ?: return) {
            val price = ticker.priceChangeForLastDay
            val (paint, priceDirection) = when {
                price > BigDecimal.ZERO -> risingTextPaint to resources.getString(R.string.price_asc)
                price < BigDecimal.ZERO -> fallingTextPaint to resources.getString(R.string.price_desc)
                else -> flatTextPaint to resources.getString(R.string.price_flat)
            }
            val changeForLastDay =
                formatNumber(ticker.priceChangeForLastDay.abs(), ticker.precision)
            drawText(changeForLastDay, x, y, paint)
            x += primaryTextPaint.measureText(changeForLastDay) + textPadding / 2
            drawText(priceDirection, x, y, paint)
            x += primaryTextPaint.measureText(priceDirection) + textPadding / 2
            val percent =
                String.format(
                    Locale.ENGLISH,
                    resources.getString(R.string.prcentChange),
                    ticker.priceChangeInPercentForLastDay
                )
            drawText(percent, x, y, paint)
            x += primaryTextPaint.measureText(percent) + textPadding
            if (isHelpVisible) {
                drawText(resources.getString(R.string.helpHint), x, y, helpTextPaint)
            }
        }
    }

    private fun Canvas.drawTimestamp() {
        with(graphDataItem[curChartId] ?: return) {

            val time = resources.getString(
                R.string.timestep,
                timeStep.duration.inMinutes.toInt(),
                "MIN"
            )
            d { "time = $time" }
            val x = width.toFloat() - chartMarginRight - primaryTextPaint.measureText(time)
            val y = chartMarginTop - primaryTextPaint.textSize / 2f
            drawText(time, x, y, secondaryTextPaint)
        }
    }

    private fun initView() {
        graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).apply {
            format = object : Format() {
                override fun format(value: Any?, buffer: StringBuffer, field: FieldPosition) = run {
                    val offset = (value as Number).toInt()

                    val lastUpdate = lastUpdateTime[curChartId]
                    val dataItem = graphDataItem[curChartId]
                    if (offset < 0 || lastUpdate == null || dataItem == null) {
                        return buffer
                    }

                    val size = dataItem.candlestickSeries.openSeries.size() - 1
                    val timeStep = dataItem.timeStep.duration.inMinutes.toInt()

                    val formattedTime = formatLocale(
                        context,
                        lastUpdate - Duration.minutes((size - offset) * timeStep).inWholeMilliseconds
                    )

                    buffer.append(formattedTime)
                }

                override fun parseObject(string: String, position: ParsePosition): Any? {
                    return null
                }
            }
            paint = Paint(lineLabelTextPaint).apply { textAlign = Paint.Align.RIGHT }
        }

        graph.getLineLabelStyle(XYGraphWidget.Edge.LEFT).apply {
            format = object : Format() {
                override fun format(value: Any?, buffer: StringBuffer, field: FieldPosition) = run {
                    (value as? Number)?.toDouble()?.let {
                        val dataItem = graphDataItem[curChartId] ?: return buffer
                        if (height > minHeight && rangeIndex++ % 2 == 0) {
                            return buffer
                        }
                        buffer.append(formatNumber(BigDecimal(it), dataItem.ticker.precision))
                    }
                }

                override fun parseObject(string: String, position: ParsePosition): Any? {
                    return null
                }
            }
            paint = Paint(lineLabelTextPaint).apply { textAlign = Paint.Align.LEFT }
        }
    }

    private fun initAttributes(context: Context, attrs: AttributeSet?) = with(graph) {
        val values = context.obtainStyledAttributes(attrs, R.styleable.xy_XYPlot)
        val customValues = context.obtainStyledAttributes(attrs, R.styleable.CustomChartView)

        initChartGridAndLabels(values, customValues)
        initChartSizeAndMargins(values)

        initBarsAndText(customValues)

        rangeCursorPaint.strokeWidth = rangeLineStroke

        // cleanup resources
        values.recycle()
        customValues.recycle()
    }

    private fun XYGraphWidget.initChartGridAndLabels(values: TypedArray, customValues: TypedArray) {

        val lineColor = context.getColorFromAttr(R.attr.chartLinesColor)
        val backColor = context.getColorFromAttr(R.attr.notificationBackgroundColor)

        // Show axis labels
        setLineLabelEdges(setOf(XYGraphWidget.Edge.LEFT, XYGraphWidget.Edge.BOTTOM))

        lineLabelColor =
            customValues.getColor(R.styleable.CustomChartView_lineLabelColor, Color.LTGRAY)
        lineLabelTextPaint = createTextPaint(lineLabelTextSize).apply { color = lineLabelColor }

        legend.isVisible = false

        isLogoVisible = customValues.getBoolean(R.styleable.CustomChartView_logoVisible, true)

        gridBackgroundPaint.color =
            values.getColor(R.styleable.xy_XYPlot_gridBackgroundColor, backColor)
        setBackgroundColor(values.getColor(R.styleable.xy_XYPlot_graphBackgroundColor, backColor))

        domainOriginLinePaint = Paint().apply { color = Color.TRANSPARENT }
        rangeOriginLinePaint = Paint().apply {
            strokeWidth =
                values.getDimension(R.styleable.xy_XYPlot_rangeOriginLineThickness, lineWidth)
            color = values.getColor(R.styleable.xy_XYPlot_rangeOriginLineColor, lineColor)
        }

        domainGridLinePaint = Paint().apply {
            strokeWidth = values.getDimension(R.styleable.xy_XYPlot_domainLineThickness, lineWidth)
            color = values.getColor(R.styleable.xy_XYPlot_domainLineColor, lineColor)
        }

        rangeGridLinePaint = Paint().apply {
            strokeWidth = values.getDimension(R.styleable.xy_XYPlot_rangeLineThickness, lineWidth)
            color = values.getColor(R.styleable.xy_XYPlot_rangeLineColor, lineColor)
        }

        backgroundPaint = Paint().apply {
            color = values.getColor(R.styleable.xy_XYPlot_graphBackgroundColor, backColor)
        }

        // Border for background
        chartBorderPaint = Paint().apply {
            color = backColor
            style = Paint.Style.STROKE
            strokeWidth = PixelUtils.dpToPix(4f)
        }
    }

    private fun XYGraphWidget.initChartSizeAndMargins(values: TypedArray) {

        // Margins and paddings
        setMargins(chartMarginLeft, chartMarginTop, chartMarginRight, chartMarginBottom)
        gridInsetLeft = values.getDimension(R.styleable.xy_XYPlot_gridInsetLeft, 0f)
        gridInsets = Insets(0f, chartMarginRight, gridInsetLeft, 0f)

        // Full screen chart
        val graphHeight = values.getFloat(R.styleable.xy_XYPlot_graphHeight, 1.0f)
        val graphHeightMode =
            values.getInt(R.styleable.xy_XYPlot_graphHeightMode, SizeMode.FILL.ordinal)
        val graphWidth = values.getFloat(R.styleable.xy_XYPlot_graphWidth, 1.0f)
        val graphWidthMode =
            values.getInt(R.styleable.xy_XYPlot_graphWidthMode, SizeMode.FILL.ordinal)
        val heightLayoutType = SizeMode.values()[graphHeightMode]
        val widthLayoutType = SizeMode.values()[graphWidthMode]
        size = Size(graphHeight, heightLayoutType, graphWidth, widthLayoutType)
    }

    private fun initBarsAndText(customValues: TypedArray) {

        // Bars
        barRisingColor =
            customValues.getColor(R.styleable.CustomChartView_barRisingColor, Color.GREEN)
        barFallingColor =
            customValues.getColor(R.styleable.CustomChartView_barFallingColor, Color.RED)
        barFlatColor =
            customValues.getColor(R.styleable.CustomChartView_helpColor, Color.YELLOW)

        // Text data paints
        fallingTextPaint = createTextPaint().apply { color = barFallingColor }
        risingTextPaint = createTextPaint().apply { color = barRisingColor }

        flatTextPaint = createTextPaint()
            .apply { color = context.getColorFromAttr(R.attr.secondaryTextColor) }
        primaryTextPaint = createTextPaint()
            .apply { color = context.getColorFromAttr(R.attr.textColor) }
        bigPrimaryTextPaint = createTextPaint(bigTextSize)
            .apply { color = context.getColorFromAttr(R.attr.textColor) }
        bigSecondaryTextPaint = createTextPaint(bigTextSize)
            .apply { color = context.getColorFromAttr(R.attr.secondaryTextColor) }
        secondaryTextPaint = createTextPaint()
            .apply { color = context.getColorFromAttr(R.attr.secondaryTextColor) }
        helpTextPaint = createTextPaint()
            .apply { color = barFlatColor }

    }

    private fun LineAndPointFormatter.updateChartLinearGradient(height: Float) {
        val color2 = Color.TRANSPARENT
        val newHeight = max(height, context.resources.getDimension(R.dimen.graph_chart_height))
        fillPaint.shader =
            LinearGradient(0f, 0f, 0f, newHeight, barRisingColor, color2, Shader.TileMode.CLAMP)
    }

    private fun initLineAndPointFormatter() {
        formatter = LineAndPointFormatter(context, R.xml.chart_linear).apply {
            interpolationParams =
                CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)
            fillPaint = Paint().apply { alpha = 100 }
            updateChartLinearGradient(this@CustomChartView.height.toFloat())

            linePaint.apply { strokeWidth = PixelUtils.dpToPix(1f); color = Color.GREEN }

            pointLabelFormatter.textPaint = primaryTextPaint
            pointLabelFormatter.textPaint.textAlign = Paint.Align.CENTER
        }
        invisibleFormatter = LineAndPointFormatter(context, R.xml.chart_linear).apply {
            interpolationParams =
                CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)

            pointLabelFormatter.textPaint = primaryTextPaint
            pointLabelFormatter.textPaint.textAlign = Paint.Align.CENTER
        }
    }

    private fun initCustomCandlestickFormatter() {
        formatter = CustomCandlestickFormatter(
            context = context,
            wickUpPaint = Paint().apply { strokeWidth = wickWidth; color = barRisingColor },
            wickDownPaint = Paint().apply { strokeWidth = wickWidth; color = barFallingColor },
            bodyFlatPaint = Paint().apply { color = barFlatColor }
        ).apply {
            bodyStyle = CandlestickFormatter.BodyStyle.SQUARE

            risingBodyStrokePaint = Paint().apply { color = barRisingColor }
            fallingBodyStrokePaint = Paint().apply { color = barFallingColor }

            pointLabelFormatter.textPaint = primaryTextPaint
            pointLabelFormatter.textPaint.textAlign = Paint.Align.CENTER

            upperCapWidth = 0f
            lowerCapWidth = 0f
        }
    }

    private fun createTextPaint(size: Float = tickerTextSize): TextPaint {
        return TextPaint().apply {
            isAntiAlias = true
            textSize = size
            textAlign = Paint.Align.LEFT
            typeface = ResourcesCompat.getFont(context, R.font.roboto_mono_regular)
        }
    }

    private fun createGradient(color1: Int, height: Float = gradientHeight): LinearGradient {
        val color2 = chartBorderPaint.color
        val x = width.toFloat() / 2
        return LinearGradient(x, 0f, x, height, color1, color2, Shader.TileMode.CLAMP)
    }

    private fun attachZoom() {
        CustomPanZoom.attach(
            this,
            Pan.BOTH,
            Zoom.STRETCH_VERTICAL,
            ZoomLimit.MIN_TICKS,
            stateListener
        )
        registry.estimator = ZoomEstimator()
    }

    private fun fetchViewWidth(): Int {
        return if (width <= 0) {
            Resources.getSystem().displayMetrics.widthPixels
        } else width
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)

        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize
            MeasureSpec.AT_MOST -> min(desiredSize, specSize)
            else -> desiredSize
        }
    }
}