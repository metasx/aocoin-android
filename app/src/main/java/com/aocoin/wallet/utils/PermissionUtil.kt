package com.aocoin.wallet.utils

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.afollestad.assent.Permission.*
import com.afollestad.assent.askForPermissions
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseActivity

/**
 * @FileName PermissionUtil
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 3:10 PM
 */
object PermissionUtil {

    /**
     * 存储权限
     */
    fun requestStoragePermission(context: Context,
                                 fragmentManager: FragmentManager,
                                 onGranted: () -> Unit) {

        val readPermission = READ_EXTERNAL_STORAGE
        val writePermission = WRITE_EXTERNAL_STORAGE

        (context as BaseActivity)
                .askForPermissions(readPermission, writePermission) { result ->
                    if (result.isAllGranted(readPermission, writePermission)) {
                        onGranted.invoke()
                    } else {
                        fragmentManager.showMessagePositiveDialog(
                                title = context.getString(R.string.permission_request_dialog_title),
                                message = context.getString(R.string.storage_permission_request_dialog_message),
                                positive = context.getString(R.string.permission_request_dialog_positive),
                                onPositiveListener = {
                                    context.startSelfSetting()
                                })
                    }
                }
    }

    /**
     * 相机权限
     */
    fun requestCameraPermission(context: Context,
                                fragmentManager: FragmentManager,
                                onGranted: () -> Unit) {

        val permission = CAMERA

        (context as BaseActivity)
                .askForPermissions(permission) { result ->
                    if (result.isAllGranted(permission)) {
                        onGranted.invoke()
                    } else {
                        fragmentManager.showMessagePositiveDialog(
                                title = context.getString(R.string.permission_request_dialog_title),
                                message = context.getString(R.string.camera_permission_request_dialog_message),
                                positive = context.getString(R.string.permission_request_dialog_positive),
                                onPositiveListener = {
                                    context.startSelfSetting()
                                })
                    }
                }
    }

}