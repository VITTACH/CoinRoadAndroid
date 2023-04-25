package ru.mobileup.coinroad.util.ui

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import com.google.android.material.tabs.TabLayout
import com.skydoves.balloon.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.databinding.ScreenGraphCreatingBinding
import java.io.ByteArrayOutputStream


@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

@ColorInt
fun Resources.color(@ColorRes colorId: Int) = ResourcesCompat.getColor(this, colorId, null)

fun Button.setClickableActive(mode: Boolean) {
    isClickable = mode
    isFocusable = mode
}

fun Button.setSecondaryColor() {
    setTextColor(context.getColorFromAttr(R.attr.textColor))
    val colors = context.getColorFromAttr(R.attr.secondaryButtonBackgroundColor)
    backgroundTintList = ColorStateList.valueOf(colors)
}

fun Button.setPrimaryColor() {
    setTextColor(context.getColorFromAttr(R.attr.textColorSelected))
    val colors = context.getColorFromAttr(R.attr.buttonBackgroundColor)
    backgroundTintList = ColorStateList.valueOf(colors)
}

inline fun View.waitForLayout(crossinline action: () -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            when {
                viewTreeObserver.isAlive -> {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    action()
                }
                else -> viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        }
    })
}

fun View.visible(visible: Boolean, useGone: Boolean = true) {
    this.visibility = if (visible) View.VISIBLE else if (useGone) View.GONE else View.INVISIBLE
}

inline fun View.doOnClick(crossinline action: () -> Unit) {
    this.setOnClickListener { action() }
}

val View.screenLocation
    get(): IntArray {
        val point = IntArray(2)
        getLocationOnScreen(point)
        return point
    }

fun View.screenLocationSafe(callback: (Int, Int) -> Unit) {
    post {
        val (x, y) = screenLocation
        callback(x, y)
    }
}

fun View.hideKeyboard() {
    clearFocus()
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Bitmap.toByteArray(): ByteArray {
    val byteStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
    return byteStream.toByteArray()
}

fun Bitmap.toNotificationSize(compressedValue: Int): Bitmap {
    var bitmap = this
    var resizeValue = compressedValue
    val maxBitmapSizeKb = 501 // We have limit 1mb for all buffer data
    while (bitmap.byteCount / 1024 > maxBitmapSizeKb) {
        bitmap = getResizedBitmap(resizeValue--)
    }
    return bitmap
}

fun Bitmap.getResizedBitmap(maxSize: Int): Bitmap {
    var width = this.width
    var height = this.height
    val bitmapRatio = width.toFloat() / height.toFloat()

    if (bitmapRatio > 1) {
        width = maxSize
        height = (width / bitmapRatio).toInt()
    } else {
        height = maxSize
        width = (height * bitmapRatio).toInt()
    }

    return createScaledBitmap(this, width, height, true)
}

fun Context.createShowCases(
    titleText: CharSequence,
    actionText: CharSequence,
    orientation: ArrowOrientation,
    arrowPosition: Float,
    preferenceName: String,
    emsText: Int = 10,
    @StringRes closeStringRes: Int = R.string.showcase_got_it,
    @DrawableRes imageRes: Int? = null,
    onCloseAction: () -> Unit = {},
): Balloon {
    val balloon = createBalloon(this) {
        setLayout(R.layout.layout_item_showcase)
        setOnBalloonDismissListener { onCloseAction.invoke() }
        setBalloonAnimation(BalloonAnimation.OVERSHOOT)
        setWidthRatio(1f)
        setHeight(BalloonSizeSpec.WRAP)
        setArrowColorResource(R.color.green_a)
        setArrowSize(20)
        setArrowTopPadding(8)
        setArrowPosition(arrowPosition)
        setArrowOrientation(orientation)
        setBackgroundColorResource(android.R.color.transparent)
        setPreferenceName(preferenceName)
        setShowCounts(1)
    }

    with(balloon.getContentView()) {
        findViewById<CardView>(R.id.root_layout).apply {
            (layoutParams as FrameLayout.LayoutParams).gravity =
                if (orientation == ArrowOrientation.BOTTOM) Gravity.BOTTOM else Gravity.TOP
        }

        findViewById<ImageView>(R.id.image_view).apply {
            isVisible = imageRes != null
            imageRes?.let { setImageResource(it) }
        }

        findViewById<TextView>(R.id.title_text).apply {
            setEms(emsText)
            text = titleText
        }

        findViewById<TextView>(R.id.action_btn).apply {
            setText(closeStringRes)
            setOnClickListener {
                balloon.dismiss()
                onCloseAction.invoke()
            }
            text = actionText
        }
    }
    return balloon
}

fun ScreenGraphCreatingBinding.onChartHeightChange(isFullScreen: Boolean) {
    val newHeight = if (isFullScreen) {
        root.context.resources.getDimension(R.dimen.graph_chart_full_height)
    } else {
        root.context.resources.getDimension(R.dimen.graph_chart_height)
    }.toInt()

    val resId = if (!isFullScreen) {
        R.drawable.ic_24_fullscreen_on
    } else R.drawable.ic_24_fullscreen_off

    fullScreenBtn.setImageResource(resId)
    graphPlot.layoutParams =
        FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, newHeight)
    graphTouchArea.layoutParams =
        (graphTouchArea.layoutParams as ConstraintLayout.LayoutParams).apply {
            height = newHeight
        }
}

fun ScreenGraphCreatingBinding.onGraphScreenScroll(scrollY: Int) {
    with(graphTouchArea) {
        val originalHeight = graphPlot.height
        if (scrollY < marginTop) {
            translationY = -scrollY.toFloat()
            layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                height = originalHeight
            }
        } else {
            translationY = -marginTop.toFloat()
            layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                height = originalHeight + marginTop - scrollY
            }
        }
    }
}

fun EditText.applyTextWatcher(
    inputFilterPattern: Regex? = null,
    listenDelKey: Boolean = true,
    formatter: (String) -> String,
) {
    val (oldFilterPattern, oldListener) = tag as? Pair<Regex?, TextWatcher> ?: null to null
    if (inputFilterPattern != null && oldFilterPattern == inputFilterPattern) return

    removeTextChangedListener(oldListener)

    val listener = object : TextWatcher {
        private var previousText = ""

        override fun afterTextChanged(s: Editable) {
            if (listenDelKey && s.length < previousText.length) {
                previousText = s.toString()
                return
            }

            removeTextChangedListener(this)

            val previousSelection = selectionStart

            if (inputFilterPattern == null || inputFilterPattern.matches(s)) {
                val formattedString = formatter(s.toString())
                previousText = formattedString
            }

            val selection = if (previousSelection == s.toString().length) {
                previousText.length
            } else {
                previousSelection
            }

            setText(previousText)
            try {
                setSelection(selection)
            } catch (e: Exception) {
                // nothing
            }

            addTextChangedListener(this)
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) = Unit
    }

    tag = inputFilterPattern to listener

    addTextChangedListener(listener)
}

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow<CharSequence?> {
        val listener = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) = Unit
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                offer(s)
            }
        }
        addTextChangedListener(listener)
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

fun TabLayout.getTabTextView(index: Int): TextView {
    return (((getChildAt(0) as LinearLayout).getChildAt(index) as LinearLayout).getChildAt(1) as TextView)
}