package com.elianshang.yougong.sales.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import com.elianshang.yougong.sales.InitApplication;

/**
 * @author zjh
 * @description
 * @date 16/10/12.
 */

public class Pref {
  private static final String SALES = "lsh_sales";
  private static final String tag = "alias_tag";

  public static void saveAliasAndTags(String data) {
    InitApplication.getInstance();
    SharedPreferences preference =
        InitApplication.getInstance().getSharedPreferences(SALES, Context.MODE_PRIVATE);
    preference.edit().putString(tag, data).commit();
  }

  public static String getAliasAndTags() {
    InitApplication.getInstance();
    SharedPreferences preference =
        InitApplication.getInstance().getSharedPreferences(SALES, Context.MODE_PRIVATE);
    return preference.getString(tag, null);
  }
}
