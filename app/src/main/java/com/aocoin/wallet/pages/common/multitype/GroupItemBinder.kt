package com.aocoin.wallet.pages.common.multitype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aocoin.wallet.R
import com.aocoin.wallet.pages.common.type.GroupItem
import kotlinx.android.synthetic.main.item_group.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * @FileName GroupItemBinder
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 2:23 PM
 */
class GroupItemBinder : ItemViewBinder<GroupItem, GroupItemBinder.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_group, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: GroupItem) {
        with(holder.itemView) {
            icon_iv?.setImageResource(item.icon)
            title_tv?.text = item.title
            item_layout?.setOnClickListener {
                item.onClockListener.invoke()
            }
        }
    }
}