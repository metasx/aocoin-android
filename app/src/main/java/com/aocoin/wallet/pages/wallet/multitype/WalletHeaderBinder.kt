package com.aocoin.wallet.pages.wallet.multitype

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aocoin.wallet.pages.wallet.type.WalletHeaderItem
import com.aocoin.wallet.pages.wallet.utils.check
import com.aocoin.wallet.R
import com.aocoin.wallet.pages.wallet.enum.WalletPageName
import com.qmuiteam.qmui.util.QMUIViewHelper
import kotlinx.android.synthetic.main.item_wallet_header.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * @FileName WalletHeaderBinder
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 6:25 PM
 */
class WalletHeaderBinder(
    private val pageName: WalletPageName,
    private val onManagerClickListener: () -> Unit) : ItemViewBinder<WalletHeaderItem, WalletHeaderBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_wallet_header, parent, false))
    }

    override fun onBindViewHolder(holdr: ViewHolder, item: WalletHeaderItem) {
        with(holdr.itemView) {
            name_tv?.text = item.name

            if (icon_iv != null) {
                // 扩大点击范围
                val expendSize = 50
                QMUIViewHelper.expendTouchArea(icon_iv!!, expendSize)
            }

            icon_iv?.visibility = VISIBLE

            icon_iv?.setOnClickListener {
                onManagerClickListener.invoke()
            }

            name_tv?.setTextColor(name_tv?.context!!)
            item_layout?.setBgColor()
            icon_iv?.setIcon()

        }
    }

    private fun TextView.setTextColor(context: Context) {
        pageName.check(
                isSwitch = {
                    this.setTextColor(ContextCompat
                            .getColor(context, R.color.colorPrimaryDark))
                },

                isManagement = {
                    this.setTextColor(ContextCompat
                            .getColor(context, R.color.qmui_config_color_white))
                })
    }

    private fun View.setBgColor() {
        pageName.check(
                isSwitch = {
                    this.setBackgroundResource(R.color.wallet_switch_header_view_bg)
                },

                isManagement = {
                    this.setBackgroundResource(R.color.wallet_management_header_view_bg)
                })
    }

    private fun ImageView.setIcon() {
        pageName.check(
                isSwitch = {
                    this.setImageResource(R.mipmap.ic_wallet_plus_dark)
                },

                isManagement = {
                    this.setImageResource(R.mipmap.ic_wallet_plus)
                })
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}