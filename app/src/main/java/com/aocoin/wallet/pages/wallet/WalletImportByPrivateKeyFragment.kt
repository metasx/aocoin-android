package com.aocoin.wallet.pages.wallet

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.aocoin.wallet.service.walletacount.WalletAccountManager
import com.aocoin.wallet.utils.PermissionUtil
import com.aocoin.wallet.utils.replaceBlank
import com.aocoin.wallet.utils.showFailedTip
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.pages.common.ScanActivity
import com.aocoin.wallet.pages.wallet.enum.WalletGuideType
import com.aocoin.wallet.pages.wallet.enum.WalletImportType
import com.aocoin.wallet.utils.Constants
import kotlinx.android.synthetic.main.fragment_wallet_import_by_private_key.*
import kotlinx.android.synthetic.main.include_secondary_button.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletImportByPrivateKeyFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 3:03 PM
 */
class WalletImportByPrivateKeyFragment : BaseFragment() {

    private val REQUEST_CODE_SCAN_QRCODE = 1

    private var mWalletLocalType = WalletLocalType.LOCAL_TYPE_POK

    private var mWalletAddress = ""
    private var mWalletKeystore = ""

    private val mInputContent = {
        var inputContent = input_et?.text?.toString()
        if (inputContent != null) {
            inputContent = inputContent.replaceBlank()
        }
        inputContent ?: ""
    }

    companion object {
        fun newInstance(walletLocalType: Int): WalletImportByPrivateKeyFragment {
            val fragment = WalletImportByPrivateKeyFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.PARAM_WALLET_LOCAL_TYPE, walletLocalType)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getUi(): Int = R.layout.fragment_wallet_import_by_private_key

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
        initButton()
    }

    private fun initTopBar() {
        topbar?.setTopBar(title = getString(R.string.title_private_key_import), isBack = true)
        topbar?.addRightTextButton(getString(R.string.qr_code_scan_title), R.id.topbar_right_scan_btn)
                ?.setOnClickListener {
                    PermissionUtil.requestCameraPermission(
                            context = mContext,
                            fragmentManager = childFragmentManager,
                            onGranted = { startScanPage() }
                    )
                }
    }

    private fun initButton() {
        secondary_btn_content_tv?.text = getString(R.string.next_btn)
    }

    override fun initListener() {
        super.initListener()

        secondary_btn?.setOnClickListener {
            var inputContent = input_et?.text?.toString()
            if (inputContent != null) {
                inputContent = inputContent.replaceBlank()
            }
            inputContent.validatePrivateKey()
        }
    }

    private fun String?.validatePrivateKey() {
        if (!this.isNullOrEmpty()) {
            WalletAccountManager(mWalletLocalType)
                    .createWalletByPrivateKey(
                            privateKey = this,
                            password = "",
                            onSuccess = { address, keystore ->
                                mWalletAddress = address
                                mWalletKeystore = keystore

                                startWalletForm()
                            },
                            onFail = { msg ->
                                mContext.showFailedTip(msg)
                            }
                    )
        } else {
            mContext.showFailedTip(getString(R.string.wallet_private_key_empty_tip))
        }
    }

    private fun startWalletForm() {
        start(
            WalletFormFragment.newInstance(
                guideType = WalletGuideType.Import,
                importType = WalletImportType.PrivateKey,
                walletLocalType = mWalletLocalType,
                privateKey = mInputContent(),
                keyStore = mWalletKeystore,
                address = mWalletAddress))
    }

    private fun startScanPage() {
        startActivityForResult(Intent(activity, ScanActivity::class.java), REQUEST_CODE_SCAN_QRCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            val qrCode = data?.getStringExtra(Constants.PARAM_QR_CODE) ?: ""
            input_et?.setText(qrCode)
        }
    }

}