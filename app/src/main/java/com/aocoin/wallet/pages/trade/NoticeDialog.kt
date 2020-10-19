package com.aocoin.wallet.pages.trade

import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_fragment_notice.*

/**
 * @FileName: NoticeDialog
 * @Description: 提示对话框
 * @Author: haoyanhui
 * @Date: 2020-01-17 17:09
 */
class NoticeDialog(private val riskNotice: String? = null, private val onClickConfirm: () -> Unit) :
    BaseDialogFragment() {

    override fun getUi(): Int = R.layout.dialog_fragment_notice

    override fun initView() {
        super.initView()

        tv_title.text = getString(R.string.transfer_risk_tip)
        tv_content.text = riskNotice
        tv_confirm.text = getString(R.string.transfer_ensure)

    }

    override fun initListener() {
        super.initListener()

        tv_confirm?.setOnClickListener {
            dismiss()
            onClickConfirm()
        }
    }

}