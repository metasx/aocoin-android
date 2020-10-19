package com.aocoin.wallet.pages.wallet

import android.os.Bundle
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseActivity
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.utils.Constants

/**
 * @FileName WalletAddActivity
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 6:11 PM
 */
class WalletAddActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_wallet_add)

        initRootFragment()
    }

    private fun initRootFragment() {
        val walletLocalType = intent.getIntExtra(Constants.PARAM_WALLET_LOCAL_TYPE, WalletLocalType.LOCAL_TYPE_POK)
        val targetFragment = WalletAddFragment.newInstance(walletLocalType = walletLocalType)
        if (findFragment(targetFragment.javaClass) == null) {
            loadRootFragment(R.id.frame_layout, targetFragment)
        }
    }
}