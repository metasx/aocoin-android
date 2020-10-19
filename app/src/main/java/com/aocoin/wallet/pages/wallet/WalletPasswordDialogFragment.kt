package com.aocoin.wallet.pages.wallet

import android.view.ViewGroup
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseDialogFragment
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.pages.wallet.utils.checkPassword
import kotlinx.android.synthetic.main.dialog_fragment_wallet_password.*
import kotlinx.android.synthetic.main.include_dialog_fragment_double_action.*

/**
 * @FileName WalletPasswordDialogFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/15 10:33 AM
 */
class WalletPasswordDialogFragment(
    private val wallet: Wallet?,
    private val onSuccessful: () -> Unit,
    private val onFailed: () -> Unit = {},
    private val onCancel: () -> Unit = {}) : BaseDialogFragment() {

    private val mInputContent = {
        password_et?.text.toString()
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog
        if (dialog != null) {
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)

            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            val window = dialog.window
            window?.setLayout(width, height)
        }
    }

    override fun getUi(): Int = R.layout.dialog_fragment_wallet_password

    override fun initListener() {

        negative_tv?.setOnClickListener {
            onCancel.invoke()

            dismiss()
        }

        positive_tv?.setOnClickListener {
            wallet?.let {
                if (mInputContent().checkPassword(mContext, it)) {
                    onSuccessful.invoke()
                } else {
                    onFailed.invoke()
                }
                dismiss()
            }
        }
    }

}