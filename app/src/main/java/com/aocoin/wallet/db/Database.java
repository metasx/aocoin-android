package com.aocoin.wallet.db;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * @FileName Database
 * @Description
 * @Author dingyan
 * @Date 2020/10/13 5:30 PM
 */
public class Database {

    /************* 钱包 *************/

    /**
     * 添加钱包
     *
     * @param wallet
     * @return
     */
    public synchronized static boolean insertMyWallet(Wallet wallet) {
        return wallet.save();
    }

    /**
     * 返回所有钱包
     */
    public synchronized static List<Wallet> findAllWallets() {
        return SQLite.select().from(Wallet.class).queryList();
    }


    /**
     * 通过 privateKey 和 LocalType 获取钱包
     *
     * @param privateKey
     * @param localType
     * @return
     */
    public synchronized static Wallet findWalletByPrivateKeyAndLocalType(String privateKey, int localType) {
        Wallet wallet = SQLite
                .select()
                .from(Wallet.class)
                .where(Wallet_Table.privateKey.is(privateKey),
                        Wallet_Table.localType.is(localType))
                .querySingle();

        if (wallet == null) {
            if (privateKey.startsWith("0x")) {
                String newPrivateKey = privateKey.substring(2);
                return SQLite
                        .select()
                        .from(Wallet.class)
                        .where(Wallet_Table.privateKey.is(newPrivateKey),
                                Wallet_Table.localType.is(localType))
                        .querySingle();
            } else {
                String newPrivateKey = "0x" + privateKey;
                return SQLite
                        .select()
                        .from(Wallet.class)
                        .where(Wallet_Table.privateKey.is(newPrivateKey),
                                Wallet_Table.localType.is(localType))
                        .querySingle();
            }
        }

        return wallet;
    }

    /**
     * 根据LocalType 获取所有钱包
     *
     * @param localType
     * @return
     */
    public synchronized static List<Wallet> findMyWalletsByLocalType(int localType) {
        return SQLite.select().from(Wallet.class).where(Wallet_Table.localType.is(localType)).queryList();
    }

    /**
     * 通过 Address he LocalType 删除钱包
     *
     * @param address
     * @param localType
     */
    public synchronized static void deleteMyWalletByAddressAndLocalType(String address, int localType) {
        List<Wallet> wallets = SQLite
                .select()
                .from(Wallet.class)
                .where(Wallet_Table.address.is(address),
                        Wallet_Table.localType.is(localType))
                .queryList();

        for (Wallet wallet : wallets) {
            wallet.delete();
        }
    }

    /**
     * 修改钱包
     *
     * @param wallet
     * @return
     */
    public synchronized static boolean updateMyWallet(Wallet wallet) {
        return wallet.update();
    }

    /**
     * 获取钱包对应的资产列表
     */
    public synchronized static List<Coin> findCoinByAddress(String address, int localType) {
        return SQLite.select().from(Coin.class)
                .where(Coin_Table.address.is(address), Coin_Table.chainType.is(localType))
                .queryList();
    }

    /**
     * 删除钱包对应的资产列表
     * @param address
     * @param localType
     */
    public synchronized static void delCoinByAddress(String address, int localType) {
        SQLite.delete().from(Coin.class)
                .where(Coin_Table.address.is(address), Coin_Table.chainType.is(localType))
                .execute();
    }

}
