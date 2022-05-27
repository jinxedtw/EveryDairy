package com.tw.longerrelationship.help

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView



class GridItemDecoration(private val count: Int,private val colSpace: Int, private val rowSpace: Int = -1, ) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position: Int = parent.getChildAdapterPosition(view)
        val column: Int = position % count

        outRect.left = column * colSpace / count
        outRect.right = colSpace - (column + 1) * colSpace / count // 列间距 - (column + 1) * (列间距 * (1f /列数))

        // 如果position > 行数，说明不是在第一行，则不指定行高，其他行的上间距为 top=mRowSpacing
        if (position >= count) {
            outRect.top = if (rowSpace != -1) rowSpace else colSpace // item top
        }
    }
}