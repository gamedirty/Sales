package com.github.lzyzsd.jsbridge;


import android.util.Log;

import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by bruce on 10/28/15.
 */
public class BridgeWebViewClient extends XWalkResourceClient {
    private BridgeWebView webView;

    public BridgeWebViewClient(BridgeWebView webView) {
        super(webView);
        this.webView = webView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
        Log.i("zhjh","shouldOverrideUrlLoading--------------------:"+url);
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
    public void onLoadFinished(XWalkView view, String url) {
        super.onLoadFinished(view, url);
        Log.i("zhjh","onLoadFinished---------------------:"+url);
    }

    @Override
    public void onProgressChanged(XWalkView view, int progressInPercent) {
        super.onProgressChanged(view, progressInPercent);
        if (progressInPercent>=30){
            if (BridgeWebView.toLoadJs != null) {
                BridgeUtil.webViewLoadLocalJs(view, BridgeWebView.toLoadJs);
            }
        }
    }
}