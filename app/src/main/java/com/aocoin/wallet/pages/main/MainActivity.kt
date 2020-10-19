package com.aocoin.wallet.pages.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseActivity
import com.aocoin.wallet.pages.assets.AssetsFragment

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initRootFragment()
    }

    private fun initRootFragment() {
        val targetFragment = AssetsFragment()
        if (findFragment(targetFragment.javaClass) == null) {
            loadRootFragment(R.id.frame_layout, targetFragment)
        }
    }

}
