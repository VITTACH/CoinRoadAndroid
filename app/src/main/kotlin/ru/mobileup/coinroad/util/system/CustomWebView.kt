package ru.mobileup.coinroad.util.system

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import androidx.core.view.MotionEventCompat

class CustomWebView : WebView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, style: Int) : super(context, attrs, style)

    override fun onTouchEvent(event: MotionEvent): Boolean {

        // Check pointer index to avoid -1 (error)
        if (MotionEventCompat.findPointerIndex(event, 0) == -1) {
            return super.onTouchEvent(event)
        }
        if (event.pointerCount >= 2) {
            requestDisallowInterceptTouchEvent(false)
        } else {
            requestDisallowInterceptTouchEvent(true)
        }
        return super.onTouchEvent(event)
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY)
        requestDisallowInterceptTouchEvent(true)
    }
}