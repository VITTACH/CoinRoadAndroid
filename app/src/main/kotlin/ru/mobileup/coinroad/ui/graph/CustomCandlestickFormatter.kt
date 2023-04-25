package ru.mobileup.coinroad.ui.graph

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import com.androidplot.util.PixelUtils
import com.androidplot.xy.CandlestickFormatter
import com.androidplot.xy.CandlestickRenderer
import com.androidplot.xy.XYPlot
import ru.mobileup.coinroad.R


class CustomCandleStickRenderer(
    plot: XYPlot?
) : CandlestickRenderer<CustomCandlestickFormatter?>(plot) {

    private var currentMin = PointF()
    private var currentMax = PointF()

    override fun drawBody(
        canvas: Canvas?,
        open: PointF?,
        close: PointF?,
        candlestickFormatter: CustomCandlestickFormatter?
    ) {
        if (canvas == null || open == null || close == null || candlestickFormatter == null) {
            return
        }

        with(candlestickFormatter) {
            val wickPaint = if (open.y >= close.y) wickUpPaint else wickDownPaint
            canvas.drawLine(currentMin.x, currentMin.y, currentMax.x, currentMax.y, wickPaint)

            val isFlatCandle = open.y == close.y
            if (isFlatCandle) {
                close.y = open.y - PixelUtils.dpToPix(1f)
            }

            val halfWidth = bodyWidth / 2.0f
            val bodyRect = RectF(open.x - halfWidth, open.y, close.x + halfWidth, close.y)

            if (isFlatCandle) {
                canvas.drawRect(bodyRect, bodyFlatPaint)
            } else {
                val bodyFillPaint =
                    if (open.y > close.y) risingBodyFillPaint else fallingBodyFillPaint
                val bodyStrokePaint =
                    if (open.y > close.y) risingBodyStrokePaint else fallingBodyStrokePaint

                canvas.drawRect(bodyRect, bodyFillPaint)
                canvas.drawRect(bodyRect, bodyStrokePaint)
            }
        }
    }

    override fun drawWick(
        canvas: Canvas?,
        min: PointF?,
        max: PointF?,
        candlestickFormatter: CustomCandlestickFormatter?
    ) {
        min?.let { currentMin = it }
        max?.let { currentMax = it }
    }
}

class CustomCandlestickFormatter : CandlestickFormatter {

    // Candle colors
    val wickUpPaint: Paint
    val wickDownPaint: Paint
    val bodyFlatPaint: Paint

    override fun getRendererClass(): Class<CustomCandleStickRenderer> {
        return CustomCandleStickRenderer::class.java
    }

    override fun doGetRendererInstance(plot: XYPlot): CustomCandleStickRenderer {
        return CustomCandleStickRenderer(plot)
    }

    constructor(
        context: Context,
        wickUpPaint: Paint,
        wickDownPaint: Paint,
        bodyFlatPaint: Paint
    ) : this(context, R.xml.chart_candlestick, wickUpPaint, wickDownPaint, bodyFlatPaint)

    constructor(
        context: Context,
        xmlCfgId: Int,
        wickUpPaint: Paint,
        wickDownPaint: Paint,
        bodyFlatPaint: Paint
    ) : super() {
        super.configure(context, xmlCfgId)

        this.wickUpPaint = wickUpPaint
        this.wickDownPaint = wickDownPaint
        this.bodyFlatPaint = bodyFlatPaint
    }
}