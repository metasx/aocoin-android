package com.aocoin.wallet.pages.assets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.event.AppEvent
import com.aocoin.wallet.pages.wallet.WalletSwitchFragment
import com.aocoin.wallet.pages.wallet.utils.getCurrentWallet
import com.aocoin.wallet.service.walletacount.WalletAccountManager
import com.aocoin.wallet.R
import com.aocoin.wallet.db.Coin
import com.aocoin.wallet.db.Database
import com.aocoin.wallet.pages.assets.multitype.CoinBinder
import com.aocoin.wallet.pages.trade.TransferActivity
import com.aocoin.wallet.pages.wallet.utils.formatAddress
import com.aocoin.wallet.utils.*
import com.qmuiteam.qmui.kotlin.onClick
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.fragment_assets.*
import kotlinx.android.synthetic.main.include_copy_button_layout.*
import kotlinx.android.synthetic.main.include_topbar.*
import kotlinx.android.synthetic.main.layout_assets_title_bar.*
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @FileName: AssetsFragment
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-12 15:38
 */
class AssetsFragment : BaseFragment(), OnRefreshListener {

    private lateinit var mWallet: Wallet

    private lateinit var mItems: Items
    private lateinit var mAdapter: MultiTypeAdapter

    override fun getUi(): Int = R.layout.fragment_assets

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initData() {
        super.initData()
        initWalletData()
    }

    private fun initWalletData() {
        mWallet = getCurrentWallet()
        address_tv?.text = mWallet.address.formatAddress()
    }

    override fun initView() {
        super.initView()
        topbar.setCenterView(
            LayoutInflater.from(mContext).inflate(R.layout.layout_assets_title_bar, null)
        )

        initWalletInfoView()

        refresh_layout?.apply {
            setOnRefreshListener(this@AssetsFragment)
            autoRefresh()
        }
    }

    private fun initWalletInfoView() {
        tv_wallet_name?.apply { text = mWallet?.name }
    }

    override fun initListener() {
        super.initListener()

        tv_wallet_name?.onClick {
            mContext.checkFastClick {
                showWalletSwitchBottomSheet()
            }
        }

        transfer_tv?.setOnClickListener {
            mContext.checkFastClick {
                startActivity(Intent(mContext, TransferActivity::class.java))
            }
        }

        copy_btn?.setOnClickListener {
            mContext.copy(mWallet.address)
        }
    }

    override fun initAdapter() {
        super.initAdapter()
        mItems = Items()
        mAdapter = MultiTypeAdapter(mItems)
        mAdapter.register(Coin::class.java, CoinBinder { coin ->
            // todo child page
        })
        recycler_view?.adapter = mAdapter
        notifyList()
    }

    private fun notifyList() {
        mItems.clear()
        val coinList = Database.findCoinByAddress(mWallet!!.address, mWallet!!.localType)
        mItems.addAll(coinList)
        mAdapter.notifyDataSetChanged()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        loadAssets()
    }

    private fun loadAssets() {
        mWallet?.let { wallet ->
            wallet.localType?.let { localType ->
                val manager = WalletAccountManager(localType)
                manager.loadAssets(mWallet!!.address, onSuccess = {
                    Logger.d("assets load success")
                    // 刷新列表显示
                    notifyList()
                    refresh_layout?.finishRefresh()
                }, onFail = {
                    mContext.showInfoTip("获取资产失败 $it")
                    refresh_layout?.finishRefresh()
                })
            }
        }
    }

    /**
     * 显示钱包钱包切换BottomSheet
     */
    private fun showWalletSwitchBottomSheet() {
        WalletSwitchFragment()!!.show(childFragmentManager, Constants.TAG_WALLET_SWITCH_DIALOG)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AppEvent) {
        when (event.type) {

            // 当前钱包已切换
            AppEvent.Type.CURRENT_WALLET_IS_SWITCHED -> {
                // 更新钱包信息显示
                initWalletData()
                initWalletInfoView()

                // 更新资产相关显示
                notifyList()
                loadAssets()
            }

            // 单独更新钱包信息
            AppEvent.Type.UPDATE_CURRENT_WALLET_INFO -> {
                // 更新钱包信息显示
                initWalletData()
                initWalletInfoView()
            }
        }
    }
}