package com.aocoin.wallet.pages.wallet

import android.os.Bundle
import com.aocoin.wallet.utils.PermissionUtil
import com.aocoin.wallet.utils.copy
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.utils.Constants
import kotlinx.android.synthetic.main.fragment_wallet_create_by_private_key.*
import kotlinx.android.synthetic.main.include_copy_button_layout.*
import kotlinx.android.synthetic.main.include_secondary_button.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletPrivateKeyBackupFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/15 11:31 AM
 */
class WalletPrivateKeyBackupFragment : BaseFragment() {

    private var mWallet: Wallet? = null

    companion object {
        fun newInstance(wallet: Wallet): WalletPrivateKeyBackupFragment {
            val fragment = WalletPrivateKeyBackupFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.PARAM_WALLET, wallet)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getUi(): Int = R.layout.fragment_wallet_create_by_private_key

    override fun initData() {
        super.initData()

        initArgumentData()
    }

    private fun initArgumentData() {
        mWallet = arguments!!.getSerializable(Constants.PARAM_WALLET) as Wallet
    }

    override fun initView() {
        super.initView()

        initTopBar()
        initContentView()
        initButton()
    }

    private fun initTopBar() {
        topbar?.setTopBar(title = getString(R.string.wallet_feature_private_key), isBack = true)
    }

    private fun initContentView() {
        private_key_tv?.text = mWallet?.privateKey!!
    }

    private fun initButton() {
        secondary_btn_content_tv?.text = getString(R.string.next_already_backup)
    }

    override fun initListener() {
        super.initListener()

        copy_btn?.setOnClickListener {
            mContext.copy(mWallet?.privateKey)
        }

        secondary_btn?.setOnClickListener {
            PermissionUtil.requestStoragePermission(
                    context = mContext,
                    fragmentManager = childFragmentManager,
                    onGranted = {
                        pop()
                    })
        }
    }


}