package com.aocoin.wallet.pages.wallet.multitype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aocoin.wallet.pages.wallet.enum.WalletPageName
import com.aocoin.wallet.pages.wallet.type.WalletKindItem
import com.aocoin.wallet.pages.wallet.utils.check
import com.aocoin.wallet.R
import com.aocoin.wallet.db.WalletLocalType
import kotlinx.android.synthetic.main.item_wallet_kind.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * @FileName WalletKindBinder
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 5:55 PM
 */
class WalletKindBinder(
    private val pageName: WalletPageName,
    private val onItemSelectedListener: (WalletKindItem) -> Unit) : ItemViewBinder<WalletKindItem, WalletKindBinder.ViewHolder>() {

    private var mLocalType = WalletLocalType.LOCAL_TYPE_POK

    fun setSelectedType(localType: Int) {
        this.mLocalType = localType
        adapter.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_wallet_kind, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: WalletKindItem) {
        with(holder.itemView) {

            icon_iv?.setImageResource(
                    if (mLocalType == item.localType)
                        item.selectedIcon
                    else
                        item.unselectedIcon
            )

            item_layout?.setBgColor(item.localType)

            item_layout?.setOnClickListener {
                setSelectedType(item.localType)

                onItemSelectedListener.invoke(item)
            }
        }
    }

    private fun View.setBgColor(type: Int) {
        pageName.check(
                isSwitch = {
                    this.setBackgroundResource(if (mLocalType == type) {
                        R.color.wallet_switch_kind_item_selected_bg
                    } else {
                        R.color.wallet_switch_kind_item_unselected_bg
                    })
                },

                isManagement = {
                    this.setBackgroundResource(
                            if (mLocalType == type) {
                                R.color.wallet_management_kind_item_selected_bg
                            } else {
                                R.color.wallet_management_kind_item_unselected_bg
                            }
                    )
                })
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}