package com.aocoin.wallet.api

import com.aocoin.wallet.widgets.jsbridge.JsBridgeWebView
import com.aocoin.wallet.widgets.jsbridge.JsCallback


/**
 * @FileName: JSApi
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-12 12:01
 */
object JSApi {
    const val WEB_URL = "file:///android_asset/web/index.html"

    fun getData(jsBridgeWebView: JsBridgeWebView, method: String, callback: JsCallback<Any>) {
        jsBridgeWebView.callHandler(method, "", callback)
    }

    fun getData(jsBridgeWebView: JsBridgeWebView, method: String, params: String, callback: JsCallback<Any>) {
        jsBridgeWebView.callHandler(method, params, callback)
    }


    // polkadot
    const val POLKA_GENERATE_MNEMONIC = "gm"
    const val POLKA_VALIDATE_MNEMONIC = "mv"
    const val POLKA_TO_ADDRESS = "tadr"
    const val POLKA_CHECK_ADDRESS = "ckadr"
    const val POLKA_RECOVER_ACCOUNT = "rc"
    const val POLKA_CONNECT_NODE = "cond"
    const val POLKA_GET_BALANCE = "gblc"
    const val POLKA_CHANGE_PASSWORD = "cpas"
    const val POLKA_TX_FEE_ESTIMATE = "tfe"
    const val POLKA_IS_ACTIVATE_ADDRESS = "iaa"
    const val POLKA_SEND_TX = "stx"
    const val POLKA_GET_BLOCK_BY_HASH = "gbbh"
    const val POLKA_GET_LAST_BLOCK = "glb"

}