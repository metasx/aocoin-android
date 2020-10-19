package com.aocoin.wallet.pages.common.multitype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.android.aocoin.core.pages.common.type.GroupSectionItem
import com.aocoin.wallet.R
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import kotlinx.android.synthetic.main.item_group_section.view.*
import me.drakeet.multitype.ItemViewBinder

/**
 * @FileName GroupSectionItemBinder
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 6:16 PM
 */
class GroupSectionItemBinder : ItemViewBinder<GroupSectionItem, GroupSectionItemBinder.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_group_section, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: GroupSectionItem) {
        with(holder.itemView) {
            title_tv?.text = item.title

            val lp = RelativeLayout.LayoutParams(title_tv.layoutParams)
            lp.leftMargin = QMUIDisplayHelper.dpToPx(item.startMargin)
            lp.topMargin = QMUIDisplayHelper.dpToPx(item.topMargin)
            lp.bottomMargin = QMUIDisplayHelper.dpToPx(item.bottomMargin)
            title_tv.layoutParams = lp
        }
    }
}