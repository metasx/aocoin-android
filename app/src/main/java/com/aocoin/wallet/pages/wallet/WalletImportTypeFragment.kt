package com.aocoin.wallet.pages.wallet

import android.os.Bundle
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.pages.common.multitype.GroupItemBinder
import com.aocoin.wallet.pages.common.type.GroupItem
import com.aocoin.wallet.utils.Constants
import kotlinx.android.synthetic.main.fragment_wallet_import_type.*
import kotlinx.android.synthetic.main.include_topbar.*
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @FileName WalletImportTypeFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 2:19 PM
 */
class WalletImportTypeFragment : BaseFragment() {

    private var mWalletLocalType = WalletLocalType.LOCAL_TYPE_POK

    private lateinit var mItems: Items
    private lateinit var mAdapter: MultiTypeAdapter

    companion object {
        fun newInstance(walletLocalType: Int): WalletImportTypeFragment {
            val fragment = WalletImportTypeFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.PARAM_WALLET_LOCAL_TYPE, walletLocalType)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getUi(): Int = R.layout.fragment_wallet_import_type

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
    }

    private fun initTopBar() {
        topbar?.setTopBar(title = getString(R.string.title_import_wallet), isBack = true)
    }

    override fun initAdapter() {
        super.initAdapter()

        mItems = Items()
        mItems.add(
            GroupItem(title = getString(R.string.wallet_import_type_mnemonic),
                icon = R.mipmap.ic_wallet_import_by_mnemonic,
                onClockListener = {
                    start(
                        WalletImportByMnemonicFragment
                            .newInstance(walletLocalType = mWalletLocalType))
                })
        )

        mItems.add(GroupItem(title = getString(R.string.wallet_import_type_private_key),
                icon = R.mipmap.ic_wallet_import_by_private_key,
                onClockListener = {
                    start(
                        WalletImportByPrivateKeyFragment
                            .newInstance(walletLocalType = mWalletLocalType))
                }
        ))
        mItems.add(
            GroupItem(title = getString(R.string.wallet_import_type_keystore),
                icon = R.mipmap.ic_wallet_import_by_key_store,
                onClockListener = {
                    start(
                        WalletImportByKeyStoreFragment
                            .newInstance(walletLocalType = mWalletLocalType))
                })
        )

        mAdapter = MultiTypeAdapter(mItems)
        mAdapter.register(GroupItem::class.java, GroupItemBinder())
        recycler_view?.adapter = mAdapter
    }

}