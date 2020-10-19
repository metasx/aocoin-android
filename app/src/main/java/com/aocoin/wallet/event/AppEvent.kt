package com.aocoin.wallet.event

/**
 * @FileName AppEvent
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 4:42 PM
 */
class AppEvent {
    enum class Type {

        // 钱包创建成功
        WALLET_CREATE_SUCCESS,

        // 钱包导入成功
        WALLET_IMPORT_SUCCESS,

        // 更新钱包管理 Icon
        UPDATE_WALLET_MANAGEMENT_ICON,

        // 当前钱包已切换
        CURRENT_WALLET_IS_SWITCHED,

        // 更新当前钱包信息
        UPDATE_CURRENT_WALLET_INFO,

        TRANSFER_SUCCESS
    }

    var type: Type
    var msg: String? = null

    constructor(type: Type) {
        this.type = type
    }

    constructor(type: Type, msg: String?) {
        this.type = type
        this.msg = msg
    }
}