package com.elianshang.yougong.sales.bridges;

import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import com.elianshang.yougong.sales.InitApplication;
import com.elianshang.yougong.sales.utils.L;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zjh
 * @description
 * @date 16/8/23.
 */
public class JpushBridge {
  public static final String BRIDGE_NAME = "connectJPush_bridge";

  public static void bindToWebview(BridgeWebView bridgeWebView) {
    bridgeWebView.registerHandler(BRIDGE_NAME, new BridgeHandler() {
      @Override
      public void handler(String data, CallBackFunction function) {
        L.i(data);
        try {
          JSONObject jo = new JSONObject(data);
          bindTag(function, jo.getString("alias"), jo.getString("tags"));
        } catch (JSONException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private static void bindTag(final CallBackFunction function, String alias, String tags) {
    Set<String> s = new HashSet<>();
    tags = tags.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\"", "");
    if (!TextUtils.isEmpty(tags)) {
      String[] ts = tags.split(",");
      int l = ts.length;
      for (int i = 0; i < l; i++) {
        L.i("tag:" + ts[i]);
        s.add(ts[i]);
      }
    }
    s.add("dev");
    JPushInterface.setAliasAndTags(InitApplication.getInstance(), alias, s,
        new TagAliasCallback() {
          @Override public void gotResult(int i, String s, Set<String> set) {
            String mm = "";
            for (String m : set) {
              mm += m;
            }
            L.i("绑定结果:" + i + "," + s +","+mm);
          }
        });
  }
}