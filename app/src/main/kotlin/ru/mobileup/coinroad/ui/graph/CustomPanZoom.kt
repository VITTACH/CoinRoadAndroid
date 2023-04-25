package ru.mobileup.coinroad.ui.graph

import android.view.View.OnTouchListener
import com.androidplot.xy.BoundaryMode
import com.androidplot.xy.PanZoom
import com.androidplot.xy.XYPlot
import java.io.Serializable

class CustomPanZoom : PanZoom, OnTouchListener {

    private var customState: CustomState

    interface StateListener {
        fun onSetDomainBoundaries(lowerBoundary: Float, upperBoundary: Float)
        fun onSetRangeBoundaries(lowerBoundary: Float, upperBoundary: Float)
    }

    constructor(plot: XYPlot, pan: Pan, zoom: Zoom, limit: ZoomLimit, listener: StateListener?) :
            super(plot, pan, zoom, limit) {
        customState = CustomState()
        customState.setStateListener(listener)
    }

    override fun adjustRangeBoundary(lower: Number?, upper: Number?, mode: BoundaryMode?) {
        super.adjustRangeBoundary(lower, upper, mode)
        customState.setRangeBoundaries(lower, upper)
    }

    override fun adjustDomainBoundary(lower: Number?, upper: Number?, mode: BoundaryMode?) {
        super.adjustDomainBoundary(lower, upper, mode)
        customState.setDomainBoundaries(lower, upper)
    }

    class CustomState : Serializable {
        private var listener: StateListener? = null

        fun setDomainBoundaries(lower: Number?, upper: Number?) {
            listener?.onSetDomainBoundaries(lower?.toFloat() ?: 0f, upper?.toFloat() ?: 0f)
        }

        fun setRangeBoundaries(lower: Number?, upper: Number?) {
            listener?.onSetRangeBoundaries(lower?.toFloat() ?: 0f, upper?.toFloat() ?: 0f)
        }

        fun setStateListener(stateListener: StateListener?) {
            listener = stateListener
        }
    }

    companion object {
        @JvmOverloads
        fun attach(
            plot: XYPlot,
            pan: Pan = Pan.BOTH,
            zoom: Zoom = Zoom.SCALE,
            limit: ZoomLimit = ZoomLimit.MIN_TICKS,
            stateListener: StateListener? = null
        ): CustomPanZoom {
            val pz = CustomPanZoom(plot, pan, zoom, limit, stateListener)
            plot.setOnTouchListener(pz)
            return pz
        }
    }
}