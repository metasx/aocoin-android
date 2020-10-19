package com.aocoin.wallet.db

import com.aocoin.wallet.db.AppDatabase
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import com.raizlabs.android.dbflow.structure.BaseModel
import java.io.Serializable

/**
 * @FileName: Wallet
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-12 16:26
 */
@Table(name = "wallet_info", database = AppDatabase::class)
public class Wallet : BaseModel() ,Serializable{

    @PrimaryKey(autoincrement = true)
    @Column
    var id: Long = 0

    @Column
    @WalletLocalType.LocalType
    var localType: Int = WalletLocalType.LOCAL_TYPE_POK

    @Column
    var address: String = ""

    @Column
    var name: String = ""

    @Column
    var password: String = ""

    @Column
    var privateKey: String = ""

    @Column
    var keystore: String = ""

    @Column
    var mnemonic: String = ""

}