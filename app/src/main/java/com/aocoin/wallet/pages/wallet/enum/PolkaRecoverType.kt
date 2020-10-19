package com.aocoin.wallet.pages.wallet.enum

/**
 * @FileName: PolkaRecoverType
 * @Description: 波卡获取账户类型
 * @Author: haoyanhui
 * @Date: 2020-09-08 23:28
 */
enum class PolkaRecoverType(val value: String) {
    // 助记词
    MNEMONIC(value = "mnemonic"),
    // 私钥
    RAWSEED(value = "rawSeed"),
    // keystore
    KEYSTORE(value = "keystore")
}