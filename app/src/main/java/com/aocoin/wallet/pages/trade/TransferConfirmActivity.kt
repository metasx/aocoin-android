package com.aocoin.wallet.pages.trade

import android.content.Intent
import android.os.Bundle
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseActivity
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.event.AppEvent
import com.aocoin.wallet.pages.wallet.utils.getCurrentWallet
import com.aocoin.wallet.service.polkaSendTransfer
import com.aocoin.wallet.utils.checkFastClick
import com.aocoin.wallet.utils.showFailedTip
import com.aocoin.wallet.utils.showWalletPasswordDialog
import kotlinx.android.synthetic.main.activity_transfer_confirm_new.*
import kotlinx.android.synthetic.main.fragment_wallet_switch.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject

/**
 * @FileName: TransferConfirmActivity
 * @Description: 转账确认页面
 * @Author: haoyanhui
 * @Date: 2020-01-07 21:14
 */
class TransferConfirmActivity : BaseActivity() {

    private lateinit var mWallet: Wallet

    private lateinit var mSendAddress: String
    private lateinit var mReceiveAddress: String
    private lateinit var mAmount: String
    private lateinit var mFee: String
    private lateinit var mCoinAbbr: String
    private var mCoinPrecision: Int = 1
    private var mWalletLocalType = WalletLocalType.LOCAL_TYPE_POK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_confirm_new)
        EventBus.getDefault().register(this)
    }

    override fun initData() {
        super.initData()
        mWallet = getCurrentWallet()

        mSendAddress = intent?.getStringExtra("send_address")!!
        mReceiveAddress = intent?.getStringExtra("receive_address")!!
        mAmount = intent?.getStringExtra("amount")!!
        mCoinAbbr = intent?.getStringExtra("coin_abbr")!!
        mCoinPrecision = intent?.getIntExtra("coin_precision", 1)!!
        mWalletLocalType = intent.getIntExtra("wallet_local_type", WalletLocalType.LOCAL_TYPE_POK)

        mFee = intent?.getStringExtra("fee")!!
    }

    override fun initView() {
        super.initView()
        topbar.setTitle(R.string.confirm_transfer)
        topbar.addLeftBackImageButton().setOnClickListener {
            finish()
        }

        tv_amount.text = mAmount
        tv_coin_name.text = mCoinAbbr
        tv_send_address.text = mSendAddress
        tv_receive_address.text = mReceiveAddress
        tv_fee.text = mFee

    }

    override fun initListener() {
        super.initListener()
        tv_submit.setOnClickListener {
            mContext.checkFastClick {
                // 验证，开始转账流程
                supportFragmentManager.showWalletPasswordDialog(onSuccessful = {
                    startPolkaTransfer(mSendAddress, mWalletLocalType, mReceiveAddress, mAmount,
                        mCoinAbbr, mCoinPrecision)
                })
            }
        }
    }

    private fun startPolkaTransfer(address: String, localType: Int, toAddress: String,
                                   amount: String, coinAbbr: String, coinPrecision: Int) {
        loadingDialog?.show()
        // 转账会广播
        polkaSendTransfer(address = address,
            localType = localType,
            toAddress = toAddress,
            amount = amount,
            coinPrecision = coinPrecision,
            onSuccess = { resultBody ->
                dismissLoadingDialog()
                val bodyObj = JSONObject(resultBody)
                val transferState = bodyObj.optBoolean("result")
                val blockHash = bodyObj.optString("blockHash")
                val txId = bodyObj.optString("hash") ?: "--"
                val errorMsg = bodyObj.optString("errorMsg") ?: "--"
                // 打开交易结果页面，显示交易是否成功
                val intent = Intent(this, TransferResultActivity::class.java)
                intent.putExtra("transfer_state", transferState)
                intent.putExtra("block_hash", blockHash)
                intent.putExtra("error_info", errorMsg)
                intent.putExtra("tx_id", txId)
                intent.putExtra("timestamp", System.currentTimeMillis().toString())
                intent.putExtra("amount", amount)
                intent.putExtra("coin_abbr", coinAbbr)
                intent.putExtra("send_address", address)
                intent.putExtra("receive_address", toAddress)
                startActivity(intent)
            },
            onFail = {
                dismissLoadingDialog()
                showFailedTip(getString(R.string.broadcast_fail) + it)
            })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AppEvent) {
        when (event.type) {
            AppEvent.Type.TRANSFER_SUCCESS -> {
                if (!isFinishing) {
                    finish()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}