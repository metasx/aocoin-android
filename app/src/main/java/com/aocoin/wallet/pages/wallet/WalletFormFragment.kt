package com.aocoin.wallet.pages.wallet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.pages.main.MainActivity
import com.aocoin.wallet.pages.wallet.enum.WalletGuideType
import com.aocoin.wallet.pages.wallet.enum.WalletImportType
import com.aocoin.wallet.pages.wallet.utils.checkWalletExistsByPrivateKey
import com.aocoin.wallet.service.walletacount.WalletAccountManager
import com.aocoin.wallet.R
import com.aocoin.wallet.db.Database
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.utils.*
import kotlinx.android.synthetic.main.fragment_wallet_info_form.*
import kotlinx.android.synthetic.main.include_input_notice_layout.*
import kotlinx.android.synthetic.main.include_secondary_button.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletInfoFormFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/13 3:29 PM
 */
class WalletFormFragment : BaseFragment() {

    /**
     * 引导类型
     */
    private var mGuideType = WalletGuideType.Create

    /**
     * 钱包导入类型
     */
    private var mImportType: WalletImportType? = null

    /**
     *  钱包底层
     */
    private var mWalletLocalType = WalletLocalType.LOCAL_TYPE_POK

    /**
     * 钱包助记词
     */
    private var mWalletMnemonic = ""

    /**
     *  钱包私钥
     */
    private var mWalletPrivateKey = ""

    /**
     * 钱包密码
     */
    private var mWalletPassword = ""

    /**
     * 钱包密钥
     */
    private var mWalletKeyStore = ""

    /**
     * 钱包地址
     */
    private var mWalletAddress = ""

    /**
     * 钱包名称
     */
    private var mName = {
        name_et?.text.toString()
    }

    /**
     * 钱包密码
     */
    private var mPassword = {
        password_et?.text.toString()
    }

    /**
     * 确认钱包密码
     */
    private var mConfirmPassWord = {
        confirm_password_et?.text.toString()
    }

    companion object {
        fun newInstance(
            guideType: WalletGuideType = WalletGuideType.Create,
            importType: WalletImportType? = null,
            walletLocalType: Int,
            mnemonic: String = "",
            privateKey: String = "",
            password: String = "",
            address: String = "",
            keyStore: String = ""
        ): WalletFormFragment {
            val fragment = WalletFormFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.PARAM_WALLET_GUIDE_TYPE, guideType)
            bundle.putSerializable(Constants.PARAM_WALLET_IMPORT_TYPE, importType)
            bundle.putInt(Constants.PARAM_WALLET_LOCAL_TYPE, walletLocalType)
            bundle.putString(Constants.PARAM_WALLET_MNEMONIC, mnemonic)
            bundle.putString(Constants.PARAM_WALLET_PRIVATE_KEY, privateKey)
            bundle.putString(Constants.PARAM_WALLET_PASSWORD, password)
            bundle.putString(Constants.PARAM_WALLET_ADDRESS, address)
            bundle.putString(Constants.PARAM_WALLET_KEY_STORE, keyStore)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getUi(): Int = R.layout.fragment_wallet_info_form

    override fun initData() {
        super.initData()

        initArgumentData()
    }

    private fun initArgumentData() {
        mGuideType =
            arguments!!.getSerializable(Constants.PARAM_WALLET_GUIDE_TYPE) as WalletGuideType
        mWalletLocalType = arguments!!.getInt(Constants.PARAM_WALLET_LOCAL_TYPE)

        if (mGuideType == WalletGuideType.Import) {
            mImportType =
                arguments!!.getSerializable(Constants.PARAM_WALLET_IMPORT_TYPE) as WalletImportType?
            mWalletMnemonic = arguments!!.getString(Constants.PARAM_WALLET_MNEMONIC) ?: ""
            mWalletPrivateKey = arguments!!.getString(Constants.PARAM_WALLET_PRIVATE_KEY) ?: ""
            mWalletPassword = arguments!!.getString(Constants.PARAM_WALLET_PASSWORD) ?: ""
            mWalletKeyStore = arguments!!.getString(Constants.PARAM_WALLET_KEY_STORE) ?: ""
            mWalletAddress = arguments!!.getString(Constants.PARAM_WALLET_ADDRESS) ?: ""
        }
    }

    override fun initView() {
        super.initView()

        initTopBar()
        initButton()
        initVisibility()
    }

    private fun initTopBar() {
        val title = if (mGuideType == WalletGuideType.Create) {
            getString(R.string.title_create_wallet)
        } else {
            when (mImportType) {
                WalletImportType.Mnemonic -> getString(R.string.title_mnemonic_import)
                WalletImportType.KeyStore -> getString(R.string.title_key_store_import)
                WalletImportType.PrivateKey -> getString(R.string.title_private_key_import)
                else -> getString(R.string.title_import_wallet)
            }
        }

        topbar?.setTopBar(title = title, isBack = true)
    }

    private fun initButton() {
        secondary_btn_content_tv?.text = getString(R.string.next_btn)
    }

    private fun initVisibility() {
        if (importTypeIsKeyStore()) {
            password_et?.setText(mWalletPassword)

            password_et_layout?.visibility = View.GONE
            confirm_password_et_layout?.visibility = View.GONE
        } else {
            password_et_layout?.visibility = View.VISIBLE
            confirm_password_et_layout?.visibility = View.VISIBLE
        }
    }

    private fun importTypeIsKeyStore(): Boolean {
        return mGuideType == WalletGuideType.Import && mImportType == WalletImportType.KeyStore
    }

    override fun initListener() {
        super.initListener()

        initFocusListener()
        initTextChangedListener()

        // 下一步
        secondary_btn?.setOnClickListener {
            formValidator {
                mContext.checkFastClick {
                    when (mGuideType) {
                        WalletGuideType.Create -> {
                            getMnemonic()
                        }

                        WalletGuideType.Import -> {
                            importWallet()
                        }
                    }
                }
            }
        }
    }

    private fun initFocusListener() {
        // 名称
        name_et?.setOnFocusChangeListener { _, _ ->
            input_notice_layout?.visibility = GONE
        }

        // 新密码
        password_et?.setOnFocusChangeListener { _, _ ->
            input_notice_layout?.visibility =
                if (importTypeIsKeyStore()) GONE else VISIBLE
        }

        // 确认新密码
        confirm_password_et?.setOnFocusChangeListener { _, _ ->
            input_notice_layout?.visibility = GONE
        }
    }

    private fun initTextChangedListener() {
        password_et?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val content = s.toString()

                val matchPointBg = ContextCompat.getDrawable(mContext, R.drawable.round_dark)
                val pointBg = ContextCompat.getDrawable(mContext, R.drawable.round_red)

                val matchTextColor = ContextCompat.getColor(mContext, R.color.color_text_grey)
                val textColor = ContextCompat.getColor(mContext, R.color.colorAccent)

                if (content.checkUppercase()) {
                    notice_uppercase_point_tv?.background = matchPointBg
                    notice_uppercase_tv?.setTextColor(matchTextColor)
                } else {
                    notice_uppercase_point_tv?.background = pointBg
                    notice_uppercase_tv?.setTextColor(textColor)
                }

                if (content.checkLowercase()) {
                    notice_lowercase_point_tv?.background = matchPointBg
                    notice_lowercase_tv?.setTextColor(matchTextColor)
                } else {
                    notice_lowercase_point_tv?.background = pointBg
                    notice_lowercase_tv?.setTextColor(textColor)
                }

                if (content.checkNumber()) {
                    notice_number_point_tv?.background = matchPointBg
                    notice_number_tv?.setTextColor(matchTextColor)
                } else {
                    notice_number_point_tv?.background = pointBg
                    notice_number_tv?.setTextColor(textColor)
                }

                if (content.length >= 8) {
                    notice_length_point_tv?.background = matchPointBg
                    notice_length_tv?.setTextColor(matchTextColor)
                } else {
                    notice_length_point_tv?.background = pointBg
                    notice_length_tv?.setTextColor(textColor)
                }
            }
        })
    }

    /**
     * 表单校验
     */
    private fun formValidator(onCompleteListener: () -> Unit) {

        when {
            mName().isEmpty() -> {
                mContext.showFailedTip(getString(R.string.wallet_name_empty_tip))
                return
            }

            !importTypeIsKeyStore()
                    && !mPassword().checkInputPassword() -> {
                mContext.showFailedTip(getString(R.string.wallet_password_error_tip))
                return
            }

            !importTypeIsKeyStore()
                    && mPassword() != mConfirmPassWord() -> {
                mContext.showFailedTip(getString(R.string.wallet_two_passwords_error_tip))
                return
            }
        }

        onCompleteListener.invoke()
    }

    override fun getContext(): Context = mContext

    private val mWalletAccountManager = { localType: Int ->
        WalletAccountManager(localType)
    }

    /**
     * 获取助记词
     */
    private fun getMnemonic() {
        mWalletAccountManager(mWalletLocalType).generateMnemonic(
            onSuccess = { mnemonic ->
                if (mnemonic.isEmpty()) {
                    mContext.showFailedTip(getString(R.string.wallet_get_mnemonic_error))
                } else {
                    mWalletMnemonic = mnemonic

                    getWallet()
                }
            },
            onFail = { msg ->
                mContext.showFailedTip(msg)
            })
    }

    private fun getWallet() {
        mWalletAccountManager(mWalletLocalType).createWalletByMnemonic(
            mnemonic = mWalletMnemonic,
            password = mPassword(),
            onSuccess = { address, privateKey, keystore ->
                mWalletAddress = address
                mWalletPrivateKey = privateKey
                mWalletKeyStore = keystore

                startCreateNotice()
            },
            onFail = { msg ->
                mContext.showFailedTip(msg)
            }
        )
    }

    private fun startCreateNotice() {
        start(
            WalletCreateNoticeFragment.newInstance(
                walletLocalType = mWalletLocalType,
                mnemonic = mWalletMnemonic,
                privateKey = mWalletPrivateKey,
                keystore = mWalletKeyStore,
                address = mWalletAddress,
                name = mName(),
                password = mPassword()
            )
        )
    }

    private fun importWallet() {
        SharedPrefKt.saveCurrentWalletAddressAndLocalType(
            address = mWalletAddress,
            localType = mWalletLocalType
        )

        mContext.checkWalletExistsByPrivateKey(
            privateKey = mWalletPrivateKey,
            localType = mWalletLocalType,
            onSuccess = {
                // 助记词、私钥导入，拿到密码再次生成钱包，保证 keystore 有效
                when (mImportType) {
                    WalletImportType.Mnemonic -> {
                        mWalletAccountManager(mWalletLocalType)
                            .createWalletByMnemonic(mnemonic = mWalletMnemonic,
                                password = mPassword(),
                                onSuccess = { address, privateKey, keystore ->
                                    saveWallet(keystore)
                                }, onFail = {

                                })
                    }
                    WalletImportType.PrivateKey -> {
                        mWalletAccountManager(mWalletLocalType)
                            .createWalletByPrivateKey(privateKey = mWalletPrivateKey,
                                password = mPassword(),
                                onSuccess = { address, keystore ->
                                    saveWallet(keystore)
                                },
                                onFail = {

                                })
                    }
                    WalletImportType.KeyStore -> {
                        saveWallet(mWalletKeyStore)
                    }
                }
            },
            onFailed = { msg ->
                mContext.showFailedTip(msg)
            }
        )
    }

    private fun saveWallet(keyStore: String) {
        val wallet = Wallet()
        wallet.address = mWalletAddress
        wallet.localType = mWalletLocalType
        wallet.name = mName()
        wallet.password = mPassword()
        wallet.privateKey = mWalletPrivateKey
        wallet.mnemonic = mWalletMnemonic
        wallet.keystore = keyStore
        Database.insertMyWallet(wallet)

        EventUtil.walletImportSuccess()

        val intent = Intent(mContext, MainActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

}