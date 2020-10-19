package com.aocoin.wallet.pages.wallet

import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.pages.wallet.enum.WalletPageName
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletManagementFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 6:08 PM
 */
class WalletManagementFragment : BaseFragment() {

    override fun getUi(): Int = R.layout.fragment_wallet_management

    override fun initView() {
        super.initView()

        initTopBar()
        initRootFragment()
    }

    private fun initTopBar() {
        topbar?.setTitle(getString(R.string.wallet_management))

        topbar?.addLeftBackImageButton()?.setOnClickListener {
            activity?.finish()
        }
    }

    private fun initRootFragment() {
        val targetFragment = WalletFragment
                .newInstance(pageName = WalletPageName.Management)
        if (findFragment(targetFragment.javaClass) == null) {
            loadRootFragment(R.id.frame_layout, targetFragment)
        }
    }
}