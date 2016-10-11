package com.github.lzyzsd.jsbridge;

import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by bruce on 10/28/15.
 */
public class BridgeWebViewClient extends WebViewClient {
  private BridgeWebView webView;

  public BridgeWebViewClient(BridgeWebView webView) {
    this.webView = webView;
  }

  @Override
  public boolean shouldOverrideUrlLoading(WebView view, String url) {
    try {
      url = URLDecoder.decode(url, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) { // 如果是返回数据
      webView.handlerReturnData(url);
      return true;
    } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) { //
      webView.flushMessageQueue();
      return true;
    } else {
      return super.shouldOverrideUrlLoading(view, url);
    }
  }

  @Override
  public void onPageFinished(WebView view, String url) {
    super.onPageFinished(view, url);

    if (BridgeWebView.toLoadJs != null) {
      BridgeUtil.webViewLoadLocalJs(view, BridgeWebView.toLoadJs);
    }
    if (webView.getStartupMessage() != null) {
      for (Message m : webView.getStartupMessage()) {
        webView.dispatchMessage(m);
      }
      webView.setStartupMessage(null);
    }
  }

  @Override
  public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
    super.onReceivedError(view, request, error);
    Log.i("zhjh","onReceivedError:"+request.getUrl());
  }
}