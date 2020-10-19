package com.aocoin.wallet.pages.assets.multitype

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aocoin.wallet.R
import com.aocoin.wallet.db.Coin
import com.aocoin.wallet.utils.Utils
import kotlinx.android.synthetic.main.item_coin.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * @FileName CoinBinder
 * @Description
 * @Author dingyan
 * @Date 2019-12-27 14:14
 */
class CoinBinder(val onItemClickListener: (coin: Coin) -> Unit) : ItemViewBinder<Coin, CoinBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_coin, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Coin) {
        with(holder.itemView) {
            val balance = Utils.format(4, item.balance)
            name_tv?.text = item.abbr
            quantity_tv?.text = balance

            rl_item.setOnClickListener {
                onItemClickListener(item)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}