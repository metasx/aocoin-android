package com.aocoin.wallet.utils

import java.util.regex.Pattern

/**
 * @FileName StringUtils
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 3:27 PM
 */

/**
 * 删除所有空格和回车
 *
 * @return
 */
fun String?.replaceBlank(): String {
    var dest = ""
    if (this != null) {
        val p = Pattern.compile("\\s*|\t|\r|\n")
        val m = p.matcher(this)
        dest = m.replaceAll("")
    }
    return dest
}