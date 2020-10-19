package com.aocoin.wallet.pages.common.type

/**
 * @FileName GroupItem
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 2:21 PM
 */
class GroupItem(
        val icon: Int,
        val title: String,
        val sectionTitle: String = "",
        val onClockListener: () -> Unit)