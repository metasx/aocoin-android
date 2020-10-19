package com.aocoin.wallet.pages.common

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.aocoin.wallet.utils.Constants
import com.aocoin.wallet.utils.PermissionUtil
import com.aocoin.wallet.utils.showFailedTip
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseActivity
import kotlinx.android.synthetic.main.activity_scan.*
import kotlinx.android.synthetic.main.include_topbar.*
import java.io.File

/**
 * @FileName ScanActivity
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 3:07 PM
 */
class ScanActivity : BaseActivity(), QRCodeView.Delegate {

    private val REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scan)
    }

    override fun onStart() {
        super.onStart()

        // 打开后置摄像头开始预览，但是并未开始识别
        zxing_view?.startCamera()

        // 显示扫描框，并开始识别
        zxing_view?.startSpotAndShowRect()
    }

    override fun onStop() {
        // 关闭摄像头预览，并且隐藏扫描框
        zxing_view?.stopCamera()
        super.onStop()
    }

    override fun onDestroy() {
        // 销毁二维码扫描控件
        zxing_view?.onDestroy()
        super.onDestroy()
    }

    override fun initView() {
        super.initView()

        initTopBar()
    }

    private fun initTopBar() {
        topbar?.setTitle(getString(R.string.qr_code_scan_title))

        topbar?.addLeftBackImageButton()?.setOnClickListener {
            finish()
        }

        topbar?.addRightTextButton(getString(R.string.album), R.id.topbar_right_album_btn)
                ?.setOnClickListener {
                    PermissionUtil.requestStoragePermission(
                            context = mContext,
                            fragmentManager = supportFragmentManager,
                            onGranted = { startPhotoPicker() }
                    )
                }
    }

    private fun startPhotoPicker() {
        val photoPickerIntent = BGAPhotoPickerActivity.IntentBuilder(mContext)
                .cameraFileDir(null)
                .maxChooseCount(1)
                .selectedPhotos(null)
                .pauseOnScroll(false)
                .build()
        startActivityForResult(photoPickerIntent, REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY)
    }

    override fun initListener() {
        super.initListener()

        zxing_view?.setDelegate(this)
    }

    override fun onScanQRCodeSuccess(result: String?) {
        if (result == null || result.isEmpty()) {
            mContext.showFailedTip(getString(R.string.qr_code_scan_failed_tip))

            // 重新开始识别
            zxing_view?.startSpot()
            return
        }

        val intent = Intent()
        intent.putExtra(Constants.PARAM_QR_CODE, result)
        setResult(RESULT_OK, intent)

        finish()
    }

    override fun onCameraAmbientBrightnessChanged(isDark: Boolean) {}

    override fun onScanQRCodeOpenCameraError() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        zxing_view?.showScanRect()
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            val picturePath = BGAPhotoPickerActivity.getSelectedPhotos(data)[0]
            val pictureBitmap = BitmapFactory.decodeFile(File(picturePath).path)
            zxing_view?.decodeQRCode(pictureBitmap)
        }
    }

}