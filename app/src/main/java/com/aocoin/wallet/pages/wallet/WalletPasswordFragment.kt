package com.aocoin.wallet.pages.wallet

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import com.aocoin.wallet.service.walletacount.WalletAccountManager
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseFragment
import com.aocoin.wallet.db.Database
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.utils.*
import kotlinx.android.synthetic.main.fragment_wallet_password.*
import kotlinx.android.synthetic.main.include_input_notice_layout.*
import kotlinx.android.synthetic.main.include_topbar.*

/**
 * @FileName WalletPasswordFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/15 10:55 AM
 */

class WalletPasswordFragment : BaseFragment() {
    private var mWallet: Wallet? = null

    private val mOldPassWord = {
        old_password_et?.text.toString()
    }

    private val mNewPassword = {
        new_password_et?.text.toString()
    }

    private val mConfirmPassword = {
        confirm_new_password_et?.text.toString()
    }

    companion object {
        fun newInstance(wallet: Wallet): WalletPasswordFragment {
            val fragment = WalletPasswordFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constants.PARAM_WALLET, wallet)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getUi(): Int = R.layout.fragment_wallet_password

    override fun initData() {
        super.initData()

        initArgumentData()
    }

    private fun initArgumentData() {
        mWallet = arguments!!.getSerializable(Constants.PARAM_WALLET) as Wallet
    }

    override fun initView() {
        super.initView()

        initTopBar()
    }

    private fun initTopBar() {
        topbar?.setTopBar(title = getString(R.string.wallet_update_password), isBack = true)

        topbar?.addRightTextButton(
                getString(R.string.save),
                R.id.topbar_right_save_btn)
                ?.setOnClickListener {
                    updatePassword()
                }
    }

    private fun updatePassword() {
        when {
            mOldPassWord().isNullOrEmpty() -> {
                mContext.showFailedTip(getString(R.string.wallet_old_password_empty_tip))
            }

            mNewPassword().isNullOrEmpty() -> {
                mContext.showFailedTip(getString(R.string.wallet_new_password_empty_tip))
            }

            mConfirmPassword().isNullOrEmpty() -> {
                mContext.showFailedTip(getString(R.string.wallet_confirm_new_password_empty_tip))
            }

            mWallet?.password != mOldPassWord() -> {
                mContext.showFailedTip(getString(R.string.input_old_invalude))
            }

            mNewPassword() != mConfirmPassword() -> {
                mContext.showFailedTip(getString(R.string.input_psw_not_same))
            }

            !mNewPassword().checkInputPassword() -> {
                mContext.showFailedTip(getString(R.string.wallet_password_input_invalude_tip))
            }

            else -> {
                update()
            }
        }
    }

    override fun initListener() {
        super.initListener()

        initFocusListener()
        initTextChangedListener()
    }

    /**
     * 焦点事件
     */
    private fun initFocusListener() {
        // 旧密码
        old_password_et?.setOnFocusChangeListener { v, hasFocus ->
            input_notice_layout?.visibility = View.GONE
        }

        // 新密码
        new_password_et?.setOnFocusChangeListener { v, hasFocus ->
            input_notice_layout?.visibility = View.VISIBLE
        }

        // 确认新密码
        confirm_new_password_et?.setOnFocusChangeListener { v, hasFocus ->
            input_notice_layout?.visibility = View.GONE
        }
    }

    private fun initTextChangedListener() {
        new_password_et?.addTextChangedListener(object : TextWatcher {
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

    private fun update() {
        // 以太坊/波卡钱包重新获取 keystore 成之后保存
        mWallet?.localType?.doNextByLocalType(
                pok = {
                    mWallet?.reloadPolkaKeystore()
                },
                ksm = {
                    mWallet?.reloadPolkaKeystore()
                },
                klp = {
                    mWallet?.reloadPolkaKeystore()
                })
    }

    // 钱包更新保存
    private fun Wallet.saveAfterUpdate() {
        // 更新钱包信息之前更新密码
        mWallet?.password = mNewPassword()
        Database.updateMyWallet(this)
                .let { result ->
                    val tip: String = getString(if (result) R.string.save_success_notice else R.string.save_failed_notice)
                    mContext.showSuccessTip(tip) {
                        if (result) pop()
                    }
                }
    }

    private fun Wallet.reloadEthKeystore() {
        WalletAccountManager(walletLocalType = localType)
                .reloadKeyStore(
                        keystore = keystore,
                        privateKey = this.privateKey,
                        oldPassword = "",
                        newPassword = mNewPassword(),
                        onSuccess = { keystore ->
                            this.keystore = keystore
                            this.saveAfterUpdate()
                        },
                        onFail = { msg ->
                            mContext.showFailedTip(msg)
                        }
                )
    }

    // 重新获取波卡的 keystore
    private fun Wallet.reloadPolkaKeystore() {
        // 助记词、私钥、keystore 取不为空的去获取
        WalletAccountManager(walletLocalType = localType)
                .reloadKeyStore(
                        keystore = this.keystore,
                        privateKey = this.privateKey,
                        oldPassword = this.password,
                        newPassword = mNewPassword(),
                        onSuccess = { keystore ->
                            this.keystore = keystore
                            this.saveAfterUpdate()
                        },
                        onFail = { msg ->
                            mContext.showFailedTip(msg)
                        }
                )
    }
}