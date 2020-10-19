package com.aocoin.wallet.widgets

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @FileName GridSpaceItemDecoration
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 1:31 PM
 */

class GridSpaceItemDecoration(
        private val spanCount: Int,
        private val horizontalSpace: Int,
        private val verticalSpace: Int,
        private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount
        if (includeEdge) {
            outRect.left = horizontalSpace - column * horizontalSpace / spanCount
            outRect.right = (column + 1) * horizontalSpace / spanCount
            if (position < spanCount) {
                outRect.top = verticalSpace
            }
            outRect.bottom = verticalSpace
        } else {
            outRect.left = column * horizontalSpace / spanCount
            outRect.right = horizontalSpace - (column + 1) * horizontalSpace / spanCount
            if (position >= spanCount) {
                outRect.top = verticalSpace
            }
        }
    }
}