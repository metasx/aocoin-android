package com.aocoin.wallet.base

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aocoin.wallet.utils.getLoadingTip
import com.qmuiteam.qmui.widget.QMUITopBar
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import me.yokeyword.fragmentation.SupportFragment


/**
 * @FileName BaseFragment
 * @Description 基础Fragment
 * @Author dingyan
 * @Date 2018/4/19 12:04
 */
abstract class BaseFragment : SupportFragment(), IBaseInitImpl {

    lateinit var mContext: Context

    var mLoadingDialog: QMUITipDialog? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mContext = activity!!
        val attachToRoot = false
        return inflater.inflate(getUi(), container, attachToRoot)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        initAdapter()
        initListener()
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        super.onEnterAnimationEnd(savedInstanceState)

        loadData()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)

            // 在配置变化的时候将这个fragment保存下来
            retainInstance = true
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        hideSoftInput()
    }

    /**
     * 设置返回标题栏
     *
     * @param title 标题
     * @param isBack 是否显示返回键
     */
    fun QMUITopBar.setTopBar(title: String = "", isBack: Boolean = false) {

        val titleView = this.setTitle(title)

        // 设置文字过多，尾部省略
        titleView.setEllipsize(TextUtils.TruncateAt.END)

        if (isBack) {
            this.addLeftBackImageButton().setOnClickListener {
                pop()
            }
        }
    }

    abstract fun getUi(): Int

    override fun initData() {}

    override fun initView() {
        mLoadingDialog = mContext.getLoadingTip()

        try {
            initKeyboard()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 关闭加载框
    fun dismissLoadingDialog() {
        if (isVisible && mLoadingDialog?.isShowing == true) {
            mLoadingDialog?.dismiss()
        }
    }

    open fun initKeyboard() {}

    override fun initAdapter() {}

    override fun initListener() {}

    override fun loadData() {}

}