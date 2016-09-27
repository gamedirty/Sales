package com.elianshang.yougong.sales;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import com.elianshang.yougong.sales.utils.L;
import com.elianshang.yougong.sales.utils.T;

/**
 * @author zjh
 * @description
 * @date 16/9/18.
 */
public class InitApplication extends Application {
  private static InitApplication instance;
  private static boolean debugable;

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    setDebugAble();
    JPushInterface.setDebugMode(true);  // 设置开启日志,发布时请关闭日志
    JPushInterface.init(this);        // 初始化 JPush
  }

  private void setDebugAble() {
    ApplicationInfo appInfo = null;
    try {
      appInfo = this.getPackageManager()
          .getApplicationInfo(getPackageName(),
              PackageManager.GET_META_DATA);
      debugable = appInfo.metaData.getBoolean("debug");
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }

  public boolean isDebugable() {
    return debugable;
  }

  public static InitApplication getInstance() {
    return instance;
  }
}
