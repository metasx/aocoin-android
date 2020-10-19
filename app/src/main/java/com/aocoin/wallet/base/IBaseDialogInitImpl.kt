package com.aocoin.wallet.base

/**
 * @FileName IBaseDialogInitImpl
 * @Description
 * @Author dingyan
 * @Date 2020/10/14 3:19 PM
 */
interface IBaseDialogInitImpl {

    /**
     * 初始化数据
     */
    fun initData()

    /**
     * 初始化视图
     */
    fun initView()

    /**
     * 初始化事件监听
     */
    fun initListener()

    /**
     * 加载事件
     */
    fun loadData()
}