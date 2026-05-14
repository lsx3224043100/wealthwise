package com.wealthwise.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "wealthwise_prefs";
    private static final String KEY_TOKEN = "user_token";
    private static final String KEY_USERNAME = "user_name";
    private static final String KEY_USER_ID = "user_id";

    public static void saveToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public static String getToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    public static void saveUsername(Context context, String username) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_USERNAME, username).apply();
    }

    public static String getUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_USERNAME, null);
    }

    public static void saveUserId(Context context, Long userId) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(KEY_USER_ID, userId).apply();
    }

    public static Long getUserId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getLong(KEY_USER_ID, -1);
    }

    private static final String KEY_PAYMENT_METHODS_EXPENSE = "payment_methods_expense";
    private static final String KEY_PAYMENT_METHODS_INCOME = "payment_methods_income";

    /** 按方向保存支付方式：direction=1 收入，direction=2 支出 */
    public static void savePaymentMethods(Context context, List<String> methods, int direction) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        StringBuilder sb = new StringBuilder();
        for (String m : methods) {
            if (sb.length() > 0) sb.append(",");
            sb.append(m);
        }
        String key = direction == 1 ? KEY_PAYMENT_METHODS_INCOME : KEY_PAYMENT_METHODS_EXPENSE;
        prefs.edit().putString(key, sb.toString()).apply();
    }

    /** 按方向获取支付方式 */
    public static List<String> getPaymentMethods(Context context, int direction) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String key = direction == 1 ? KEY_PAYMENT_METHODS_INCOME : KEY_PAYMENT_METHODS_EXPENSE;
        String raw = prefs.getString(key, null);
        List<String> list = new ArrayList<>();
        if (raw != null) {
            for (String s : raw.split(",")) {
                if (!s.isEmpty()) list.add(s);
            }
        } else {
            // 默认值
            if (direction == 2) {
                list.add("微信");
                list.add("支付宝");
                list.add("现金");
                list.add("银行卡");
            } else {
                list.add("微信");
                list.add("支付宝");
                list.add("银行卡");
            }
        }
        return list;
    }

    // 保留旧接口兼容（默认支出）
    @Deprecated
    public static void savePaymentMethods(Context context, List<String> methods) {
        savePaymentMethods(context, methods, 2);
    }

    @Deprecated
    public static List<String> getPaymentMethods(Context context) {
        return getPaymentMethods(context, 2);
    }

    public static void clearAll(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }
}
