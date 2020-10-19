package com.aocoin.wallet.utils

import androidx.fragment.app.FragmentManager
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.pages.common.dialog.MessageDialog
import com.aocoin.wallet.pages.wallet.WalletPasswordDialogFragment
import com.aocoin.wallet.pages.wallet.utils.getCurrentWallet
import com.aocoin.wallet.utils.Constants

/**
 * @FileName DialogUtil
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 3:18 PM
 */

/**
 * 显示带有'确认'和'取消'按钮的Dialog
 * @param title 标题
 * @param message 信息
 * @param onPositiveListener 确认按钮回调
 * @param onNegativeListener 取消按钮回调
 */
fun FragmentManager.showMessagePositiveDialog(title: String = "",
                                              message: String,
                                              negative: String? = null,
                                              positive: String? = null,
                                              onPositiveListener: () -> Unit,
                                              onNegativeListener: () -> Unit = {}) {

    MessageDialog(
            title = title,
            message = message,
            negative = negative,
            positive = positive,
            onPositiveListener = onPositiveListener,
            onNegativeListener = onNegativeListener
    ).show(this, Constants.TAG_MESSAGE_DIALOG)
}

/**
 * 显示钱包密码Dialog
 * @param wallet 要校验密码的钱包 默认当前钱包
 */
fun FragmentManager.showWalletPasswordDialog(
    wallet: Wallet? = getCurrentWallet(),
    onSuccessful: () -> Unit,
    onFailed: () -> Unit = {},
    onCancel: () -> Unit = {}) {
    WalletPasswordDialogFragment(
            wallet = wallet,
            onSuccessful = {
                onSuccessful.invoke()
            },

            onFailed = {
                onFailed.invoke()
            },

            onCancel = {
                onCancel.invoke()
            }).show(this, Constants.TAG_WALLET_PASSWORD_DIALOG)
}