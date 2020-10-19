package com.aocoin.wallet.pages.common.dialog

import android.view.View
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseDialogFragment
import kotlinx.android.synthetic.main.dialog_fragment_message.*
import kotlinx.android.synthetic.main.include_dialog_fragment_double_action.*

/**
 * @FileName MessageDialog
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 3:18 PM
 */
class MessageDialog(
    private val style: MessageDialogStyle = MessageDialogStyle.Default,
    private val title: String = "",
    private val message: String,
    private val negative: String? = null,
    private val positive: String? = null,
    private val onPositiveListener: () -> Unit,
    private val onNegativeListener: () -> Unit = {}) : BaseDialogFragment() {

    override fun getUi(): Int = R.layout.dialog_fragment_message

    override fun initView() {
        super.initView()

        title_tv?.text = title
        message_tv?.text = message

        negative_tv?.text = if (negative.isNullOrEmpty()) getString(R.string.button_cancel) else negative
        positive_tv?.text = if (positive.isNullOrEmpty()) getString(R.string.button_ok) else positive

        title_tv?.visibility = if (title.isEmpty()) View.GONE else View.VISIBLE

        initStyle()
    }

    private fun initStyle() {
        when (style) {
            MessageDialogStyle.OntAction -> {
                negative_tv?.visibility = View.GONE
                action_divider_view?.visibility = View.GONE
            }
        }
    }

    override fun initListener() {
        super.initListener()

        positive_tv?.setOnClickListener {
            onPositiveListener.invoke()
            finish()
        }

        negative_tv?.setOnClickListener {
            onNegativeListener.invoke()
            finish()
        }
    }

    private fun finish() {
        if (activity != null
                && !activity!!.isFinishing) {
            dismiss();
        }
    }

}

enum class MessageDialogStyle {
    Default,

    OntAction
}