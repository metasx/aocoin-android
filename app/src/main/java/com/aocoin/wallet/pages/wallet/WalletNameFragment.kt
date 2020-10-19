package com.aocoin.wallet.pages.wallet

import android.os.Bundle
import com.aocoin.wallet.utils.EventUtil
import com.aocoin.wallet.utils.showFailedTip
import com.aocoin.wallet.utils.showSuccessTip
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.Database
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.utils.Constants
import kotlinx.android.synthetic.main.fragment_wallet_name.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletNameFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/15 10:49 AM
 */

class WalletNameFragment : BaseFragment() {

    private var mWallet: Wallet? = null

    private val mName = {
        name_et?.text.toString().trim()
    }

    companion object {
        fun newInstance(wallet: Wallet): WalletNameFragment {
            val fragment = WalletNameFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.PARAM_WALLET, wallet)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getUi(): Int = R.layout.fragment_wallet_name

    override fun initData() {
        super.initData()

        initArgumentData()
    }

    private fun initArgumentData() {
        mWallet = arguments!!.getSerializable(Constants.PARAM_WALLET) as Wallet
    }

    private fun updateWalletName() {
        mWallet?.name = name_et?.text.toString()
        Database.updateMyWallet(mWallet)
    }

    override fun initView() {
        super.initView()

        initTopBar()
        initNameInput()
    }

    private fun initTopBar() {
        topbar?.setTopBar(title = getString(R.string.wallet_update_name), isBack = true)

        topbar?.addRightTextButton(
                getString(R.string.save),
                R.id.topbar_right_save_btn)
                ?.setOnClickListener {
                    if (!mName().isNullOrEmpty()) {
                        // 修改本地数据库
                        updateWalletName()

                        // 更新页面
                        EventUtil.updateCurrentWalletInfo()

                        // 提示成功
                        mContext.showSuccessTip(getString(R.string.save_success_notice)) {
                            pop()
                        }
                    } else {
                        mContext.showFailedTip(getString(R.string.wallet_name_empty_tip))
                    }
                }
    }

    private fun initNameInput() {
        name_et?.setText(mWallet?.name!!)
    }

}