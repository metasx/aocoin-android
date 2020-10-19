package com.aocoin.wallet.pages.wallet

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.aocoin.wallet.event.AppEvent
import com.aocoin.wallet.pages.wallet.enum.WalletPageName
import com.aocoin.wallet.utils.checkFastClick
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseDialogFragment
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import kotlinx.android.synthetic.main.fragment_wallet_switch.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * @FileName WalletSwitchFragment
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 5:45 PM
 */
class WalletSwitchFragment : BaseDialogFragment() {
    override fun getUi() = R.layout.fragment_wallet_switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mDialog = Dialog(mContext)
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.setContentView(getUi())

        val window = mDialog.window
        window?.setWindowAnimations(R.style.BottomDialog)
        if (window != null) {
            val layoutParams: WindowManager.LayoutParams = window.attributes
            layoutParams.gravity = Gravity.BOTTOM
            layoutParams.height = QMUIDisplayHelper.dpToPx(489)
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
            window.attributes = layoutParams
        }
        return mDialog
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun initView() {
        super.initView()

        initTopBar()

        initRootFragment()
    }

    private fun initTopBar() {
        val titleTv = topbar?.setTitle(mContext.getString(R.string.wallet_switch))
        titleTv?.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark))
        topbar?.setBackgroundResource(R.drawable.topbar_round_bg)

        topbar?.addRightImageButton(
                R.mipmap.ic_black_close,
                R.id.topbar_right_close_iv)
                ?.setOnClickListener {
                    dialog?.dismiss()
                }
    }

    private fun Int.updateManagementIcon() {
        addManagementIcon()
    }

    private fun cleanManagementIcon() {
        topbar?.removeAllLeftViews()
    }

    private fun addManagementIcon() {
        cleanManagementIcon()
        topbar?.addLeftImageButton(
                R.mipmap.ic_wallet_manager,
                R.id.topbar_left_set_iv)
                ?.setOnClickListener {
                    startManagementPage()
                }
    }

    private fun initRootFragment() {
        val targetFragment = WalletFragment.newInstance(pageName = WalletPageName.Switch)
        loadRootFragment(R.id.frame_layout, targetFragment)
    }

    override fun initListener() {}

    /**
     * 打开管理页
     */
    private fun startManagementPage() {
        mContext.checkFastClick {
            val intent = Intent(activity, WalletManagementActivity::class.java)
            activity?.startActivity(intent)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AppEvent) {

        when (event.type) {
            // 创建 / 导入钱包成功
            AppEvent.Type.WALLET_CREATE_SUCCESS,
            AppEvent.Type.WALLET_IMPORT_SUCCESS -> {
                // 关闭Dialog
                dialog?.dismiss()
            }

            // 更新管理Icon可见状态
            AppEvent.Type.UPDATE_WALLET_MANAGEMENT_ICON -> {
                val localType: Int = event.msg!!.toInt()
                localType.updateManagementIcon()
            }
        }
    }
}