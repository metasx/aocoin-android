package com.aocoin.wallet.base


interface IBaseInitImpl {

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
     * 初始化适配器
     */
    fun initAdapter()

    /**
     * 加载事件
     */
    fun loadData()

}