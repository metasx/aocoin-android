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
import kotlinx.android.synthetic.main.fragment_wallet_import_by_mnemonic.*
import kotlinx.android.synthetic.main.include_secondary_button.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletImportByMnemonicFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 2:28 PM
 */
class WalletImportByMnemonicFragment : BaseFragment() {

    private var mWalletLocalType = WalletLocalType.LOCAL_TYPE_POK

    private var mWalletAddress = ""
    private var mWalletPrivateKey = ""
    private var mWalletKeystore = ""

    private val mInputContent = {
        input_et?.text?.toString() ?: ""
    }

    companion object {
        fun newInstance(walletLocalType: Int): WalletImportByMnemonicFragment {
            val fragment = WalletImportByMnemonicFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.PARAM_WALLET_LOCAL_TYPE, walletLocalType)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getUi(): Int = R.layout.fragment_wallet_import_by_mnemonic

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
        topbar?.setTopBar(title = getString(R.string.title_mnemonic_import), isBack = true)
    }

    private fun initButton() {
        secondary_btn_content_tv?.text = getString(R.string.next_btn)
    }

    override fun initListener() {
        super.initListener()

        secondary_btn?.setOnClickListener {

            mInputContent().validateMnemonic(
                    onSuccess = { address: String, privateKey: String, keystore: String ->
                        mWalletAddress = address
                        mWalletPrivateKey = privateKey
                        mWalletKeystore = keystore

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
                importType = WalletImportType.Mnemonic,
                walletLocalType = mWalletLocalType,
                mnemonic = mInputContent(),
                privateKey = mWalletPrivateKey,
                keyStore = mWalletKeystore,
                address = mWalletAddress
        ))
    }

    /**
     * 验证助记词
     */
    private fun String?.validateMnemonic(onSuccess: (address: String, privateKey: String, keystore: String) -> Unit,
                                         onFailed: (String) -> Unit) {

        if (this != null) {
            val mnemonics = this.trim().split(" ")

            when {
                this.isEmpty() -> {
                    onFailed.invoke(getString(R.string.wallet_mnemonic_empty_tip))
                }

                mnemonics.size < 12 -> {
                    onFailed.invoke(getString(R.string.wallet_mnemonic_not_enough_length_tip))
                }

                else -> {
                    val manager = WalletAccountManager(mWalletLocalType)

                    manager.checkMnemonic(
                            mnemonic = this,
                            onSuccess = { _, _ ->
                                manager.createWalletByMnemonic(
                                        mnemonic = this,
                                        password = "",
                                        onSuccess = { address, privateKey, keystore ->
                                            onSuccess.invoke(address, privateKey, keystore)
                                        },
                                        onFail = { msg ->
                                            mContext.showFailedTip(msg)
                                        })
                            },
                            onFail = { msg ->
                                onFailed.invoke(msg)
                            })
                }
            }
        } else {
            onFailed.invoke("")
        }

    }
}