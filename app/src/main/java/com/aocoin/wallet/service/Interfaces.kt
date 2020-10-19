package com.android.aocoin.core.service

/**
 * @FileName: Interfaces
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-13 09:56
 */
interface IWalletAccount {

    fun generateMnemonic(onSuccess: (mnemonic: String) -> Unit, onFail: (msg: String) -> Unit)

    fun checkMnemonic(mnemonic: String, onSuccess: (isValid: Boolean, msg: String?) -> Unit,
                      onFail: (msg: String) -> Unit)

    fun createWalletByMnemonic(mnemonic: String,
                               password: String,
                               onSuccess: (address: String, privateKey: String, keystore: String) -> Unit,
                               onFail: (msg: String) -> Unit)

    fun createWalletByKeyStore(keystore: String,
                               password: String,
                               onSuccess: (address: String, privateKey: String, keystore: String) -> Unit,
                               onFail: (msg: String) -> Unit)

    fun createWalletByPrivateKey(privateKey: String, password: String, onSuccess: (address: String, keystore: String) -> Unit,
                                 onFail: (msg: String) -> Unit)

    fun checkAddress(address: String, onSuccess: (isValid: Boolean, msg: String?) -> Unit,
                     onFail: (msg: String) -> Unit)

    fun reloadKeyStore(keystore: String, privateKey: String, oldPassword: String, newPassword: String,
                       onSuccess: (keystore: String) -> Unit, onFail: (errorMsg: String) -> Unit)

    fun loadAssetsInfo(address: String, onSuccess: () -> Unit, onFail: (msg: String) -> Unit)

    fun loadFeeInfo(address: String, amount: String, toAddress: String, coinPrecision: Int,
                    onSuccess: (fee: String, existentialDeposit: String) -> Unit,
                    onFail: (msg: String) -> Unit)
}