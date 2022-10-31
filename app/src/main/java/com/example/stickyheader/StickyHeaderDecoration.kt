package com.example.stickyheader

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.RecyclerView.State
import com.example.stickyheader.databinding.ItemHeaderBinding
import com.example.stickyheader.databinding.ItemStickyHeaderBinding

class StickyHeaderDecoration(private val adapter: StickyHeaderCallBack, root: View) :
    ItemDecoration() {

    interface StickyHeaderCallBack {
        fun onBindHeaderData(binding: ItemStickyHeaderBinding, header: HeaderItemViewModel?)
        fun getHeaderForCurrentPosition(position: Int): HeaderItemViewModel?
        fun getHeaderForOldPosition(position: Int): HeaderItemViewModel?
        fun isHeader(itemPosition: Int): Boolean
    }

    private val headerBinding by lazy { ItemStickyHeaderBinding.inflate(LayoutInflater.from(root.context)) }

    private val headerView: View
        get() = headerBinding.root

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: State) {
        super.onDrawOver(canvas, parent, state)

        val topChild = parent.getChildAt(0)
        val secondChild = parent.getChildAt(1)

        parent.getChildAdapterPosition(topChild)
            .let { topChildPosition ->
                if (adapter.isHeader(topChildPosition)) {
                    adapter.getHeaderForCurrentPosition(topChildPosition).let { header ->
                        adapter.onBindHeaderData(headerBinding, header)
                        layoutHeaderView(topChild)
                        canvas.drawHeaderView(topChild, secondChild)
                    }
                }
                else {
                    adapter.getHeaderForOldPosition(topChildPosition - 1).let { header ->
                        adapter.onBindHeaderData(headerBinding, header)
                        layoutHeaderView(topChild)
                        canvas.drawHeaderView(topChild, secondChild)
                    }
                }
            }
    }

    private fun layoutHeaderView(topView: View?) {
        topView?.let {
            headerView.measure(
                MeasureSpec.makeMeasureSpec(topView.width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            )
            headerView.layout(topView.left, 0, topView.right, headerView.measuredHeight)
        }
    }

    private fun Canvas.drawHeaderView(topView: View?, secondChild: View?) {
        save()
        translate(0f, calculateHeaderTop(topView, secondChild))
        headerView.draw(this)
        restore()
    }

    private fun calculateHeaderTop(topView: View?, secondChild: View?): Float =
        secondChild?.let { secondView ->
            val threshold = getPixels(8, headerBinding.root.context) + headerView.bottom
            if (secondView.findViewById<View>(headerView.id)?.visibility != View.GONE && secondView.top <= threshold) {
                (secondView.top - threshold).toFloat()
            } else {
                maxOf(topView?.top ?: 0, 0).toFloat()
            }
        } ?: maxOf(topView?.top ?: 0, 0).toFloat()

    private fun getPixels(dipValue: Int, context: Context): Int {
        val r: Resources = context.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue.toFloat(),
            r.displayMetrics
        ).toInt()
    }
}
