package com.aocoin.wallet.pages.wallet.multitype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aocoin.wallet.pages.wallet.type.WalletChainItem
import com.aocoin.wallet.R
import com.aocoin.wallet.db.WalletLocalType
import kotlinx.android.synthetic.main.item_wallet_chain.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * @FileName WalletChainBinder
 * @Description
 * @Author dingyan
 * @Date 2020-01-07 14:28
 */
class WalletChainBinder(private val onCheckedChangedListener: (WalletChainItem) -> Unit)
    : ItemViewBinder<WalletChainItem, WalletChainBinder.ViewHolder>() {

    private var mLocalType = WalletLocalType.LOCAL_TYPE_POK

    private fun setSelectedType(localType: Int) {
        this.mLocalType = localType
        adapter.notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_wallet_chain, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: WalletChainItem) {
        with(holder.itemView) {
            icon_iv?.setImageResource(item.icon)

            name_tv?.text = item.name

            tag_iv?.setImageResource(
                    if (item.localType == mLocalType)
                        R.mipmap.ic_cb_checked
                    else
                        R.mipmap.ic_cb)

            item_layout?.setOnClickListener {
                setSelectedType(item.localType)
                onCheckedChangedListener.invoke(item)
            }
        }
    }
}