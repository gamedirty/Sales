package com.github.lzyzsd.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.util.SimpleArrayMap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("SetJavaScriptEnabled")
public class BridgeWebView extends XWalkView implements WebViewJavascriptBridge {

  private final String TAG = "BridgeWebView";

  public static final String toLoadJs = "WebViewJavascriptBridge.js";
  SimpleArrayMap<String, BridgeHandler> messageHandlers = new SimpleArrayMap<>();
  SimpleArrayMap<String, CallBackFunction> responseCallbacks = new SimpleArrayMap<>();

  BridgeHandler defaultHandler = new DefaultHandler();

  private List<Message> startupMessage = new ArrayList<Message>();

  public List<Message> getStartupMessage() {
    return startupMessage;
  }

  public void setStartupMessage(List<Message> startupMessage) {
    this.startupMessage = startupMessage;
  }

  private long uniqueId = 0;

  public BridgeWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public BridgeWebView(Context context) {
    super(context);
    init();
  }

  /**
   * @param handler default handler,handle messages send by js without assigned handler name, if js
   * message has handler name, it will be handled by named handlers registered by native
   */
  public void setDefaultHandler(BridgeHandler handler) {
    this.defaultHandler = handler;
  }

  private void init() {
    String ua = this.getUserAgentString();
    ua += ("/lianshang_android" + "/" + "1.8");

    JSONObject jo = new JSONObject();
    try {
      jo.put("platform", "android");
      jo.put("device_id",
          "" + DeviceTool.getDeviceID(getContext(), DeviceTool.getIMEI(getContext())));
      jo.put("imei", "" + DeviceTool.getIMEI(getContext()));
      jo.put("brand", "" + DeviceTool.getBrandName());
      jo.put("device_name", "" + DeviceTool.getDeviceName());
      jo.put("os_version", "" + DeviceTool.getOSVersionName());
    } catch (JSONException e) {
      e.printStackTrace();
    }
    Log.i("zhjh", "useragent:" + ua + jo.toString());
    this.setUserAgentString(ua + jo.toString());
    this.setVerticalScrollBarEnabled(false);
    this.setHorizontalScrollBarEnabled(false);
    this.setOnLongClickListener(null);
    this.setUIClient(generateBridgeWebViewClient());
    this.setResourceClient(generateBridgeWebViewUIClient());
  }

  protected XWalkUIClient generateBridgeWebViewClient() {
    return new BridgeXwalkClient(this);
  }

  private BridgeWebViewClient generateBridgeWebViewUIClient() {
    return new BridgeWebViewClient(this);
  }

  void handlerReturnData(String url) {
    String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
    Log.i("zhjh", "URL中的方法是:" + functionName);
    CallBackFunction f = responseCallbacks.get(functionName);
    String data = BridgeUtil.getDataFromReturnUrl(url);
    Log.i("zhjh", "URL中的数据是:" + data);
    if (f != null) {
      f.onCallBack(data);
      responseCallbacks.remove(functionName);
      return;
    }
  }

  @Override
  public void send(String data) {
    send(data, null);
  }

  @Override
  public void send(String data, CallBackFunction responseCallback) {
    doSend(null, data, responseCallback);
  }

  private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
    Message m = new Message();
    if (!TextUtils.isEmpty(data)) {
      m.setData(data);
    }
    if (responseCallback != null) {
      String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT,
          ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
      responseCallbacks.put(callbackStr, responseCallback);
      m.setCallbackId(callbackStr);
    }
    if (!TextUtils.isEmpty(handlerName)) {
      m.setHandlerName(handlerName);
    }
    queueMessage(m);
  }

  private void queueMessage(Message m) {
    if (startupMessage != null) {
      startupMessage.add(m);
    } else {
      dispatchMessage(m);
    }
  }

  void dispatchMessage(Message m) {
    String messageJson = m.toJson();
    //escape special characters for json string
    messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
    messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
    String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
    if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
      this.loadUrl(javascriptCommand, null);
    }
  }

  void flushMessageQueue() {
    if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
      loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, new CallBackFunction() {

        @Override
        public void onCallBack(String data) {
          // deserializeMessage
          List<Message> list = null;
          try {
            list = Message.toArrayList(data);
          } catch (Exception e) {
            e.printStackTrace();
            return;
          }
          if (list == null || list.size() == 0) {
            return;
          }
          for (int i = 0; i < list.size(); i++) {
            Message m = list.get(i);
            String responseId = m.getResponseId();
            // 是否是response
            if (!TextUtils.isEmpty(responseId)) {
              CallBackFunction function = responseCallbacks.get(responseId);
              String responseData = m.getResponseData();
              function.onCallBack(responseData);
              responseCallbacks.remove(responseId);
            } else {
              CallBackFunction responseFunction = null;
              // if had callbackId
              final String callbackId = m.getCallbackId();
              if (!TextUtils.isEmpty(callbackId)) {
                responseFunction = new CallBackFunction() {
                  @Override
                  public void onCallBack(String data) {
                    Log.i("zhjh", "onCallBack:" + data);
                    Message responseMsg = new Message();
                    responseMsg.setResponseId(callbackId);
                    responseMsg.setResponseData(data);
                    queueMessage(responseMsg);
                  }
                };
              } else {
                responseFunction = new CallBackFunction() {
                  @Override
                  public void onCallBack(String data) {
                    // do nothing
                  }
                };
              }
              BridgeHandler handler;
              if (!TextUtils.isEmpty(m.getHandlerName())) {
                handler = messageHandlers.get(m.getHandlerName());
              } else {
                handler = defaultHandler;
              }
              if (handler != null) {
                handler.handler(m.getData(), responseFunction);
              }
            }
          }
        }
      });
    }
  }

  public void loadUrl(String jsUrl, CallBackFunction returnCallback) {
    this.load(jsUrl, null);
    responseCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
  }

  /**
   * register handler,so that javascript can call it
   */
  public void registerHandler(String handlerName, BridgeHandler handler) {
    if (handler != null) {
      messageHandlers.put(handlerName, handler);
    }
  }

  /**
   * call javascript registered handler
   */
  public void callHandler(String handlerName, String data, CallBackFunction callBack) {
    doSend(handlerName, data, callBack);
  }
}
