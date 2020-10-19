package com.aocoin.wallet.pages.wallet.multitype

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aocoin.wallet.R
import com.aocoin.wallet.db.Wallet
import com.aocoin.wallet.pages.wallet.enum.WalletPageName
import com.aocoin.wallet.pages.wallet.utils.check
import com.aocoin.wallet.pages.wallet.utils.formatAddress
import com.noober.background.drawable.DrawableCreator
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import kotlinx.android.synthetic.main.item_wallet.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * @FileName WalletBinder
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 5:58 PM
 */

class WalletBinder(
    private val pageName: WalletPageName,
    private val onItemClickListener: (Wallet) -> Unit) : ItemViewBinder<Wallet, WalletBinder.ViewHolder>() {

    private var mCurrentWallet: Wallet = Wallet()

    fun setSelectedWallet(wallet: Wallet) {
        this.mCurrentWallet = wallet
        adapter.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_wallet, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, wallet: Wallet) {
        with(holder.itemView) {
            val context = name_tv?.context!!

            name_tv?.text = wallet.name
            address_tv?.text = wallet.address.formatAddress()

            name_tv?.setNameTextColor(context, wallet)
            address_tv?.setAddressTextColor(context, wallet)

            check_mark_iv?.visibility = if (isSelected(wallet)) View.VISIBLE else View.GONE

            item_layout?.setBgDrawable(context, wallet)

            item_layout?.setOnClickListener {
                onItemClickListener.invoke(wallet)
            }

            pageName.check(
                    isSwitch = {
                        more_iv?.visibility = View.GONE
                    },

                    isManagement = {
                        more_iv?.visibility = View.VISIBLE
                    })

        }
    }

    private fun isSelected(wallet: Wallet): Boolean {
        return mCurrentWallet.address == wallet.address && mCurrentWallet.localType == wallet.localType
    }

    private fun View.setBgDrawable(context: Context, wallet: Wallet) {
        val radius = 20F
        val strokeWidth = QMUIDisplayHelper.dp2px(context, 1).toFloat()

        var solidColor = ContextCompat.getColor(context, R.color.wallet_management_wallet_item_bg)
        var strokeColor = ContextCompat.getColor(context, R.color.wallet_management_wallet_item_bg)

        pageName.check(
                isSwitch = {
                    solidColor = ContextCompat
                            .getColor(
                                    context,
                                    if (isSelected(wallet)) {
                                        R.color.wallet_switch_wallet_item_selected_bg
                                    } else {
                                        R.color.wallet_switch_wallet_item_unselected_bg
                                    })

                    strokeColor = ContextCompat
                            .getColor(
                                    context,
                                    if (isSelected(wallet)) {
                                        R.color.wallet_switch_wallet_item_selected_stroke
                                    } else {
                                        R.color.wallet_switch_wallet_item_unselected_stroke
                                    })
                })

        val drawable = DrawableCreator.Builder()
                .setCornersRadius(radius)
                .setSolidColor(solidColor)
                .setStrokeColor(strokeColor)
                .setStrokeWidth(strokeWidth)
                .build()

        this.background = drawable
    }

    private fun TextView.setNameTextColor(context: Context, wallet: Wallet) {

        pageName.check(
                isSwitch = {
                    this.setTextColor(ContextCompat.getColor(context,
                            if (isSelected(wallet)) {
                                R.color.qmui_config_color_white
                            } else {
                                R.color.colorPrimaryDark
                            }
                    ))
                },

                isManagement = {
                    this.setTextColor(ContextCompat
                            .getColor(context, R.color.qmui_config_color_white))
                })
    }

    private fun TextView.setAddressTextColor(context: Context, wallet: Wallet) {

        pageName.check(
                isSwitch = {
                    this.setTextColor(ContextCompat.getColor(context,
                            if (isSelected(wallet)) {
                                R.color.wallet_switch_wallet_item_selected_address_tv_color
                            } else {
                                R.color.wallet_switch_wallet_item_unselected_address_tv_color
                            }
                    ))
                },

                isManagement = {
                    this.setTextColor(ContextCompat
                            .getColor(context,
                                    R.color.wallet_management_wallet_item_selected_address_tv_color))
                })

    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}