package com.aocoin.wallet.pages.wallet

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.aocoin.wallet.widgets.GridSpaceItemDecoration
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.pages.wallet.adapter.WalletMnemonicAdapter
import com.aocoin.wallet.pages.wallet.utils.getMnemonics
import com.aocoin.wallet.utils.Constants
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import kotlinx.android.synthetic.main.fragment_wallet_create_by_mnemonic.*
import kotlinx.android.synthetic.main.include_secondary_button.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletMnemonicBackupFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/15 11:29 AM
 */

class WalletMnemonicBackupFragment : BaseFragment() {

    private lateinit var mMnemonic: String
    private lateinit var mAdapter: WalletMnemonicAdapter

    companion object {
        fun newInstance(mnemonic: String)
                : WalletMnemonicBackupFragment {
            val fragment = WalletMnemonicBackupFragment()
            val bundle = Bundle()
            bundle.putString(Constants.PARAM_WALLET_MNEMONIC, mnemonic)
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
        mMnemonic = arguments!!.getString(Constants.PARAM_WALLET_MNEMONIC) ?: ""
    }

    override fun initView() {
        super.initView()

        initTopBar()
        initVisibility()
        initButton()
    }

    private fun initTopBar() {
        topbar?.setTopBar(title = getString(R.string.title_backup_mnemonic), isBack = true)
    }

    private fun initVisibility() {
        notice_tv?.text = getString(R.string.wallet_backup_mnemonic_msg)
    }

    private fun initButton() {
        secondary_btn_content_tv?.text = getString(R.string.next_already_backup)
    }

    override fun initListener() {
        super.initListener()

        secondary_btn?.setOnClickListener {
            popTo(WalletDetailFragment::class.java, false)
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        val spanCount = 3

        mAdapter = WalletMnemonicAdapter(
                mMnemonics = mMnemonic.getMnemonics())
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
}