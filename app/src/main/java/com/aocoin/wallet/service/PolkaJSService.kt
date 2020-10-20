package com.aocoin.wallet.service

import com.aocoin.wallet.CoreApp
import com.aocoin.wallet.api.JSApi
import com.aocoin.wallet.pages.wallet.utils.getCurrentWallet
import com.aocoin.wallet.utils.Logger
import com.aocoin.wallet.widgets.jsbridge.JsCallback
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal

/**
 * @FileName: PolkaJSservice
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-19 12:02
 */
fun polkaIsActivateAddress(
    toAddress: String,
    onSuccess: (isActivate: Boolean) -> Unit,
    onFail: (errMsg: String) -> Unit
) {
    val param = JSONObject()
    param.put("address", toAddress)
    Logger.d("POLKA_IS_ACTIVATE_ADDRESS 入参 $param")
    JSApi.getData(CoreApp.jsWebView, JSApi.POLKA_IS_ACTIVATE_ADDRESS, param.toString(),
        JsCallback { data ->
            Logger.d("POLKA_IS_ACTIVATE_ADDRESS = $data")
            val resultObj = JSONObject(data.toString())
            if (resultObj.optBoolean("success")) {
                onSuccess(resultObj.optJSONObject("body").optBoolean("isActivate"))
            } else {
                onFail(resultObj.optString("errorMsg"))
            }
        })
}

/*
    转账，包含构建交易、签名交易、广播
 */
fun polkaSendTransfer(
    address: String, toAddress: String, amount: String, coinPrecision: Int,
    onSuccess: (resultBody: JSONObject) -> Unit, onFail: (errMsg: String) -> Unit
) {
    val param = JSONObject()
    val txInfo = JSONObject()
    txInfo.put("module", "balances")
    txInfo.put("call", "transfer")
    txInfo.put("address", address)
    txInfo.put("proxy", "")
    val wallet = getCurrentWallet()
    txInfo.put("password", wallet.password)
    txInfo.put("pk", wallet.keystore)
    txInfo.put("ss58", "")
    txInfo.put("tip", "")
    val paramList = JSONArray()
    paramList.put(toAddress)
    val amountStr =
        BigDecimal(amount).multiply(BigDecimal.TEN.pow(coinPrecision)).toBigInteger().toString()
    Logger.d("amount = $amountStr")
    paramList.put(amountStr)
    param.put("txInfo", txInfo)
    param.put("paramList", paramList)
    Logger.d("POLKA_SEND_TX params = $param")
    JSApi.getData(CoreApp.jsWebView, JSApi.POLKA_SEND_TX, param.toString(), JsCallback { data ->
        Logger.d("POLKA_SEND_TX return = $data")
        val resultObj = JSONObject(data.toString())
        if (resultObj.optBoolean("success")) {
            onSuccess(resultObj.optJSONObject("body"))
        } else {
            onFail(resultObj.optString("errorMsg"))
        }
    })
}


fun polkaLoadBlockByHash(
    blockHash: String, onSuccess: (blockNum: String) -> Unit,
    onFail: (errMsg: String) -> Unit
) {
    val param = JSONObject()
    param.put("blockHash", blockHash)
    Logger.d("POLKA_GET_BLOCK_BY_HASH 入参 $param")
    JSApi.getData(
        CoreApp.jsWebView,
        JSApi.POLKA_GET_BLOCK_BY_HASH,
        param.toString(),
        JsCallback { data ->
            Logger.d("POLKA_GET_BLOCK_BY_HASH 返回 $data")
            val resultObj = JSONObject(data.toString())
            if (resultObj.optBoolean("success")) {
                onSuccess(resultObj.optString("body"))
            } else {
                onFail(resultObj.optString("errorMsg"))
            }
        })
}


fun polkaLoadLastBlock(onSuccess: (block: String) -> Unit, onFail: (errMsg: String) -> Unit) {
    Logger.d("POLKA_GET_LAST_BLOCK 开始")
    JSApi.getData(CoreApp.jsWebView, JSApi.POLKA_GET_LAST_BLOCK, JsCallback { data ->
        Logger.d("POLKA_GET_LAST_BLOCK 返回 $data")
        val resultObj = JSONObject(data.toString())
        if (resultObj.optBoolean("success")) {
            onSuccess(resultObj.optString("body"))
        } else {
            onFail(resultObj.optString("errorMsg"))
        }
    })
}