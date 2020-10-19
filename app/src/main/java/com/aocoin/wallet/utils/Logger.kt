package com.aocoin.wallet.utils

import android.util.Log
import com.aocoin.wallet.BuildConfig

/**
 * @FileName: Logger
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-10 15:25
 */
class Logger {
    companion object {
        const val TAG = "aocoin_core"

        fun d(msg: String) {
            d(TAG, msg)
        }

        fun d(tag: String, msg: String) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "$tag--$msg")
            }
        }

        fun d(tag: String, e: Exception) {
            if (BuildConfig.DEBUG) {
                d(tag)
                e.printStackTrace()
            }
        }

        fun d(tag: String, t: Throwable) {
            if (BuildConfig.DEBUG) {
                d(tag)
                t.printStackTrace()
            }
        }

        fun i(msg: String) {
            i(TAG, msg)
        }

        fun i(tag: String, msg: String) {
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "$tag--$msg")
            }
        }

        fun e(msg: String) {
            e(TAG, msg)
        }

        fun e(tag: String, msg: String) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, "$tag--$msg")
            }
        }
    }
}