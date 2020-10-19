package com.aocoin.wallet.pages.trade

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.aocoin.wallet.R
import com.aocoin.wallet.base.BaseActivity
import com.aocoin.wallet.db.Coin
import com.aocoin.wallet.db.Database
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.event.AppEvent
import com.aocoin.wallet.pages.wallet.utils.getCurrentWallet
import com.aocoin.wallet.service.polkaIsActivateAddress
import com.aocoin.wallet.service.walletacount.WalletAccountManager
import com.aocoin.wallet.utils.*
import kotlinx.android.synthetic.main.activity_transfer.*
import kotlinx.android.synthetic.main.include_topbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigDecimal

/**
 * @FileName: TransferActivity
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-14 14:37
 */
class TransferActivity : BaseActivity() {

    private lateinit var mWallet: Wallet

    // 当前选中要转账的币种信息
    private var mCurrentCoin: Coin? = null

    private lateinit var mManager: WalletAccountManager

    private var mFeeInfo: String? = ""
    private var mExistentialDeposit: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)
        EventBus.getDefault().register(this)
    }

    override fun initData() {
        super.initData()
        mWallet = getCurrentWallet()
        mManager = WalletAccountManager(mWallet.localType)
        loadCoinList()
        loadFeeInfo()
    }

    override fun initView() {
        super.initView()
        topbar?.setTitle(R.string.transfer_title)
        topbar?.addLeftBackImageButton()?.setOnClickListener {
            finish()
        }
    }

    override fun initListener() {
        super.initListener()
        tv_next?.setOnClickListener {
            if (!inputAmount().isAmountLegal(mContext) || !receiveAddress().isAddressNotEmpty(
                    mContext
                )
            ) {
                return@setOnClickListener
            }
            if (sendAddress().equals(receiveAddress(), true)) {
                mContext.showFailedTip(getString(R.string.input_address_same))
                return@setOnClickListener
            }
            val input = inputAmount().toBigDecimal()
            val balance = mCurrentCoin!!.balance.toBigDecimal()
            // 未获取到手续费信息，无反应
            if (mFeeInfo.isNullOrEmpty()) {
                mContext.showFailedTip("暂未获取到手续费信息")
                return@setOnClickListener
            }
            if (isBalanceEnoughPay(mContext, balance.toDouble(), input.toDouble())
                && isBalanceEnoughPayPolkaFee(balance, input, mFeeInfo ?: "")
            ) {
                val existentialDeposit = mExistentialDeposit!!
                val toAddress = receiveAddress()
                // 校验接收地址
                mManager.checkAddress(toAddress, onSuccess = { isVisible, msg ->
                    if (isVisible) {
                        val inputAmount = inputAmount()
                        // 接收地址是否已激活
                        polkaIsActivateAddress(toAddress, onSuccess = { isActivate ->
                            // 未激活并且转账金额小于指定值则提示
                            if (!isActivate && BigDecimal(inputAmount) < existentialDeposit.toBigDecimal()) {
                                mContext.showFailedTip("${getString(R.string.polka_unactivate_notice)}${existentialDeposit}")
                            } else {
                                val noticeStr = String.format(
                                    getString(R.string.polka_transfer_risk_tips_text),
                                    existentialDeposit
                                )
                                NoticeDialog(riskNotice = noticeStr, onClickConfirm = {
                                    val intent = Intent(this, TransferConfirmActivity::class.java)
                                    intent.putExtra("send_address", sendAddress())
                                    intent.putExtra("wallet_local_type", mWallet.localType)
                                    intent.putExtra("receive_address", receiveAddress())
                                    intent.putExtra("fee", tronFeeInfo())
                                    intent.putExtra("amount", inputAmount)
                                    intent.putExtra("coin_abbr", mCurrentCoin!!.abbr)
                                    intent.putExtra("coin_precision", mCurrentCoin!!.precision)
                                    startActivity(intent)
                                }).show(supportFragmentManager, Constants.TAG_NOTICE_DIALOG)
                            }
                        }, onFail = {
                            mContext.showFailedTip(it)
                        })
                    } else {
                        showWalletAddressInvalid()
                    }
                }, onFail = {
                    showWalletAddressInvalid()
                })
            }
        }
    }

    private fun isBalanceEnoughPay(
        context: Context,
        balance: Double,
        tradeAmount: Double
    ): Boolean {
        if (tradeAmount > balance) {
            context.showFailedTip(context.getString(R.string.input_balance_low))
            return false
        }
        return true
    }

    private fun isBalanceEnoughPayPolkaFee(
        balance: BigDecimal,
        input: BigDecimal,
        fee: String
    ): Boolean {
        if (fee.isEmpty()) {
            mContext.showFailedTip("未获取到矿工费值")
            return false
        }
        if (balance > input.add(BigDecimal(fee))) {
            return true
        }
        mContext.showFailedTip("余额不足以支付矿工费")
        return false
    }

    private fun showWalletAddressInvalid() {
        mContext.showFailedTip(resources.getString(R.string.transfer_address_error))
    }

    private fun loadCoinList() {
        mWallet?.let { wallet ->
            mCurrentCoin = Database.findCoinByAddress(wallet.address, wallet.localType)[0]
        }
        showCoinInfo()
    }

    private fun showCoinInfo() {
        tv_coin_name?.text = mCurrentCoin?.abbr
        tv_balance?.text = mCurrentCoin?.balance
    }

    private fun loadFeeInfo() {
        // 金额精度还原
        val amount = inputAmount()
        val amountStr = BigDecimal(if (amount.isEmpty()) "0" else amount)
            .multiply(BigDecimal.TEN.pow(mCurrentCoin!!.precision)).toBigInteger().toString()
        Logger.d("amountStr = $amountStr")
        mManager.loadFeeInfo(mWallet!!.address,
            amountStr,
            receiveAddress(),
            mCurrentCoin!!.precision,
            onSuccess = { fee, existentialDeposit ->
                mFeeInfo = fee
                mExistentialDeposit = existentialDeposit

                tv_eth_fee?.text = "≈ $mFeeInfo ${mCurrentCoin!!.abbr}"
            },
            onFail = {
                mContext.showFailedTip(it)
            })
    }

    private val inputAmount = {
        et_input_count?.text.toString().trim()
    }

    private val sendAddress = {
        mWallet!!.address
    }

    private val receiveAddress = {
        et_input_recive_address?.text.toString().trim()
    }

    private val tronFeeInfo = {
        tv_eth_fee?.text.toString().trim()
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