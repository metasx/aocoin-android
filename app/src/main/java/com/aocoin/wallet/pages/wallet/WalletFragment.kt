package com.aocoin.wallet.pages.wallet

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.aocoin.wallet.pages.wallet.multitype.WalletBinder
import com.aocoin.wallet.utils.EventUtil
import com.aocoin.wallet.utils.SharedPrefKt
import com.aocoin.wallet.utils.checkFastClick
import com.aocoin.wallet.utils.doNextByLocalType
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.Database
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.pages.wallet.enum.WalletPageName
import com.aocoin.wallet.pages.wallet.multitype.WalletHeaderBinder
import com.aocoin.wallet.pages.wallet.multitype.WalletKindBinder
import com.aocoin.wallet.pages.wallet.type.WalletHeaderItem
import com.aocoin.wallet.pages.wallet.type.WalletKindItem
import com.aocoin.wallet.pages.wallet.utils.check
import com.aocoin.wallet.pages.wallet.utils.getCurrentWallet
import com.aocoin.wallet.utils.Constants
import kotlinx.android.synthetic.main.fragment_wallet.*
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @FileName WalletFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 5:54 PM
 */
class WalletFragment : BaseFragment() {

    /**
     * 钱包分类适配器
     */
    private lateinit var mKindItems: Items
    private lateinit var mKindAdapter: MultiTypeAdapter
    private lateinit var mWalletKindBinder: WalletKindBinder

    /**
     * 钱包适配器
     */
    private lateinit var mWalletItems: Items
    private lateinit var mWalletAdapter: MultiTypeAdapter
    private lateinit var mWalletBinder: WalletBinder

    /**
     * 当前选中的钱包类型
     */
    private var mSelectedWalletLocalType = WalletLocalType.LOCAL_TYPE_POK

    /**
     * 页面名称
     */
    private var mWalletPageName = WalletPageName.Switch

    /**
     * 当前钱包
     */
    private var mCurrentWallet: Wallet? = null

    private val mWalletKinds = {
        listOf(
                // POK
                WalletKindItem(
                        localType = WalletLocalType.LOCAL_TYPE_POK,
                        selectedIcon = R.mipmap.ic_pok_coin_selected,
                        unselectedIcon = R.mipmap.ic_pok_coin_unselected),

                // KSM
                WalletKindItem(
                        localType = WalletLocalType.LOCAL_TYPE_KSM,
                        selectedIcon = R.mipmap.ic_ksm_coin_selected,
                        unselectedIcon = R.mipmap.ic_ksm_coin_unselected),

                // KLP
                WalletKindItem(
                        localType = WalletLocalType.LOCAL_TYPE_KLP,
                        selectedIcon = R.mipmap.ic_klp_coin_selected,
                        unselectedIcon = R.mipmap.ic_klp_coin_unselected)
        )
    }

    private var mParentFragment: BaseFragment? = null

    companion object {
        fun newInstance(pageName: WalletPageName): WalletFragment {
            val fragment = WalletFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.PARAM_WALLET_PAGE_NAME, pageName)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getUi(): Int = R.layout.fragment_wallet

    override fun onSupportVisible() {
        super.onSupportVisible()

        initWalletData()

        mSelectedWalletLocalType.updateWalletAdapter()

        initDefaultSelected()

        EventUtil.updateWalletManagementIcon(localType = mSelectedWalletLocalType)
    }

    override fun initData() {
        super.initData()

        initWalletData()

        initPatentFragment()

        initArgumentData()
    }

    private fun initWalletData() {
        mCurrentWallet = getCurrentWallet()

        mSelectedWalletLocalType = mCurrentWallet?.localType!!
    }

    private fun initPatentFragment() {
        if (parentFragment is WalletManagementFragment) {
            mParentFragment = parentFragment as WalletManagementFragment
        }
    }

    private fun initArgumentData() {
        mWalletPageName = arguments!!.getSerializable(Constants.PARAM_WALLET_PAGE_NAME) as WalletPageName
    }

    private fun getWalletHeader(): WalletHeaderItem {
        var name = ""
        mSelectedWalletLocalType.doNextByLocalType(
                pok = {
                    name = getString(R.string.wallet_type_pok)
                },
                ksm = {
                    name = getString(R.string.wallet_type_ksm)
                },
                klp = {
                    name = getString(R.string.wallet_type_klp)
                }
        )

        return WalletHeaderItem(
                name = name,
                localType = mSelectedWalletLocalType)
    }

    override fun initView() {
        super.initView()

        // 分类列表背景
        kind_recycler_view?.setBackgroundResource(
                if (mWalletPageName == WalletPageName.Switch) {
                    R.color.wallet_switch_kind_view_bg
                } else {
                    R.color.wallet_management_kind_view_bg
                })

        // 钱包列表背景
        wallet_recycler_view?.setBackgroundResource(
                if (mWalletPageName == WalletPageName.Switch) {
                    R.color.wallet_switch_wallet_view_bg
                } else {
                    R.color.wallet_management_wallet_view_bg
                })

        space_view?.visibility =
                if (mWalletPageName == WalletPageName.Switch) View.GONE else View.VISIBLE
        space_view?.setBackgroundResource(
                if (mWalletPageName == WalletPageName.Switch) {
                    R.color.wallet_switch_space_view_bg
                } else {
                    R.color.wallet_management_space_view_bg
                }
        )
    }

    /**
     * 根据钱包类型获取钱包列表
     */
    private fun getWalletsByType(): List<Wallet> {
        return Database.findAllWallets().filter { it.localType == mSelectedWalletLocalType }
    }

    override fun initAdapter() {
        super.initAdapter()

        initKindAdapter()
        initWalletAdapter()
        initDefaultSelected()
    }

    private fun initKindAdapter() {
        mKindItems = Items()
        mKindItems.addAll(mWalletKinds())
        mKindAdapter = MultiTypeAdapter(mKindItems)

        mWalletKindBinder = WalletKindBinder(mWalletPageName) { walletKindItem ->
            // 更新钱包列表
            walletKindItem.localType.updateWalletAdapter()

            // 更新管理按钮
            mWalletPageName.check(isSwitch = {
                EventUtil.updateWalletManagementIcon(localType = mSelectedWalletLocalType)
            })
        }

        mKindAdapter.register(WalletKindItem::class.java, mWalletKindBinder)

        kind_recycler_view?.adapter = mKindAdapter
    }

    private fun initWalletAdapter() {
        mWalletItems = Items()
        mWalletItems.add(getWalletHeader())
        mWalletItems.addAll(getWalletsByType())
        mWalletAdapter = MultiTypeAdapter(mWalletItems)

        // 头部
        mWalletAdapter.register(
            WalletHeaderItem::class.java,
                WalletHeaderBinder(mWalletPageName) {
                    mWalletPageName.check(
                            isSwitch = {
                                startAddWalletActivity()
                            },

                            isManagement = {
                                startAddWalletFragment()
                            })
                })

        mWalletBinder = WalletBinder(mWalletPageName) { wallet ->
            mWalletPageName.check(
                    isSwitch = {
                        mContext.checkFastClick {
                            mWalletBinder.setSelectedWallet(wallet)
                            updateCurrentWallet(wallet)
                        }
                    },

                    isManagement = {
                        mContext.checkFastClick {
                            mParentFragment?.start(WalletDetailFragment.newInstance(wallet))
                        }
                    })
        }

        // 钱包
        mWalletAdapter.register(Wallet::class.java, mWalletBinder)

        wallet_recycler_view?.adapter = mWalletAdapter
    }

    private fun Int.updateWalletAdapter() {
        mSelectedWalletLocalType = this

        val wallets = getWalletsByType()
        mWalletItems.clear()
        mWalletItems.add(getWalletHeader())
        mWalletItems.addAll(wallets)
        mWalletAdapter.notifyDataSetChanged()
    }

    private fun initDefaultSelected() {
        mWalletKindBinder.setSelectedType(mSelectedWalletLocalType)

        mWalletBinder.setSelectedWallet(mCurrentWallet ?: Wallet())
    }

    /**
     * 更新当前钱包
     */
    private fun updateCurrentWallet(wallet: Wallet) {
        // 更新当前钱包类型
        mSelectedWalletLocalType = wallet.localType

        // 更新本地当前钱包地址
        SharedPrefKt.saveCurrentWalletAddressAndLocalType(
                address = wallet.address,
                localType = wallet.localType)

        // 通知页面更新
        EventUtil.currentWalletIsSwitched()
    }

    /**
     * 打开添加钱包页
     */
    private fun startAddWalletActivity() {
        mContext.checkFastClick {
            val intent = Intent(activity, WalletAddActivity::class.java)
            intent.putExtra(Constants.PARAM_WALLET_LOCAL_TYPE, mSelectedWalletLocalType)
            activity?.startActivity(intent)
        }
    }

    private fun startAddWalletFragment() {
        mParentFragment?.start(
            WalletAddFragment
                .newInstance(walletLocalType = mSelectedWalletLocalType))
    }
}