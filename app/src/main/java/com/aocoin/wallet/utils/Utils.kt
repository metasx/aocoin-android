package com.aocoin.wallet.utils

import android.text.TextUtils
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @FileName: Utils
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-12 14:58
 */
class Utils {
    companion object {
        private var lastClickTime: Long = 0

        /**
         * 快速点击校验
         */
        fun isFastDoubleClick(): Boolean {
            val time = System.currentTimeMillis()
            val timeD = time - lastClickTime
            if (timeD < 500) {
                return true
            }
            lastClickTime = time
            return false
        }

        fun format(digits: Int, value: String?): String? {
            if (TextUtils.isEmpty(value)) {
                return value
            }
            val df = DecimalFormat()
            df.maximumFractionDigits = digits
            df.groupingSize = 0
            df.roundingMode = RoundingMode.DOWN
            return df.format(BigDecimal(value))
        }

        fun stampToDate(s: String): String? {
            val res: String
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            var lt: Long = s.toLong()
            val date = Date(lt)
            res = simpleDateFormat.format(date)
            return res
        }
    }
}