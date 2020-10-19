package com.aocoin.wallet.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.aocoin.wallet.R
import com.aocoin.wallet.db.WalletLocalType
import java.math.BigDecimal

/**
 * @FileName: CommonUtil
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-12 14:57
 */

/**
 * 文本复制
 */
fun Context.copy(text: String?) {
    val clipboard = this.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)

    this.showSuccessTip(content = this.getString(R.string.copy_to_clipboard))
}

/**
 * 打开应用设置
 */
fun Context.startSelfSetting() {
    val intent = Intent()
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS";
    intent.data = Uri.fromParts("package", this.packageName, null);
    this.startActivity(intent)
}

fun Context.checkFastClick(complete: () -> Unit) {
    if (!Utils.isFastDoubleClick()) {
        complete.invoke()
    }
}

/*
    根据钱包中 localType 的值执行不同的逻辑，入参为各个公链对应执行逻辑的方法
 */
fun Int.doNextByLocalType(pok: () -> Unit = {}, ksm: () -> Unit = {}, klp: () -> Unit = {}) {
    when (this) {
        WalletLocalType.LOCAL_TYPE_POK -> {
            pok()
        }
        WalletLocalType.LOCAL_TYPE_KSM -> {
            ksm()
        }
        WalletLocalType.LOCAL_TYPE_KLP -> {
            klp()
        }
    }
}

fun String.isAmountLegal(context: Context, emptyNoticeResId: Int = 0, lessZeroNoticeResId: String = "",
                         showNotice: Boolean = true): Boolean {
    if (this.isEmpty()) {
        if (showNotice) {
            context.showFailedTip(context.getString(if (emptyNoticeResId == 0) R.string.input_amount else emptyNoticeResId))
        }
        return false
    } else if (this.startsWith(".") || this.endsWith(".")) {
        if (showNotice) {
            context.showFailedTip(context.getString(R.string.input_format_error_msg))
        }
        return false
    } else if (BigDecimal(this) <= BigDecimal.ZERO) {
        if (showNotice) {
            context.showFailedTip(if (lessZeroNoticeResId.isEmpty())
                context.getString(R.string.transfer_amount_need_over_zero)
            else lessZeroNoticeResId)
        }
        return false
    }
    return true
}

fun String.isAddressNotEmpty(context: Context): Boolean {
    if (this.isEmpty()) {
        context.showFailedTip(context.getString(R.string.transfer_tab3_hints))
        return false
    }
    return true
}
