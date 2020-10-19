package com.aocoin.wallet.service.walletacount

import com.android.aocoin.core.service.IWalletAccount
import com.aocoin.wallet.db.WalletLocalType

/**
 * @FileName: WalletAccountManager
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-13 10:38
 */
class WalletAccountManager(walletLocalType: Int) {

    private var walletAccountImpl: IWalletAccount? = null

    init {
        walletAccountImpl = when (walletLocalType) {
            WalletLocalType.LOCAL_TYPE_POK -> {
                PolkaWalletAccountImpl(walletLocalType)
            }
            WalletLocalType.LOCAL_TYPE_KSM -> {
                PolkaWalletAccountImpl(walletLocalType)
            }
            WalletLocalType.LOCAL_TYPE_KLP -> {
                PolkaWalletAccountImpl(walletLocalType)
            }
            else -> {
                null
            }
        }
    }

    fun generateMnemonic(onSuccess: (mnemonic: String) -> Unit, onFail: (msg: String) -> Unit) {
        walletAccountImpl?.generateMnemonic(onSuccess, onFail)
    }

    fun checkMnemonic(mnemonic: String, onSuccess: (isValid: Boolean, msg: String?) -> Unit,
                      onFail: (msg: String) -> Unit) {
        walletAccountImpl?.checkMnemonic(mnemonic, onSuccess, onFail)
    }

    fun createWalletByMnemonic(mnemonic: String,
                               password: String,
                               onSuccess: (address: String, privateKey: String, keystore: String) -> Unit,
                               onFail: (msg: String) -> Unit) {
        walletAccountImpl?.createWalletByMnemonic(mnemonic, password, onSuccess, onFail)
    }

    fun createWalletByKeyStore(keystore: String,
                               password: String,
                               onSuccess: (address: String, privateKey: String, keystore: String) -> Unit,
                               onFail: (msg: String) -> Unit) {
        walletAccountImpl?.createWalletByKeyStore(keystore, password, onSuccess, onFail)
    }

    fun createWalletByPrivateKey(privateKey: String,
                                 password: String,
                                 onSuccess: (address: String, keystore: String) -> Unit,
                                 onFail: (msg: String) -> Unit) {
        walletAccountImpl?.createWalletByPrivateKey(privateKey, password, onSuccess, onFail)
    }

    fun checkAddress(address: String, onSuccess: (isValid: Boolean, msg: String?) -> Unit,
                     onFail: (msg: String) -> Unit) {
        walletAccountImpl?.checkAddress(address, onSuccess, onFail)
    }

    fun reloadKeyStore(keystore: String, privateKey: String, oldPassword: String, newPassword: String,
                       onSuccess: (keystore: String) -> Unit, onFail: (errorMsg: String) -> Unit) {
        walletAccountImpl?.reloadKeyStore(keystore, privateKey, oldPassword, newPassword, onSuccess, onFail)
    }

    fun loadAssets(address: String, onSuccess: () -> Unit, onFail: (msg: String) -> Unit) {
        walletAccountImpl?.loadAssetsInfo(address, onSuccess, onFail)
    }

    fun loadFeeInfo(address: String, amount: String, toAddress: String, coinPrecision: Int,
                    onSuccess: (fee: String, existentialDeposit: String) -> Unit,
                    onFail: (msg: String) -> Unit) {
        walletAccountImpl?.loadFeeInfo(address, amount, toAddress, coinPrecision, onSuccess, onFail)
    }
}