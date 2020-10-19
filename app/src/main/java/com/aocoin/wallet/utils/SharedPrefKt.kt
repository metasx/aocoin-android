package com.aocoin.wallet.utils

import com.aocoin.wallet.utils.Constants

object SharedPrefKt {

    fun getCurrentWalletAddress(): String {
        return SharedPrefUtil.getStringData(Constants.SP_WALLET_ADDRESS_KEY)
    }

    fun getCurrentWalletLocalType(): Int {
        return SharedPrefUtil.getInt(Constants.SP_WALLET_LOCAL_TYPE_KEY)
    }

    /**
     * 保存当前钱包 address 和 LocalType
     */
    fun saveCurrentWalletAddressAndLocalType(address: String, localType: Int) {
        SharedPrefUtil.saveData(Constants.SP_WALLET_ADDRESS_KEY, address)
        SharedPrefUtil.saveData(Constants.SP_WALLET_LOCAL_TYPE_KEY, localType)
    }

}
