package com.aocoin.wallet.pages.wallet

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aocoin.wallet.service.walletacount.WalletAccountManager
import com.aocoin.wallet.utils.EventUtil
import com.aocoin.wallet.utils.SharedPrefKt
import com.aocoin.wallet.utils.checkFastClick
import com.aocoin.wallet.utils.showFailedTip
import com.aocoin.wallet.widgets.GridSpaceItemDecoration
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.Database
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.pages.main.MainActivity
import com.aocoin.wallet.pages.wallet.adapter.WalletMnemonicAdapter
import com.aocoin.wallet.pages.wallet.type.WalletMnemonicItem
import com.aocoin.wallet.pages.wallet.utils.checkWalletExistsByPrivateKey
import com.aocoin.wallet.pages.wallet.utils.getRandomMnemonics
import com.aocoin.wallet.pages.wallet.utils.toMnemonic
import com.aocoin.wallet.utils.Constants
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import kotlinx.android.synthetic.main.fragment_wallet_create_by_mnemonic_confirm.*
import kotlinx.android.synthetic.main.include_secondary_button.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletCreateByMnemonicConfirmFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 1:32 PM
 */
class WalletCreateByMnemonicConfirmFragment : BaseFragment() {

    private lateinit var mWalletPrivateKey: String
    private lateinit var mWalletKeystore: String
    private lateinit var mWalletAddress: String

    private lateinit var mWalletName: String
    private lateinit var mWalletPassword: String

    private var mWalletLocalType = WalletLocalType.LOCAL_TYPE_POK

    /**
     * 原助记词
     */
    private lateinit var mWalletMnemonic: String

    /**
     * 打乱顺序后的助记词
     */
    private var mRandomMnemonics: ArrayList<WalletMnemonicItem> = arrayListOf()

    /**
     * 打乱顺序后的助记词适配器
     */
    private lateinit var mRandomMnemonicAdapter: WalletMnemonicAdapter

    /**
     * 选中的助记词列表
     */
    private var mSelectedMnemonics: ArrayList<WalletMnemonicItem> = arrayListOf()

    /**
     * 选中的助记词适配器
     */
    private lateinit var mSelectedMnemonicAdapter: WalletMnemonicAdapter

    private val WALLET_MNEMONIC_WORDS_SIZE = 12

    companion object {
        fun newInstance(walletLocalType: Int,
                        mnemonic: String,
                        privateKey: String,
                        keystore: String,
                        address: String,
                        name: String,
                        password: String)
                : WalletCreateByMnemonicConfirmFragment {
            val fragment = WalletCreateByMnemonicConfirmFragment()
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

    override fun getUi(): Int = R.layout.fragment_wallet_create_by_mnemonic_confirm

    override fun initData() {
        super.initData()

        initArgumentData()
        initRandomMnemonicWords()
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

    private fun initRandomMnemonicWords() {
        mRandomMnemonics.addAll(mWalletMnemonic.getRandomMnemonics())
    }

    override fun initView() {
        super.initView()

        initTopBar()
        initButton()
    }

    private fun initTopBar() {
        topbar?.setTopBar(title = getString(R.string.title_verify_mnemonic), isBack = true)
    }

    private fun initButton() {
        secondary_btn_content_tv?.text = getString(R.string.next_btn)
    }

    private fun RecyclerView?.init() {
        val spanCount = 3
        this?.layoutManager = GridLayoutManager(mContext, spanCount)
        this?.addItemDecoration(
            GridSpaceItemDecoration(
                spanCount = spanCount,
                horizontalSpace = QMUIDisplayHelper.dp2px(mContext, 30),
                verticalSpace = QMUIDisplayHelper.dp2px(mContext, 15),
                includeEdge = false)
        )
    }

    private fun updateNoticeTextVisibility() {
        notice_tv?.visibility = if (mSelectedMnemonics.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun initListener() {
        super.initListener()

        secondary_btn?.setOnClickListener {
            mContext.checkFastClick {
                when {
                    mSelectedMnemonics.size < WALLET_MNEMONIC_WORDS_SIZE -> {
                        mContext.showFailedTip(getString(R.string.wallet_mnemonic_select_all_tip))
                    }

                    mSelectedMnemonics.toMnemonic() != mWalletMnemonic -> {
                        mContext.showFailedTip(getString(R.string.wallet_mnemonic_order_error_tip))
                    }

                    else -> {
                        // 不同公链校验助记词
                        WalletAccountManager(mWalletLocalType)
                                .checkMnemonic(
                                        mnemonic = mWalletMnemonic,
                                        onSuccess = { _, _ ->
                                            saveWallet()
                                        },
                                        onFail = { msg ->
                                            mContext.showFailedTip(msg)
                                        })
                    }
                }
            }
        }
    }

    override fun initAdapter() {
        super.initAdapter()

        initWordAdapter()
        initSelectedWordAdapter()
    }

    private fun initWordAdapter() {
        mRandomMnemonicAdapter = WalletMnemonicAdapter(
                mMnemonics = mRandomMnemonics,
                mSelectedMnemonics = mSelectedMnemonics) { mnemonic ->

            if (mSelectedMnemonics.contains(mnemonic)) {
                mSelectedMnemonics.remove(mnemonic)
            } else {
                mSelectedMnemonics.add(mnemonic)
            }

            mRandomMnemonicAdapter.notifyDataSetChanged()
            mSelectedMnemonicAdapter.notifyDataSetChanged()
            updateNoticeTextVisibility()
        }
        recycler_view?.init()
        recycler_view?.adapter = mRandomMnemonicAdapter
    }

    private fun initSelectedWordAdapter() {
        mSelectedMnemonicAdapter = WalletMnemonicAdapter(mSelectedMnemonics)

        selected_recycler_view?.init()
        selected_recycler_view?.adapter = mSelectedMnemonicAdapter
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
                    wallet.keystore = ""
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