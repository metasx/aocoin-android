package com.aocoin.wallet.pages.wallet

import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.pages.wallet.enum.WalletGuideType
import com.aocoin.wallet.utils.checkFastClick
import com.aocoin.wallet.R
import kotlinx.android.synthetic.main.include_primary_button.*
import kotlinx.android.synthetic.main.include_secondary_button.*


/**
 * @FileName WalletGuideFragment
 * @Description
 * @Author dingyan
 * @Date 2020-01-07 10:29
 */
class WalletGuideFragment : BaseFragment() {

    override fun getUi(): Int = R.layout.fragment_wallet_guide

    override fun initView() {
        super.initView()

        initButton()
    }

    private fun initButton() {
        primary_btn_content_tv?.text = getString(R.string.title_create_wallet)
        secondary_btn_content_tv.text = getString(R.string.title_import_wallet)
    }

    override fun initListener() {
        super.initListener()

        primary_btn?.setOnClickListener {
            WalletGuideType.Create.startWalletChainPage()
        }

        secondary_btn?.setOnClickListener {
            WalletGuideType.Import.startWalletChainPage()
        }
    }

    private fun WalletGuideType.startWalletChainPage() {
        mContext.checkFastClick {
            start(WalletChainFragment.newInstance(guideType = this))
        }
    }
}