package com.aocoin.wallet.pages.trade

import android.os.Bundle
import android.view.View
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseActivity
import com.aocoin.wallet.db.WalletLocalType
import com.aocoin.wallet.pages.trade.enum.TransferState
import com.aocoin.wallet.service.polkaLoadBlockByHash
import com.aocoin.wallet.service.polkaLoadLastBlock
import com.aocoin.wallet.utils.EventUtil
import com.aocoin.wallet.utils.Logger
import com.aocoin.wallet.utils.SharedPrefKt
import com.aocoin.wallet.utils.Utils
import kotlinx.android.synthetic.main.activity_transfer_result.*
import kotlinx.android.synthetic.main.include_topbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @FileName: TransferResultActivity
 * @Description: 转账结果页面
 * @Author: haoyanhui
 * @Date: 2020-01-09 00:21
 * 以太坊本地交易记录打开该页面需要传参：决定一打开页面是否显示加速入口（获取nonce同本地记录最后一条对比，只有相等才显示加速按钮）
 */
class TransferResultActivity : BaseActivity() {

    // 波卡需要确认的块数
    private val POLKA_NEED_CONFIRM_BLOCK_COUNT = 2

    // 需要确认的块数
    private var mSuccessBlockCount: Int = POLKA_NEED_CONFIRM_BLOCK_COUNT

    private var mWalletLocalType = WalletLocalType.LOCAL_TYPE_POK

    private var mFinishBlockNumber: String = ""

    private lateinit var mTxId: String
    private lateinit var mTimeStamp: String
    private lateinit var mSendAddress: String
    private lateinit var mReceiveAddress: String
    private lateinit var mAmount: String
    private lateinit var mCoinAbbr: String

    // 波卡转账
    // 结果状态，false 时直接显示状态，true 时需要确认块之后再显示成功状态
    private var mPolkaState = false

    // 块 hash
    private var mPolkaBlockHash: String? = ""

    // 请求数据的 job
    private var mRequestJob: Job? = null

    // 转账结果
    private lateinit var mTransferState: TransferState

    // 成功块数
    private var mSuccessBlockNumber = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer_result)
    }

    override fun initData() {
        super.initData()
        mTxId = intent.getStringExtra("tx_id")!!
        mTimeStamp = intent.getStringExtra("timestamp")!!
        mAmount = intent.getStringExtra("amount")!!
        mCoinAbbr = intent.getStringExtra("coin_abbr")!!
        mSendAddress = intent.getStringExtra("send_address")!!
        mReceiveAddress = intent.getStringExtra("receive_address")!!

        mWalletLocalType = SharedPrefKt.getCurrentWalletLocalType()

        showPolkaState()
    }

    override fun loadData() {
        super.loadData()
        polkaLoadResult()
    }

    override fun initView() {
        super.initView()
        topbar.setTitle(R.string.title_transfer_details)
        topbar.addLeftBackImageButton().setOnClickListener {
            finish()
        }

        refreshView()

        tv_transfer_id.text = mTxId
        tv_transfer_time.text = Utils.stampToDate(mTimeStamp)
        tv_send_address.text = mSendAddress
        tv_receive_address.text = mReceiveAddress
        tv_transfer_block.text = "- - -"
        tv_amount.text = mAmount
        tv_coin_name.text = mCoinAbbr

    }

    private fun refreshView() {
        when (mTransferState) {
            TransferState.DOING -> {
                if (rl_loading_result.visibility == View.GONE) {
                    rl_loading_result.visibility = View.VISIBLE
                }
                if (rl_transfer_result.visibility == View.VISIBLE) {
                    rl_transfer_result.visibility = View.GONE
                }
                polkaShowDoingView()
            }
            TransferState.FAIL -> {
                rl_loading_result.visibility = View.GONE
                rl_transfer_result.visibility = View.VISIBLE
                iv_transfer_result.setImageResource(R.mipmap.ic_trade_fail)
                tv_transfer_status.text = getString(R.string.transfer_failed)

            }
            TransferState.SUCCESS -> {
                rl_loading_result.visibility = View.GONE
                rl_transfer_result.visibility = View.VISIBLE
                iv_transfer_result.setImageResource(R.mipmap.ic_trade_success)
                tv_transfer_status.text = getString(R.string.transfer_success)

            }
        }
    }

    // 设置波卡返回的状态
    private fun showPolkaState() {
        mSuccessBlockCount = POLKA_NEED_CONFIRM_BLOCK_COUNT
        mPolkaState = intent.getBooleanExtra("transfer_state", false)
        mPolkaBlockHash = intent.getStringExtra("block_hash")
        mTransferState = TransferState.DOING
    }

    private fun polkaShowDoingView() {
        tv_loading_status.text = getString(R.string.doing)
        tv_desc.text = "$mSuccessBlockNumber/$mSuccessBlockCount"
    }

    private fun polkaLoadResult() {
        // 获取块信息并显示
        mPolkaBlockHash?.let {
            mRequestJob = GlobalScope.launch(Dispatchers.IO) {
                polkaLoadBlockByHash(it, onSuccess = { blockNum ->
                    showBlockInfo(blockNum)
                    // false 直接显示失败状态；true 去确认区块
                    if (mPolkaState) {
                        loadPolkaLastBlock()
                    } else {
                        showResultState(TransferState.FAIL)
                    }
                }, onFail = {
                    // 失败了，重新请求
                    reloadPolkaBlockByHash()
                })
            }
        }
    }

    // 显示结果
    private fun showResultState(state: TransferState) {
        dealPolkaResult(state)
    }

    private fun reloadPolkaBlockByHash() {
        polkaLoadResult()
    }

    private fun loadPolkaLastBlock() {
        mRequestJob = GlobalScope.launch(Dispatchers.IO) {
            // 等待 3 秒再请求
            Thread.sleep(3000)
            polkaLoadLastBlock(onSuccess = {
                val finishedCount =
                    it.toBigInteger().subtract(mFinishBlockNumber.toBigInteger()).toInt()
                if (finishedCount >= mSuccessBlockCount) {
                    showResultState(TransferState.SUCCESS)
                } else {
                    showDealBlockInfo(if (finishedCount < 0) 0 else finishedCount)
                    // 轮询
                    loadPolkaLastBlock()
                }
            }, onFail = {
                // 轮询
                loadPolkaLastBlock()
            })
        }
    }

    private fun showBlockInfo(blockNumber: String) {
        mFinishBlockNumber = blockNumber
        tv_transfer_block.text = blockNumber
    }

    private fun showDealBlockInfo(size: Int) {
        mSuccessBlockNumber = size
        tv_desc.text = "${mSuccessBlockNumber}/$mSuccessBlockCount"
    }

    private fun dealPolkaResult(state: TransferState) {
        mTransferState = state
        if (mTransferState == TransferState.SUCCESS) {
            EventUtil.transferSuccess()
        }
        refreshView()
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("-- 交易结果页 onDestroy ")
        // 取消请求 job
        mRequestJob?.takeIf {
            it.isActive
        }.let {
            Logger.d("-- onDestroy mRequestJob.cancel")
            it?.cancel()
        }
    }

}