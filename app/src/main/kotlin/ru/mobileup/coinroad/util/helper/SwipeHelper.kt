package ru.mobileup.coinroad.util.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.mobileup.coinroad.R
import ru.mobileup.coinroad.util.ui.getColorFromAttr
import kotlin.math.min


class SwipeHelper {

    interface SwipeListener {
        fun onSwipeItem(position: Int)
        fun onMove() {}
        fun onMoved() {}
    }

    private var context: Context? = null
    private var swipeListener: SwipeListener? = null
    private var hasHeader = false

    fun onSetListener(context: Context, hasHeader: Boolean, swipeListener: SwipeListener?) {
        this.hasHeader = hasHeader
        this.context = context
        this.swipeListener = swipeListener
    }

    val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        private val backgroundColor = Paint()

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            swipeListener?.onSwipeItem(viewHolder.adapterPosition)
        }

        override fun getSwipeDirs(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if (viewHolder.adapterPosition == 0 && hasHeader) return 0
            return super.getSwipeDirs(recyclerView, viewHolder)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isActive: Boolean
        ) {
            var x = dX
            val view = viewHolder.itemView
            val height = view.bottom - view.top

            if (x == 0f) {
                swipeListener?.onMoved()
            } else {
                swipeListener?.onMove()
            }

            context?.let {
                backgroundColor.color = it.getColorFromAttr(R.attr.swipeBackgroundColor)
            }

            val backgroundRect = if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX <= 0) {
                x = -min(-dX, height * 2f)
                RectF(
                    view.right + x,
                    view.top.toFloat(),
                    view.right.toFloat(),
                    view.bottom.toFloat()
                )
            } else if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX > 0) {
                x = min(dX, height * 2f)
                RectF(view.left + x, view.top.toFloat(), view.left.toFloat(), view.bottom.toFloat())
            } else null

            backgroundRect?.let { c.drawRect(it, backgroundColor) }

            super.onChildDraw(c, recyclerView, viewHolder, x, dY, actionState, isActive)
        }
    }
}