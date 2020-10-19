package com.aocoin.wallet.utils

import android.content.Context
import com.aocoin.wallet.R
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog


/**
 * @FileName TipDialogUtil
 * @Description
 * @Author dingyan
 * @Date 2019-12-27 17:01
 */

/**
 * 默认显示时间
 */
private const val SHOW_TIME = 1000L

/**
 * 简单文字提示
 *
 * @param content 显示内容
 */
fun Context.showTextTip(content: String) {
    showTip(QMUITipDialog.Builder.ICON_TYPE_NOTHING, content)
}

/**
 * 成功类新提示
 *
 * @param content 显示内容
 */
fun Context.showSuccessTip(content: String) {
    showTip(QMUITipDialog.Builder.ICON_TYPE_SUCCESS, content)
}

/**
 * 失败文字提示
 *
 * @param content 显示内容
 */
fun Context.showFailedTip(content: String) {
    showTip(QMUITipDialog.Builder.ICON_TYPE_FAIL, content)
}

/**
 * 提醒文字提示
 *
 * @param content 显示内容
 */
fun Context.showInfoTip(content: String) {
    showTip(QMUITipDialog.Builder.ICON_TYPE_INFO, content)
}

/**
 * 成功文字提示,带消息监听
 *
 * @param content 显示内容
 */
fun Context.showSuccessTip(content: String, onDismissListener: () -> Unit) {
    showTip(iconType = QMUITipDialog.Builder.ICON_TYPE_SUCCESS,
            content = content,
            onDismissListener = onDismissListener)
}

/**
 * 失败文字提示,带消息监听
 *
 * @param content 显示内容
 */
fun Context.showFailedTip(content: String, onDismissListener: () -> Unit) {
    showTip(iconType = QMUITipDialog.Builder.ICON_TYPE_FAIL,
            content = content,
            onDismissListener = onDismissListener)
}

/**
 * 显示提示
 * 目前不用IconType
 *
 * @param iconType 显示图标类型
 * @param content 显示内容
 */
fun Context.showTip(iconType: Int, content: String, time: Long = SHOW_TIME, onDismissListener: () -> Unit = {}) {

    val tipDialog = QMUITipDialog.Builder(this)
            .setTipWord(content)
            .setIconType(QMUITipDialog.Builder.ICON_TYPE_NOTHING)
            .create()
    tipDialog.show()

    Thread {
        try {
            Thread.sleep(time)
            tipDialog.dismiss()
            onDismissListener.invoke()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }.start()
}

/**
 * 获取Loading提示
 *
 * @return Dialog
 */
fun Context.getLoadingTip(): QMUITipDialog {
    return getCustomLoadingTip()
}

/*
   自定义动画的加载框
 */
fun Context.getCustomLoadingTip(): QMUITipDialog {
    return QMUITipDialog.CustomBuilder(this).setContent(R.layout.dialog_custom_loading).create()
}