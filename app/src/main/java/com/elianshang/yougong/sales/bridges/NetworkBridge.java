package com.elianshang.yougong.sales.bridges;

import com.elianshang.yougong.sales.InitApplication;
import com.elianshang.yougong.sales.utils.L;
import com.elianshang.yougong.sales.utils.NetWorkTool;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zjh
 * @description
 * @date 16/9/27.
 */

public class NetworkBridge {
  public static final String BRIDGE_NAME = "network_bridge";

  public static void bindToWebview(BridgeWebView bridgeWebView) {
    bridgeWebView.registerHandler(BRIDGE_NAME, new BridgeHandler() {
      @Override
      public void handler(String data, CallBackFunction function) {
        L.i(data);
        JSONObject jsonObject = new JSONObject();
        try {
          jsonObject.put(ResultConstants.errorCode, ResultConstants.ERRORCODE_SUCCESS);
          jsonObject.put("network", NetWorkTool.getNetWorkType(InitApplication.getInstance()));
        } catch (JSONException e) {
          e.printStackTrace();
        }
        function.onCallBack(jsonObject.toString());
      }
    });
  }
}
