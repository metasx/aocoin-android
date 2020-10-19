package com.aocoin.wallet.pages.wallet.utils

import android.content.Context
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.pages.wallet.enum.PolkadotChainInfo
import com.aocoin.wallet.pages.wallet.enum.WalletPageName
import com.aocoin.wallet.pages.wallet.type.WalletMnemonicItem
import com.aocoin.wallet.utils.SharedPrefKt
import com.aocoin.wallet.utils.showFailedTip
import com.aocoin.wallet.R
import com.aocoin.wallet.db.Database
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.db.Wallet_Table
import com.raizlabs.android.dbflow.sql.language.Select
import java.util.*

/**
 * @FileName: WalletUtil
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-12 17:43
 */

fun WalletPageName.check(
        isSwitch: () -> Unit = {},
        isManagement: () -> Unit = {}) {
    if (this == WalletPageName.Switch) {
        isSwitch.invoke()
    } else {
        isManagement.invoke()
    }
}

/**
 * 获取当前钱包
 */
fun getCurrentWallet(): Wallet {
    val address = SharedPrefKt.getCurrentWalletAddress()
    val localType = SharedPrefKt.getCurrentWalletLocalType()
    return Select().from(Wallet::class.java)
            .where(Wallet_Table.address.`is`(address), Wallet_Table.localType.`is`(localType))
            .querySingle()!!
}

fun Int.getPolkaChainTypeByLocalType(): PolkadotChainInfo? {
    return when (this) {
        WalletLocalType.LOCAL_TYPE_POK -> PolkadotChainInfo.POLKADOT
        WalletLocalType.LOCAL_TYPE_KSM -> PolkadotChainInfo.KUSAMA
        WalletLocalType.LOCAL_TYPE_KLP -> PolkadotChainInfo.KULUPU
        else -> null
    }
}

/**
 * 通过私钥判断钱包是否已经存在
 */
fun Context.checkWalletExistsByPrivateKey(privateKey: String,
                                          localType: Int,
                                          onSuccess: () -> Unit,
                                          onFailed: (String) -> Unit) {
    val wallet: Wallet? = Database.findWalletByPrivateKeyAndLocalType(privateKey, localType)

    if (!wallet?.address.isNullOrEmpty()) {
        onFailed.invoke(getString(R.string.wallet_has_exist_tip))
    } else {
        onSuccess.invoke()
    }
}

/**
 * 获取助记词列表
 */
fun String.getMnemonics(): List<WalletMnemonicItem> {
    val words = this.split(" ")

    val itemWallets: ArrayList<WalletMnemonicItem> = arrayListOf()

    for (i in words.indices) {
        itemWallets.add(WalletMnemonicItem(id = i, word = words[i]))
    }

    return itemWallets
}

/**
 * 获取打乱顺序后的助记词列表
 */
fun String.getRandomMnemonics(): List<WalletMnemonicItem> {
    val list = this.getMnemonics()
    Collections.shuffle(list)
    return list
}

/**
 * 将助记词列表转换成字符串
 */
fun List<WalletMnemonicItem>.toMnemonic(): String {
    var mnemonic = ""
    this.forEach { item ->
        mnemonic += "${item.word} "
    }
    return mnemonic.trimEnd()
}

/**
 * 将钱包地址转换成前6位，后6位数的形式
 */
fun String.formatAddress(): String {
    if (this.length < 6) {
        return this
    }
    val start = this.substring(0, 6)
    val end = this.substring(this.length - 3)
    return "$start...$end"
}

/**
 * 检查密码是否正确
 */
fun String.checkPassword(context: Context,
                         wallet: Wallet? = getCurrentWallet(),
                         showMessage: Boolean = true): Boolean {
    if (wallet?.password == this) {
        return true
    } else {
        if (showMessage) {
            context.showFailedTip(context.getString(R.string.wallet_password_input_wrong_tip))
        }
    }
    return false
}