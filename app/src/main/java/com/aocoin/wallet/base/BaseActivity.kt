package com.aocoin.wallet.base

import android.content.Context
import android.os.Bundle
import com.aocoin.wallet.utils.getCustomLoadingTip
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import me.yokeyword.fragmentation.SupportActivity


/**
 * @FileName BaseActivity
 * @Description 基础Activity
 * @Author dingyan
 * @Date 2018/4/19 11:51
 */
abstract class BaseActivity : SupportActivity(), IBaseInitImpl {

    // 自定义动画加载框
    var loadingDialog: QMUITipDialog? = null

    lateinit var mContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        mContext = this

        // 沉浸式状态栏
        QMUIStatusBarHelper.translucent(this)
        super.onCreate(savedInstanceState)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        initData()
        initView()
        initAdapter()
        initListener()
        loadData()
    }

    override fun initData() {}

    override fun initView() {
        loadingDialog = this.getCustomLoadingTip()
    }

    // 加载框消失
    fun dismissLoadingDialog() {
        if (!isFinishing && loadingDialog?.isShowing == true) {
            loadingDialog?.dismiss()
        }
    }

    override fun initAdapter() {}

    override fun initListener() {}

    override fun loadData() {}
}
