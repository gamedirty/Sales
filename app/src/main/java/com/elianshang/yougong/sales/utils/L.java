package com.elianshang.yougong.sales.utils;

import android.util.Log;
import com.elianshang.yougong.sales.InitApplication;

/**
 * @author zjh
 * @description
 * @date 16/9/18.
 */
public class L {
  public static void i(String msg) {
    if (InitApplication.getInstance().isDebugable()) {
      Log.i("zhjh", msg);
    }
  }

  public static void i(String tag, String msg) {
    if (InitApplication.getInstance().isDebugable()) {
      Log.i(tag, msg);
    }
  }
}
