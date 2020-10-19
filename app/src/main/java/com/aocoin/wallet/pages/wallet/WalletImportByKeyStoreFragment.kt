package com.aocoin.wallet.pages.wallet

import android.os.Bundle
import com.aocoin.wallet.service.walletacount.WalletAccountManager
import com.aocoin.wallet.utils.showFailedTip
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.pages.wallet.enum.WalletGuideType
import com.aocoin.wallet.pages.wallet.enum.WalletImportType
import com.aocoin.wallet.utils.Constants
import kotlinx.android.synthetic.main.fragment_wallet_import_by_key_store.*
import kotlinx.android.synthetic.main.include_secondary_button.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletImportByKeyStoreFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 3:44 PM
 */
class WalletImportByKeyStoreFragment : BaseFragment() {
    private var mWalletLocalType = WalletLocalType.LOCAL_TYPE_POK

    private var mWalletAddress = ""
    private var mWalletPrivateKey = ""

    private val mKeyStore = {
        key_store_et?.text?.toString() ?: ""
    }

    private val mPassword = {
        password_et?.text?.toString() ?: ""
    }

    companion object {
        fun newInstance(walletLocalType: Int): WalletImportByKeyStoreFragment {
            val fragment = WalletImportByKeyStoreFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.PARAM_WALLET_LOCAL_TYPE, walletLocalType)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getUi(): Int = R.layout.fragment_wallet_import_by_key_store

    override fun initData() {
        super.initData()
        initArgumentData()
    }

    private fun initArgumentData() {
        mWalletLocalType = arguments!!.getInt(Constants.PARAM_WALLET_LOCAL_TYPE)
    }

    override fun initView() {
        super.initView()

        initTopBar()
        initButton()
    }

    private fun initTopBar() {
        topbar?.setTopBar(title = getString(R.string.title_key_store_import), isBack = true)
    }

    private fun initButton() {
        secondary_btn_content_tv?.text = getString(R.string.next_btn)
    }

    override fun initListener() {
        super.initListener()

        secondary_btn?.setOnClickListener {
            validateKeyStore(
                    onSuccess = { privateKey, address ->
                        mWalletPrivateKey = privateKey
                        mWalletAddress = address

                        startWalletForm()
                    },
                    onFailed = { failedMsg ->
                        if (failedMsg.isNotEmpty()) {
                            mContext.showFailedTip(failedMsg)
                        }
                    }
            )
        }
    }

    private fun startWalletForm() {
        start(
            WalletFormFragment.newInstance(
                guideType = WalletGuideType.Import,
                importType = WalletImportType.KeyStore,
                walletLocalType = mWalletLocalType,
                address = mWalletAddress,
                privateKey = mWalletPrivateKey,
                keyStore = mKeyStore(),
                password = mPassword()
        ))
    }

    private fun validateKeyStore(onSuccess: (String, String) -> Unit,
                                 onFailed: (String) -> Unit) {
        when {
            mKeyStore().isEmpty() -> {
                onFailed.invoke(getString(R.string.wallet_key_store_empty_tip))
            }

            mPassword().isEmpty() -> {
                onFailed.invoke(getString(R.string.wallet_key_store_password_empty_tip))
            }

            else -> {
                // 判断密码是否匹配
                validatePassWord(
                        onSuccess = { privateKey, address ->
                            onSuccess.invoke(privateKey, address)
                        },
                        onFailed = { failedMsg ->
                            onFailed.invoke(failedMsg)
                        })
            }
        }
    }

    private fun validatePassWord(onSuccess: (String, String) -> Unit,
                                 onFailed: (String) -> Unit) {
        WalletAccountManager(mWalletLocalType)
                .createWalletByKeyStore(
                        keystore = mKeyStore(),
                        password = mPassword(),
                        onSuccess = { address, privateKey, _ ->
                            onSuccess.invoke(privateKey, address)
                        },
                        onFail = { msg ->
                            onFailed.invoke(msg)
                        }
                )
    }

}