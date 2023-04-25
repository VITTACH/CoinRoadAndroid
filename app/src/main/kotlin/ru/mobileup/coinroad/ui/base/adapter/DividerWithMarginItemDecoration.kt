package ru.mobileup.coinroad.ui.base.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.androidplot.util.PixelUtils
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.util.ui.getColorFromAttr

class DividerWithMarginItemDecoration(
    context: Context,
    private val leftMargin: Int = 0,
    private val rightMargin: Int = 0,
    private val beforeFirst: Boolean = false,
    private val afterFirst: Boolean = true,
    private val afterLast: Boolean = false
) : RecyclerView.ItemDecoration() {

    private val paint = Paint().apply {
        color = context.getColorFromAttr(R.attr.dividerColor)
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        canvas.save()
        val itemCount = state.itemCount

        val left = leftMargin.toFloat()
        val right = parent.width - rightMargin.toFloat()
        val height = PixelUtils.dpToPix(1f)

        parent.children.forEach { view ->
            val position = parent.getChildAdapterPosition(view)

            val top = view.top.toFloat()
            val bottom = view.bottom.toFloat()
            val topWithDivider = top + height
            val bottomWithDivider = bottom - height

            if (position > itemCount) return

            when {
                position == 0 -> {
                    if (afterFirst && itemCount > 1) {
                        canvas.drawRect(left, bottomWithDivider, right, bottom, paint)
                    }
                    if (beforeFirst) canvas.drawRect(left, topWithDivider, right, top, paint)
                }

                position >= 1 -> {
                    if (position + 1 < itemCount) {
                        canvas.drawRect(left, bottomWithDivider, right, bottom, paint)
                    }
                }
            }

            if (position == itemCount - 1 && afterLast && itemCount > 1) {
                canvas.drawRect(left, bottomWithDivider, right, bottom, paint)
            }
        }
        canvas.restore()
    }
}
