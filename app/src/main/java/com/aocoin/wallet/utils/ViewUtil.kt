package com.aocoin.wallet.utils

import android.view.View
import com.noober.background.drawable.DrawableCreator
import com.qmuiteam.qmui.util.QMUIDisplayHelper

/**
 * @FileName ViewUtil
 * @Description
 * @Author dingyan
 * @Date 2020/10/13 4:51 PM
 */

/**
 * 更新背景
 */
fun View.updateBackground(radius: Int, solidColor: Int) {
    val drawable = DrawableCreator.Builder()
            .setCornersRadius(QMUIDisplayHelper.dpToPx(radius).toFloat())
            .setSolidColor(solidColor)
            .build()
    this.background = drawable
}
