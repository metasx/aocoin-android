package com.aocoin.wallet.db

import com.raizlabs.android.dbflow.annotation.Database

/**
 * @FileName: AppDatabase
 * @Description: 作用描述
 * @Author: haoyanhui
 * @Date: 2020-10-12 10:15
 */
@Database(name = AppDatabase.DB_NAME, version = AppDatabase.DB_VERSION)
object AppDatabase {
    const val DB_VERSION: Int = 1
    const val DB_NAME = "core_db"
}