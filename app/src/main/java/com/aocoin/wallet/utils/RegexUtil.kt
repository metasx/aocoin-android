package com.aocoin.wallet.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @FileName RegexUtil
 * @Description
 * @Author dingyan
 * @Date 2020/10/13 4:50 PM
 */

/**
 * 是否有大写字母
 */
fun String.checkUppercase(): Boolean {
    val regex = ".*[A-Z]+.*".toRegex()
    return this.matches(regex)

}

/**
 * 是否有小写字母
 */
fun String.checkLowercase(): Boolean {
    val regex = ".*[a-z]+.*".toRegex()
    return this.matches(regex)
}

/**
 * 是否有数字
 */
fun String.checkNumber(): Boolean {
    val regex = ".*[0-9]+.*".toRegex()
    return this.matches(regex)
}

/**
 * 密码校验
 */
fun String.checkInputPassword(): Boolean {
    val pat: Pattern = Pattern.compile("^(?:(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])).{8,30}$")
    val m: Matcher = pat.matcher(this.trim { it <= ' ' })
    return m.find()
}