package com.aocoin.wallet.service.walletacount

import com.android.aocoin.core.service.IWalletAccount
import com.aocoin.wallet.CoreApp
import com.aocoin.wallet.pages.wallet.enum.PolkaRecoverType
import com.aocoin.wallet.utils.Logger
import com.aocoin.wallet.pages.wallet.utils.getPolkaChainTypeByLocalType
import com.aocoin.wallet.api.JSApi
import com.aocoin.wallet.db.Coin
import com.aocoin.wallet.db.Database
import com.aocoin.wallet.utils.Utils.Companion.format
import com.aocoin.wallet.utils.doNextByLocalType
import com.aocoin.wallet.widgets.jsbridge.JsCallback
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * @FileName: PolkaWaletAcountImpl
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-13 10:34
 */
class PolkaWalletAccountImpl(private val localType: Int) : IWalletAccount {
    override fun generateMnemonic(
        onSuccess: (mnemonic: String) -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        JSApi.getData(CoreApp.jsWebView, JSApi.POLKA_GENERATE_MNEMONIC, JsCallback { data ->
            Logger.d("POLKA_GENERATE_MNEMONIC return $data")
            val resultObj = JSONObject(data.toString())
            if (resultObj.optBoolean("success")) {
                onSuccess(resultObj.optString("body"))
            } else {
                onFail(resultObj.optString("errorMsg"))
            }
        })
    }

    override fun checkMnemonic(
        mnemonic: String, onSuccess: (isValid: Boolean, msg: String?) -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        val param = JSONObject()
        param.put("seed", mnemonic)
        Logger.d("POLKA_VALIDATE_MNEMONIC params $param")
        JSApi.getData(
            CoreApp.jsWebView,
            JSApi.POLKA_VALIDATE_MNEMONIC,
            param.toString(),
            JsCallback { data ->
                Logger.d("POLKA_VALIDATE_MNEMONIC return $data")
                val resultObj = JSONObject(data.toString())
                if (resultObj.optBoolean("success")) {
                    onSuccess(true, "")
                } else {
                    onFail(resultObj.optString("errorMsg"))
                }
            })
    }

    override fun createWalletByMnemonic(
        mnemonic: String,
        password: String,
        onSuccess: (address: String, privateKey: String, keystore: String) -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        recoverPolkaAccount(keyType = PolkaRecoverType.MNEMONIC.value, key = mnemonic,
            password = password,
            onSuccess = { privateKey: String, address: String, keystore: String ->
                onSuccess(address, privateKey, keystore)
            },
            onFail = {
                onFail(it ?: "")
            })
    }

    override fun createWalletByKeyStore(
        keystore: String, password: String,
        onSuccess: (address: String, privateKey: String, keystore: String) -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        recoverPolkaAccount(keyType = PolkaRecoverType.KEYSTORE.value,
            key = keystore,
            password = password,
            onSuccess = { privateKey: String, address: String, keystoreR: String ->
                onSuccess(address, privateKey, keystoreR)
            },
            onFail = {
                onFail(it ?: "")
            })
    }

    override fun createWalletByPrivateKey(
        privateKey: String,
        password: String,
        onSuccess: (address: String, keystore: String) -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        recoverPolkaAccount(keyType = PolkaRecoverType.RAWSEED.value, key = privateKey,
            password = password,
            onSuccess = { _: String, address: String, keystore: String ->
                onSuccess(address, keystore)
            },
            onFail = {
                onFail(it ?: "")
            })
    }

    override fun checkAddress(
        address: String, onSuccess: (isValid: Boolean, msg: String?) -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        val param = JSONObject()
        param.put("address", address)
        param.put("ss58Format", localType.getPolkaChainTypeByLocalType()?.ss58Format ?: -1)
        Logger.d("POLKA_CHECK_ADDRESS params $param")
        JSApi.getData(
            CoreApp.jsWebView,
            JSApi.POLKA_CHECK_ADDRESS,
            param.toString(),
            JsCallback { data ->
                Logger.d("POLKA_CHECK_ADDRESS return = $data")
                val resultObj = JSONObject(data.toString())
                if (resultObj.optBoolean("success")) {
                    val bodyObj = resultObj.optJSONObject("body")
                    onSuccess(bodyObj.optBoolean("isValid"), bodyObj.optString("message"))
                } else {
                    onFail(resultObj.optString("errorMsg"))
                }
            })
    }

    override fun reloadKeyStore(
        keystore: String,
        privateKey: String,
        oldPassword: String,
        newPassword: String,
        onSuccess: (keystore: String) -> Unit,
        onFail: (errorMsg: String) -> Unit
    ) {
        val param = JSONObject()
        param.put("keystore", keystore)
        param.put("passOld", oldPassword)
        param.put("passNew", newPassword)
        Logger.d("POLKA_CHANGE_PASSWORD params = $param")
        JSApi.getData(
            CoreApp.jsWebView,
            JSApi.POLKA_CHANGE_PASSWORD,
            param.toString(),
            JsCallback { data ->
                Logger.d("POLKA_CHANGE_PASSWORD return = $data")
                val resultObj = JSONObject(data.toString())
                if (resultObj.optBoolean("success")) {
                    onSuccess(resultObj.optJSONObject("body").optString("keystore"))
                } else {
                    onFail(resultObj.optString("errorMsg"))
                }
            })
    }

    override fun loadAssetsInfo(
        address: String,
        onSuccess: () -> Unit,
        onFail: (msg: String) -> Unit
    ) {
        polkaConnectNode(onSuccess = {
            val balanceParam = JSONObject()
            balanceParam.put("address", address)
            Logger.d("POLKA_GET_BALANCE params $balanceParam")
            JSApi.getData(
                CoreApp.jsWebView,
                JSApi.POLKA_GET_BALANCE,
                balanceParam.toString(),
                JsCallback { data ->
                    Logger.d("POLKA_GET_BALANCE return $data")
                    val balanceResult = JSONObject(data.toString())
                    if (balanceResult.optBoolean("success")) {
                        val bodyObj = balanceResult.optJSONObject("body")
                        val coin = Coin()
                        // 区分波卡公链，不同别名和精度
                        localType.doNextByLocalType(pok = {
                            coin.abbr = "DOT"
                            coin.precision = 10
                        }, ksm = {
                            coin.abbr = "KSM"
                            coin.precision = 12
                        }, klp = {
                            coin.abbr = "KLP"
                            coin.precision = 12
                        })
                        val balance = bodyObj?.optString("freeBalance") ?: "0"
                        coin.balance = format(
                            4, BigDecimal(balance)
                                .divide(
                                    BigDecimal.TEN.pow(coin.precision),
                                    coin.precision, RoundingMode.DOWN
                                ).toString()
                        ) ?: "0"
                        coin.chainType = localType
                        coin.address = address
                        // first del, then save
                        Database.delCoinByAddress(address, localType)
                        coin.save()
                        onSuccess()
                    } else {
                        onFail(balanceResult.optString("errorMsg"))
                    }
                })
        }, onFail = {
            onFail(it)
        })
    }

    override fun loadFeeInfo(
        address: String, amount: String, toAddress: String, coinPrecision: Int,
        onSuccess: (fee: String, existentialDeposit: String) -> Unit, onFail: (msg: String) -> Unit
    ) {
        polkaConnectNode(onSuccess = {
            polkaLoadTransferFee(address = address, toAddress = toAddress, amount = amount,
                onSuccess = { feeStr, existentialDeposit ->
                    // 根据精度计算
                    val feeInfo = format(
                        6, BigDecimal(feeStr)
                            .divide(BigDecimal.TEN.pow(coinPrecision), 10, RoundingMode.DOWN)
                            .toString()
                    )
                    val deposit = format(
                        6, BigDecimal(existentialDeposit)
                            .divide(BigDecimal.TEN.pow(coinPrecision), 10, RoundingMode.DOWN)
                            .toString()
                    )
                    onSuccess(feeInfo ?: "", deposit ?: "")
                },
                onFail = {
                    onFail(it)
                })
        }, onFail = {
            onFail(it)
        })
    }

    private fun recoverPolkaAccount(
        keyType: String, key: String, password: String,
        onSuccess: (privateKey: String, address: String, keystore: String) -> Unit,
        onFail: (String?) -> Unit
    ) {
        val paramObj = JSONObject()
        paramObj.put("keyType", keyType)
        paramObj.put("key", key)
        paramObj.put("password", password)
        Logger.d("POLKA_RECOVER_ACCOUNT params $paramObj")
        JSApi.getData(
            CoreApp.jsWebView,
            JSApi.POLKA_RECOVER_ACCOUNT,
            paramObj.toString(),
            JsCallback { data ->
                Logger.d("POLKA_RECOVER_ACCOUNT return $data")
                val resultObj = JSONObject(data.toString())
                if (resultObj.optBoolean("success")) {
                    val dataObj = JSONObject(data.toString())
                    // 需要根据实际选择进行地址转换
                    val bodyObj = dataObj.optJSONObject("body")
                    val tempAddress = bodyObj.optString("address")
                    polkaAddressChange(address = tempAddress,
                        onSuccess = { address ->
                            val keystore = bodyObj.optString("keystore")
                            // hyh 获取私钥
                            val privateKey = bodyObj.optString("rawSeed")
                            onSuccess(privateKey, address, keystore)
                        },
                        onFail = {
                            onFail(it)
                        })
                } else {
                    onFail(resultObj.optString("errorMsg"))
                }
            })
    }

    private fun polkaAddressChange(
        address: String,
        onSuccess: (String) -> Unit,
        onFail: (String) -> Unit
    ) {
        val param = JSONObject()
        param.put("address", address)
        param.put("ss58Format", localType.getPolkaChainTypeByLocalType()?.ss58Format ?: -1)
        Logger.d("POLKA_TO_ADDRESS params $param")
        JSApi.getData(
            CoreApp.jsWebView,
            JSApi.POLKA_TO_ADDRESS,
            param.toString(),
            JsCallback { data ->
                Logger.d("POLKA_TO_ADDRESS return $data")
                val resultObj = JSONObject(data.toString())
                if (resultObj.optBoolean("success")) {
                    onSuccess(resultObj.optString("body"))
                } else {
                    onFail(resultObj.optString("errorMsg"))
                }
            })
    }

    private fun polkaConnectNode(
        onSuccess: () -> Unit = {},
        onFail: (errMsg: String) -> Unit = {}
    ) {
        val connectParam = JSONObject()
        val chainType = localType.getPolkaChainTypeByLocalType()
        chainType?.let { chain ->
            connectParam.put("endpoint", chain.endpoint)
            Logger.d("POLKA_CONNECT_NODE params $connectParam")
            JSApi.getData(
                CoreApp.jsWebView,
                JSApi.POLKA_CONNECT_NODE,
                connectParam.toString(),
                JsCallback { data ->
                    Logger.d("POLKA_CONNECT_NODE return $data")
                    val connectResult = JSONObject(data.toString())
                    if (connectResult.optBoolean("success")) {
                        onSuccess()
                    } else {
                        onFail(connectResult.optString("errorMsg"))
                    }
                })
        }
    }

    private fun polkaLoadTransferFee(
        address: String, toAddress: String, amount: String,
        onSuccess: (feeStr: String, existentialDeposit: String) -> Unit,
        onFail: (errMsg: String) -> Unit
    ) {
        val param = JSONObject()
        val txInfo = JSONObject()
        txInfo.put("module", "balances")
        txInfo.put("call", "transfer")
        txInfo.put("address", address)
        txInfo.put("proxy", "")
        val paramList = JSONArray()
        // 无接收地址，放入自己地址
        paramList.put(if (toAddress.isEmpty()) address else toAddress)
        paramList.put(amount)
        param.put("txInfo", txInfo)
        param.put("paramList", paramList)
        Logger.d("POLKA_TX_FEE_ESTIMATE params = $param")
        JSApi.getData(
            CoreApp.jsWebView,
            JSApi.POLKA_TX_FEE_ESTIMATE,
            param.toString(),
            JsCallback { data ->
                Logger.d("POLKA_TX_FEE_ESTIMATE return = $data")
                val resultObj = JSONObject(data.toString())
                if (resultObj.optBoolean("success")) {
                    val bodyObj = resultObj.optJSONObject("body")
                    val feeStr = bodyObj.optJSONObject("dispatchInfo").optString("partialFee")
                    val existentialDeposit = bodyObj.optString("existentialDeposit")

                    onSuccess(feeStr, existentialDeposit)
                } else {
                    onFail(resultObj.optString("errorMsg"))
                }
            })
    }
}