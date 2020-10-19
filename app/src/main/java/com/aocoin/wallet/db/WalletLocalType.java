package com.aocoin.wallet.db;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @FileName: WalletLocalType
 * @Description: 定义钱包本地类型，用来区分公链/伪公链，当公链增加需要在该类中添加其对应的值
 * @Author: haoyanhui
 * @Date: 2020-06-02 15:21
 */
public interface WalletLocalType {
    // polkadot
    int LOCAL_TYPE_POK = 104;
    // kusama
    int LOCAL_TYPE_KSM = 105;
    // kulupu
    int LOCAL_TYPE_KLP = 106;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({LOCAL_TYPE_POK, LOCAL_TYPE_KSM, LOCAL_TYPE_KLP})
    @interface LocalType {
    }
}
