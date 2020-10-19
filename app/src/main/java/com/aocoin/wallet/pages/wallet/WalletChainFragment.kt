package com.aocoin.wallet.pages.wallet

import android.os.Bundle
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.pages.wallet.enum.WalletGuideType
import com.aocoin.wallet.pages.wallet.multitype.WalletChainBinder
import com.aocoin.wallet.pages.wallet.type.WalletChainItem
import com.aocoin.wallet.utils.Constants
import kotlinx.android.synthetic.main.fragment_wallet_chain.*
import kotlinx.android.synthetic.main.include_secondary_button.*
import kotlinx.android.synthetic.main.include_topbar.*
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @FileName WalletChainFragment
 * @Description
 * @Author dingyan
 * @Date 2020-01-07 11:09
 */
class WalletChainFragment : BaseFragment() {

    private lateinit var mItems: Items
    private lateinit var mAdapter: MultiTypeAdapter
    private lateinit var mChainBinder: WalletChainBinder
    private var mChains = {
        arrayListOf(
                WalletChainItem(
                        icon = R.mipmap.ic_pok_coin_selected,
                        localType = WalletLocalType.LOCAL_TYPE_POK,
                        name = Constants.WALLET_TYPE_POLKADOT_NAME),
                WalletChainItem(
                        icon = R.mipmap.ic_ksm_coin_selected,
                        localType = WalletLocalType.LOCAL_TYPE_KSM,
                        name = Constants.WALLET_TYPE_KUSAMA_NAME),
                WalletChainItem(
                        icon = R.mipmap.ic_klp_coin_selected,
                        localType = WalletLocalType.LOCAL_TYPE_KLP,
                        name = Constants.WALLET_TYPE_KULUPU_NAME)
        )
    }

    /**
     * 引导类型
     */
    private var mGuideType = WalletGuideType.Create

    /**
     * 当前选中的链类型
     */
    private var mSelectedLocalType = WalletLocalType.LOCAL_TYPE_POK

    companion object {
        fun newInstance(guideType: WalletGuideType = WalletGuideType.Create): WalletChainFragment {
            val fragment = WalletChainFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.PARAM_WALLET_GUIDE_TYPE, guideType)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getUi(): Int = R.layout.fragment_wallet_chain

    override fun initData() {
        super.initData()

        initArgumentData()
    }

    private fun initArgumentData() {
        mGuideType = arguments!!.getSerializable(Constants.PARAM_WALLET_GUIDE_TYPE) as WalletGuideType
    }

    override fun initView() {
        super.initView()

        initTopBar()
        initButton()
    }

    private fun initTopBar() {
        topbar?.setTopBar(title = getString(R.string.wallet_chain), isBack = true)
    }

    private fun initButton() {
        secondary_btn_content_tv?.text = getString(R.string.next_btn)
    }

    override fun initAdapter() {
        super.initAdapter()

        mItems = Items()
        mItems.addAll(mChains())
        mAdapter = MultiTypeAdapter(mItems)

        mChainBinder = WalletChainBinder { chainItem ->
            mSelectedLocalType = chainItem.localType
        }

        mAdapter.register(WalletChainItem::class.java, mChainBinder)
        recycler_view?.adapter = mAdapter
    }

    override fun initListener() {
        super.initListener()

        secondary_btn?.setOnClickListener {
            startWalletInfoForm()
        }
    }

    private fun startWalletInfoForm() {
        when (mGuideType) {
            WalletGuideType.Create -> {
                start(
                    WalletFormFragment.newInstance(
                        guideType = mGuideType,
                        walletLocalType = mSelectedLocalType
                    )
                )
            }

            WalletGuideType.Import -> {
                start(WalletImportTypeFragment.newInstance(walletLocalType = mSelectedLocalType))
            }
        }
    }
}