package com.aocoin.wallet

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.aocoin.wallet.api.JSApi
import com.aocoin.wallet.utils.SharedPrefUtil
import com.aocoin.wallet.widgets.jsbridge.JsBridgeWebView
import com.raizlabs.android.dbflow.config.FlowManager
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader

/**
 * @FileName: CoreApp
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-10 14:53
 */
class CoreApp : Application() {

    companion object {

        lateinit var context: Context

        lateinit var jsWebView: JsBridgeWebView

        lateinit var instance: CoreApp
    }

    override fun onCreate() {
        super.onCreate()
        context = this.applicationContext
        instance = this
        // 初始化数据库
        initDBFlow()
        SharedPrefUtil.init(this)
        initJSAPIWebView()
        initSmartyRefreshLayout()
    }

    private fun initJSAPIWebView() {
        jsWebView = JsBridgeWebView(context)
        jsWebView.loadUrl(JSApi.WEB_URL)
    }

    private fun initDBFlow() {
        FlowManager.init(this)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun initSmartyRefreshLayout() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context?, layout: RefreshLayout ->
            layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white)
            ClassicsHeader(context)
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context: Context?, layout: RefreshLayout? ->
            ClassicsFooter(
                context
            ).setDrawableSize(20f)
        }
    }

}