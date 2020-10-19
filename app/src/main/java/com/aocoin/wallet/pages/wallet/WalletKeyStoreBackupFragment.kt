package com.aocoin.wallet.pages.wallet

import android.os.Bundle
import com.aocoin.wallet.service.walletacount.WalletAccountManager
import com.aocoin.wallet.utils.copy
import com.aocoin.wallet.utils.doNextByLocalType
import com.aocoin.wallet.utils.replaceBlank
import com.aocoin.wallet.utils.showFailedTip
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.utils.Constants
import kotlinx.android.synthetic.main.fragment_wallet_key_store_backup.*
import kotlinx.android.synthetic.main.include_copy_button_layout.*
import kotlinx.android.synthetic.main.include_secondary_button.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletKeyStoreBackupFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/15 11:35 AM
 */
class WalletKeyStoreBackupFragment : BaseFragment() {

    private var mWallet: Wallet? = null

    companion object {
        fun newInstance(wallet: Wallet): WalletKeyStoreBackupFragment {
            val fragment = WalletKeyStoreBackupFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.PARAM_WALLET, wallet)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getUi(): Int = R.layout.fragment_wallet_key_store_backup

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
        topbar?.setTopBar(title = getString(R.string.wallet_feature_key_store), isBack = true)
    }

    private fun initContentView() {
        val localType = mWallet?.localType ?: WalletLocalType.LOCAL_TYPE_POK

        localType.doNextByLocalType(
                pok = {
                    setPokKeyStore()
                },
                ksm = {
                    setPokKeyStore()
                },
                klp = {
                    setPokKeyStore()
                }
        )
    }

    private fun setTronKeyStore() {
        mWallet?.let {
            WalletAccountManager(it.localType)
                    .reloadKeyStore(
                            keystore = it.keystore,
                            privateKey = it.privateKey,
                            oldPassword = it.password,
                            newPassword = it.password,
                            onSuccess = { keystore ->
                                key_store_tv?.text = keystore
                            },
                            onFail = { msg ->
                                mContext.showFailedTip(msg)
                            }
                    )
        }
    }

    private fun setEthKeyStore() {
        key_store_tv?.text = (mWallet?.keystore ?: "").replaceBlank()
    }

    private fun setPokKeyStore() {
        key_store_tv?.text = mWallet?.keystore ?: ""
    }

    private fun initButton() {
        secondary_btn_content_tv?.text = getString(R.string.next_already_backup)
    }

    override fun initListener() {
        super.initListener()

        copy_btn?.setOnClickListener {
            mContext.copy(key_store_tv.text?.toString() ?: "")
        }

        secondary_btn?.setOnClickListener {
            pop()
        }
    }
}