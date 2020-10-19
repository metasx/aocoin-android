package com.aocoin.wallet.db

import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel

/**
 * @FileName: Coin
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-12 17:11
 */
@Table(name = "coin_info", database = AppDatabase::class)
public class Coin : BaseModel() {

    @PrimaryKey(autoincrement = true)
    @Column
    var id: Long = 0

    @Column
    @WalletLocalType.LocalType
    var chainType: Int = WalletLocalType.LOCAL_TYPE_POK

    @Column
    var address: String = ""

    @Column
    var abbr: String = ""

    @Column
    var imageSrc: String = ""

    @Column
    var balance: String = ""

    @Column
    var precision: Int = 1
}