package com.aocoin.wallet.pages.wallet

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.aocoin.wallet.widgets.GridSpaceItemDecoration
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.pages.wallet.adapter.WalletMnemonicAdapter
import com.aocoin.wallet.pages.wallet.utils.getMnemonics
import com.aocoin.wallet.utils.Constants
import com.aocoin.wallet.db.WalletLocalType
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import kotlinx.android.synthetic.main.fragment_wallet_create_by_mnemonic.*
import kotlinx.android.synthetic.main.include_secondary_button.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletCreateByMnemonicFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 1:24 PM
 */
class WalletCreateByMnemonicFragment : BaseFragment() {
    private lateinit var mWalletMnemonic: String
    private lateinit var mWalletPrivateKey: String
    private lateinit var mWalletKeystore: String
    private lateinit var mWalletAddress: String

    private lateinit var mWalletName: String
    private lateinit var mWalletPassword: String
    private var mWalletLocalType = WalletLocalType.LOCAL_TYPE_POK

    private lateinit var mAdapter: WalletMnemonicAdapter

    companion object {
        fun newInstance(walletLocalType: Int,
                        mnemonic: String,
                        privateKey: String,
                        keystore: String,
                        address: String,
                        name: String,
                        password: String)
                : WalletCreateByMnemonicFragment {
            val fragment = WalletCreateByMnemonicFragment()
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

    override fun getUi(): Int = R.layout.fragment_wallet_create_by_mnemonic

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
        topbar?.setTopBar(title = getString(R.string.title_backup_mnemonic), isBack = true)
    }

    private fun initButton() {
        secondary_btn_content_tv?.text = getString(R.string.next_btn)
    }

    override fun initAdapter() {
        super.initAdapter()
        val spanCount = 3

        mAdapter = WalletMnemonicAdapter(
                mMnemonics = mWalletMnemonic.getMnemonics())
        recycler_view?.layoutManager = GridLayoutManager(mContext, spanCount)
        recycler_view?.addItemDecoration(
            GridSpaceItemDecoration(
                spanCount = spanCount,
                horizontalSpace = QMUIDisplayHelper.dp2px(mContext, 30),
                verticalSpace = QMUIDisplayHelper.dp2px(mContext, 15),
                includeEdge = false)
        )
        recycler_view?.adapter = mAdapter
    }

    override fun initListener() {
        super.initListener()

        secondary_btn?.setOnClickListener {
            if (mWalletMnemonic.isNotEmpty()) {
                start(
                    WalletCreateByMnemonicConfirmFragment.newInstance(
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

}