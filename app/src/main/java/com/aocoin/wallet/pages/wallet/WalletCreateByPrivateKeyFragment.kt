package com.aocoin.wallet.pages.wallet

import android.content.Intent
import android.os.Bundle
import com.aocoin.wallet.utils.EventUtil
import com.aocoin.wallet.utils.SharedPrefKt
import com.aocoin.wallet.utils.copy
import com.aocoin.wallet.utils.showFailedTip
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.Database
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.pages.main.MainActivity
import com.aocoin.wallet.pages.wallet.utils.checkWalletExistsByPrivateKey
import com.aocoin.wallet.utils.Constants
import kotlinx.android.synthetic.main.fragment_wallet_create_by_private_key.*
import kotlinx.android.synthetic.main.include_copy_button_layout.*
import kotlinx.android.synthetic.main.include_secondary_button.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletCreateByPrivateKeyFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 11:54 AM
 */
class WalletCreateByPrivateKeyFragment : BaseFragment() {

    private lateinit var mWalletMnemonic: String
    private lateinit var mWalletPrivateKey: String
    private lateinit var mWalletKeystore: String
    private lateinit var mWalletAddress: String

    private lateinit var mWalletName: String
    private lateinit var mWalletPassword: String
    private var mWalletLocalType = WalletLocalType.LOCAL_TYPE_POK

    companion object {
        fun newInstance(walletLocalType: Int,
                        mnemonic: String,
                        privateKey: String,
                        keystore: String,
                        address: String,
                        name: String,
                        password: String
        ): WalletCreateByPrivateKeyFragment {
            val fragment = WalletCreateByPrivateKeyFragment()
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

    override fun getUi(): Int = R.layout.fragment_wallet_create_by_private_key

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
        initContentView()
        initButton()
    }

    private fun initTopBar() {
        topbar?.setTopBar(title = getString(R.string.back_up_private_key), isBack = true)
    }

    private fun initContentView() {
        private_key_tv?.text = mWalletPrivateKey
    }

    private fun initButton() {
        secondary_btn_content_tv?.text = getString(R.string.next_already_backup)
    }

    override fun initListener() {
        super.initListener()

        copy_btn?.setOnClickListener {
            mContext.copy(mWalletPrivateKey)
        }

        secondary_btn?.setOnClickListener {
            saveWallet()
        }
    }

    private fun saveWallet() {
        SharedPrefKt.saveCurrentWalletAddressAndLocalType(
                address = mWalletAddress,
                localType = mWalletLocalType)

        mContext.checkWalletExistsByPrivateKey(
                privateKey = mWalletPrivateKey,
                localType = mWalletLocalType,
                onSuccess = {
                    val wallet = Wallet()
                    wallet.address = mWalletAddress
                    wallet.localType = mWalletLocalType
                    wallet.name = mWalletName
                    wallet.password = mWalletPassword
                    wallet.privateKey = mWalletPrivateKey
                    wallet.mnemonic = mWalletMnemonic
                    wallet.keystore = mWalletKeystore
                    Database.insertMyWallet(wallet)

                    EventUtil.walletCreateSuccess()

                    val intent = Intent(mContext, MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                },
                onFailed = { msg ->
                    mContext.showFailedTip(msg)
                }
        )
    }
}