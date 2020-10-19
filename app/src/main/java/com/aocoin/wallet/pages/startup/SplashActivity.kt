package com.aocoin.wallet.pages.startup

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.aocoin.wallet.pages.main.MainActivity
import com.aocoin.wallet.pages.wallet.WalletGuideActivity
import com.aocoin.wallet.utils.SharedPrefKt
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseActivity
import kotlinx.android.synthetic.main.activity_splash.*

/**
 * @FileName: SplashActivity
 * @Description: 启动页
 * @Author: haoyanhui
 * @Date: 2020-06-25 12:52
 */
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun initView() {
        super.initView()
        startAnimation()
    }

    private fun startAnimation() {
        lottie_animation_view_logo?.apply {
            setAnimation("animation_splash.json")
            imageAssetsFolder = "images/"
            speed = 0.3f
            addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    // 动画结束打开页面
                    openNextPageBySetting()
                }
            })
        }
    }

    private fun Activity.openNextPageBySetting() {
        when {
            SharedPrefKt.getCurrentWalletAddress().isNullOrEmpty() -> {
                // 未创建/导入钱包，则打开创建/导入钱包选择页
                startActivity(Intent(this, WalletGuideActivity::class.java))
            }
            else -> {
                // 非以上情况，则进入 App
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        finish()
    }
}