package com.aocoin.wallet.pages.wallet.multitype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aocoin.wallet.pages.wallet.type.WalletFeatureItem
import com.aocoin.wallet.R
import kotlinx.android.synthetic.main.item_wallet_feature.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * @FileName WalletFeatureBinder
 * @Description
 * @Author dingyan
 * @Date 2020/10/15 10:48 AM
 */
class WalletFeatureBinder(private val onItemClickListener: (WalletFeatureItem) -> Unit)
    : ItemViewBinder<WalletFeatureItem, WalletFeatureBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_wallet_feature, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: WalletFeatureItem) {
        with(holder.itemView) {
            name_tv?.text = item.name

            item_layout?.setOnClickListener {
                onItemClickListener.invoke(item)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}