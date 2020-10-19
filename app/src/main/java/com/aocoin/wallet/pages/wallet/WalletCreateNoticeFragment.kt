package com.aocoin.wallet.pages.wallet

import android.os.Bundle
import com.aocoin.wallet.utils.showMessagePositiveDialog
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.utils.Constants
import kotlinx.android.synthetic.main.include_primary_button.*
import kotlinx.android.synthetic.main.include_secondary_button.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletCreateNoticeFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 11:13 AM
 */
class WalletCreateNoticeFragment : BaseFragment() {

    private lateinit var mWalletMnemonic: String
    private lateinit var mWalletPrivateKey: String
    private lateinit var mWalletKeystore: String
    private lateinit var mWalletAddress: String

    private lateinit var mWalletName: String
    private lateinit var mWalletPassword: String
    private var mWalletLocalType = WalletLocalType.LOCAL_TYPE_POK

    companion object {
        fun newInstance(
                walletLocalType: Int,
                mnemonic: String,
                privateKey: String,
                keystore: String,
                address: String,
                name: String,
                password: String)
                : WalletCreateNoticeFragment {
            val fragment = WalletCreateNoticeFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.PARAM_WALLET_LOCAL_TYPE, walletLocalType)
            bundle.putString(Constants.PARAM_WALLET_MNEMONIC, mnemonic)
            bundle.putString(Constants.PARAM_WALLET_PRIVATE_KEY, privateKey)
            bundle.putString(Constants.PARAM_WALLET_KEY_STORE, keystore)
            bundle.putString(Constants.PARAM_WALLET_ADDRESS, address)

            bundle.putString(Constants.PARAM_NAME, name)
            bundle.putString(Constants.PARAM_PASSWORD, password)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getUi(): Int = R.layout.fragment_wallet_create_notice

    override fun initData() {
        super.initData()

        initArgumentData()
    }

    private fun initArgumentData() {
        mWalletLocalType = arguments!!.getInt(Constants.PARAM_WALLET_LOCAL_TYPE)
        mWalletMnemonic = arguments!!.getString(Constants.PARAM_WALLET_MNEMONIC)!!
        mWalletPrivateKey = arguments!!.getString(Constants.PARAM_WALLET_PRIVATE_KEY)!!
        mWalletKeystore = arguments!!.getString(Constants.PARAM_WALLET_KEY_STORE)!!
        mWalletAddress = arguments!!.getString(Constants.PARAM_WALLET_ADDRESS)!!

        mWalletName = arguments!!.getString(Constants.PARAM_NAME)!!
        mWalletPassword = arguments!!.getString(Constants.PARAM_PASSWORD)!!
    }

    override fun initView() {
        super.initView()

        initTopBar()
        initButton()
    }

    private fun initTopBar() {
        topbar?.setTopBar(title = getString(R.string.title_backup_notice), isBack = true)
    }

    private fun initButton() {
        primary_btn_content_tv?.text = getString(R.string.title_backup_key)
        secondary_btn_content_tv?.text = getString(R.string.title_backup_mnemonic)

        primary_btn_start_iv?.setImageResource(R.mipmap.ic_recommend)
    }

    override fun initListener() {
        super.initListener()

        // 备份私钥
        primary_btn?.setOnClickListener {
            showWarnDialog {
                start(
                    WalletCreateByPrivateKeyFragment.newInstance(
                        walletLocalType = mWalletLocalType,
                        mnemonic = mWalletMnemonic,
                        privateKey = mWalletPrivateKey,
                        keystore = mWalletKeystore,
                        address = mWalletAddress,
                        name = mWalletName,
                        password = mWalletPassword
                ))
            }
        }

        // 备份助记词
        secondary_btn?.setOnClickListener {
            showWarnDialog {
                start(
                    WalletCreateByMnemonicFragment.newInstance(
                        walletLocalType = mWalletLocalType,
                        mnemonic = mWalletMnemonic,
                        privateKey = mWalletPrivateKey,
                        keystore = mWalletKeystore,
                        address = mWalletAddress,
                        name = mWalletName,
                        password = mWalletPassword
                ))
            }
        }
    }

    private fun showWarnDialog(onComplete: () -> Unit) {
        childFragmentManager.showMessagePositiveDialog(
                title = getString(R.string.wallet_backup_warn_dialog_title),
                message = getString(R.string.wallet_backup_warn_dialog_content),
                onPositiveListener = {
                    onComplete.invoke()
                }
        )
    }
}