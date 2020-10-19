package com.aocoin.wallet.pages.wallet.enum

/**
 * @FileName: PolkaSubchainType
 * @Description: 波卡中各个链对应的信息
 * @Author: haoyanhui
 * @Date: 2020-09-10 11:30
 */
enum class PolkadotChainInfo(val endpoint: String, val ss58Format: Int) {
    POLKADOT(endpoint = "wss://rpc.polkadot.io", ss58Format = 0),
    KUSAMA(endpoint = "wss://kusama-rpc.polkadot.io/", ss58Format = 2),
    KULUPU(endpoint = "wss://rpc.kulupu.corepaper.org/ws", ss58Format = 16)
}