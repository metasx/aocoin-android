package com.aocoin.wallet.pages.wallet

import android.content.Intent
import android.os.Bundle
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.Database
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.pages.wallet.enum.WalletFeature
import com.aocoin.wallet.pages.wallet.multitype.WalletFeatureBinder
import com.aocoin.wallet.pages.wallet.type.WalletFeatureItem
import com.aocoin.wallet.pages.wallet.utils.formatAddress
import com.aocoin.wallet.utils.*
import kotlinx.android.synthetic.main.fragment_wallet_detail.*
import kotlinx.android.synthetic.main.include_copy_button_layout.*
import kotlinx.android.synthetic.main.include_topbar.*
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter

/**
 * @FileName WalletDetailFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/15 10:21 AM
 */
class WalletDetailFragment : BaseFragment() {
    private var mWallet: Wallet? = null

    private lateinit var mItems: Items
    private lateinit var mAdapter: MultiTypeAdapter

    private var mFeatures: ArrayList<WalletFeatureItem> = arrayListOf()

    companion object {
        fun newInstance(wallet: Wallet): WalletDetailFragment {
            val fragment = WalletDetailFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.PARAM_WALLET, wallet)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onSupportVisible() {
        super.onSupportVisible()

        initWalletData()
        initWalletInfoView()
    }

    override fun getUi(): Int = R.layout.fragment_wallet_detail

    override fun initData() {
        super.initData()

        initWalletData()
        initFeatures()
    }

    private fun initWalletData() {
        mWallet = arguments!!.getSerializable(Constants.PARAM_WALLET) as Wallet
    }

    private fun initFeatures() {
        val nameItem = WalletFeatureItem(
                feature = WalletFeature.UpdateName,
                name = getString(R.string.wallet_feature_update_name))

        val passwordItem = WalletFeatureItem(
                feature = WalletFeature.UpdatePassword,
                name = getString(R.string.wallet_feature_update_pwd))

        val mnemonicItem = WalletFeatureItem(
                feature = WalletFeature.Mnemonic,
                name = getString(R.string.wallet_feature_update_mnemonic))

        val privateKeyItem = WalletFeatureItem(
                feature = WalletFeature.PrivateKey,
                name = getString(R.string.wallet_feature_private_key))

        val keyStoreItem = WalletFeatureItem(
                feature = WalletFeature.KeyStore,
                name = getString(R.string.wallet_feature_key_store))

        mFeatures.add(nameItem)
        mFeatures.add(passwordItem)
        if (!mWallet?.mnemonic.isNullOrEmpty()) {
            mFeatures.add(mnemonicItem)
        }
        if (!mWallet?.privateKey.isNullOrEmpty()) {
            mFeatures.add(privateKeyItem)
        }
        mFeatures.add(keyStoreItem)
    }

    override fun initView() {
        super.initView()

        initTopBar()
        initWalletInfoView()
    }

    private fun initTopBar() {
        topbar?.setTopBar(title = getString(R.string.wallet_management), isBack = true)

        // 当用户每个公链下仅剩一个钱包的情况 ，钱包管理页面中隐藏删除按钮
        val wallets = Database.findMyWalletsByLocalType(mWallet?.localType!!)
        if (wallets.size > 1) {
            topbar?.addRightImageButton(
                    R.mipmap.ic_white_delete,
                    R.id.topbar_right_delete_wallet_iv)
                    ?.setOnClickListener {
                        mContext.checkFastClick {
                            showDeleteDialog()
                        }
                    }
        }
    }

    private fun initWalletInfoView() {
        name_tv?.text = mWallet?.name
        address_tv?.text = mWallet?.address!!.formatAddress()
    }

    private fun showDeleteDialog() {
        childFragmentManager.showWalletPasswordDialog(
                wallet = mWallet,
                onSuccessful = {
                    deleteWallet()
                })
    }

    /**
     * 删除钱包
     */
    private fun deleteWallet() {
        // 删除钱包
        Database.deleteMyWalletByAddressAndLocalType(mWallet?.address, mWallet?.localType!!)

        val walletAddress = SharedPrefKt.getCurrentWalletAddress()
        if (mWallet?.address == walletAddress) {
            val allWallets = Database.findMyWalletsByLocalType(mWallet!!.localType)

            if (allWallets.isNotEmpty()) {
                val wallet = allWallets.first()
                SharedPrefKt.saveCurrentWalletAddressAndLocalType(
                        address = wallet.address,
                        localType = wallet.localType)

                EventUtil.currentWalletIsSwitched()
            } else {
                SharedPrefKt.saveCurrentWalletAddressAndLocalType(
                        address = "",
                        localType = 0)

                val intent = Intent(mContext, WalletGuideActivity::class.java)
                startActivity(intent)
            }
        }

        mContext.showSuccessTip(content = getString(R.string.delete_success)) {
            pop()
        }
    }

    override fun initAdapter() {
        super.initAdapter()

        mItems = Items()
        mItems.addAll(mFeatures)
        mAdapter = MultiTypeAdapter(mItems)
        mAdapter.register(
            WalletFeatureItem::class.java,
                WalletFeatureBinder { featureItem ->
                    featureItemClick(featureItem)
                })
        recycler_view?.adapter = mAdapter
    }

    private fun featureItemClick(featureItem: WalletFeatureItem) {
        when (featureItem.feature) {
            // 更新名称
            WalletFeature.UpdateName -> {
                mContext.checkFastClick {
                    start(WalletNameFragment.newInstance(wallet = mWallet!!))
                }
            }

            // 更新密码
            WalletFeature.UpdatePassword -> {
                mContext.checkFastClick {
                    start(WalletPasswordFragment.newInstance(wallet = mWallet!!))
                }
            }

            // 备份助记词
            WalletFeature.Mnemonic -> {
                mContext.checkFastClick {
                    childFragmentManager.showWalletPasswordDialog(
                            wallet = mWallet!!,
                            onSuccessful = {
                                start(WalletMnemonicBackupFragment.newInstance(mWallet?.mnemonic!!))
                            }
                    )
                }
            }

            // 备份私钥
            WalletFeature.PrivateKey -> {
                mContext.checkFastClick {
                    childFragmentManager.showWalletPasswordDialog(
                            wallet = mWallet!!,
                            onSuccessful = {
                                start(WalletPrivateKeyBackupFragment.newInstance(wallet = mWallet!!))
                            }
                    )
                }
            }

            // 备份KeyStore
            WalletFeature.KeyStore -> {
                mContext.checkFastClick {
                    childFragmentManager.showWalletPasswordDialog(
                            wallet = mWallet!!,
                            onSuccessful = {
                                start(WalletKeyStoreBackupFragment.newInstance(wallet = mWallet!!))
                            }
                    )
                }
            }
        }
    }

    override fun initListener() {
        super.initListener()

        // 复制
        copy_btn?.setOnClickListener {
            mContext.copy(mWallet?.address)
        }
    }

}