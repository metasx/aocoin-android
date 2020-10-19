package com.aocoin.wallet.utils

import com.aocoin.wallet.event.AppEvent
import org.greenrobot.eventbus.EventBus

/**
 * @FileName EventUtil
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 4:41 PM
 */
object EventUtil {

    /**
     * 钱包导入成功
     */
    fun walletImportSuccess() {
        EventBus.getDefault().post(AppEvent(AppEvent.Type.WALLET_IMPORT_SUCCESS))
    }

    /**
     * 钱包创建成功
     */
    fun walletCreateSuccess() {
        EventBus.getDefault().post(AppEvent(AppEvent.Type.WALLET_CREATE_SUCCESS))
    }

    /**
     * 更新当前钱包
     */
    fun updateCurrentWalletInfo() {
        EventBus.getDefault().post(AppEvent(AppEvent.Type.UPDATE_CURRENT_WALLET_INFO))
    }

    /**
     * 当前钱包已切换
     */
    fun currentWalletIsSwitched() {
        EventBus.getDefault().post(AppEvent(AppEvent.Type.CURRENT_WALLET_IS_SWITCHED))
    }

    /**
     * 更新钱包管理按钮显示状态
     */
    fun updateWalletManagementIcon(localType: Int) {
        EventBus.getDefault().post(AppEvent(AppEvent.Type.UPDATE_WALLET_MANAGEMENT_ICON, localType.toString()))
    }

    /**
     * 转账成功，结束中间页面
     */
    fun transferSuccess() {
        EventBus.getDefault().post(AppEvent(AppEvent.Type.TRANSFER_SUCCESS))
    }

}