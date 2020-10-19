package com.aocoin.wallet.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 工具类
 */
public class SharedPrefUtil {

    public static final String SP_DATA = "aocoin_core_data";

    private static volatile SharedPrefUtil mInstance;

    private static SharedPreferences mPreferences;
    private static SharedPreferences.Editor editor;

    public static void init(Context context) {
        if (null == mInstance) {
            synchronized (SharedPrefUtil.class) {
                if (null == mInstance) {
                    mInstance = new SharedPrefUtil(context);
                }
            }
        }
    }

    private SharedPrefUtil(Context context) {
        mPreferences = context.getSharedPreferences(SP_DATA, Context.MODE_PRIVATE);
        editor = mPreferences.edit();
    }

    @Deprecated
    public static void saveData(Context context, String key, String value) {
        init(context);
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveData(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveData(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    public static void saveData(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void saveData(Context context, String key, int value) {
        init(context);
        editor.putInt(key, value);
        editor.apply();
    }

    public static void saveData(Context context, String key, boolean value) {
        init(context);
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void saveData(Context context, String key, long value) {
        init(context);
        editor.putLong(key, value);
        editor.apply();
    }

    public static String getStringData(Context context, String key) {
        init(context);
        return mPreferences.getString(key, "");
    }

    public static String getStringData(String key) {
        return mPreferences.getString(key, "");
    }

    public static String getStringData(String key, String defStr) {
        return mPreferences.getString(key, defStr);
    }

    public static int getInt(String key) {
        return mPreferences.getInt(key, 0);
    }

    public static boolean getBoolean(String key) {
        return mPreferences.getBoolean(key, false);
    }

    /**
     * 获取boolean值，未设置返回传入的值
     */
    public static boolean getBoolean(String key, boolean def) {
        return mPreferences.getBoolean(key, def);
    }

    public static int getIntData(Context context, String key) {
        init(context);
        return mPreferences.getInt(key, 0);
    }

    public static long getLongData(Context context, String key) {
        init(context);
        return mPreferences.getLong(key, 0);
    }

    public static boolean getBooleanData(Context context, String key) {
        init(context);
        return mPreferences.getBoolean(key, false);
    }

    public static void clearData(Context context) {
        init(context);
        editor.clear();
        editor.apply();
    }
}
