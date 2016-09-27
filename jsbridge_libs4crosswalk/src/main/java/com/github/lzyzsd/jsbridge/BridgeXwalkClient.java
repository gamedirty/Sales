package com.github.lzyzsd.jsbridge;

import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;

/**
 * @author zjh
 * @description
 * @date 16/8/15.
 */
public class BridgeXwalkClient extends XWalkUIClient {
    private BridgeWebView view;

    public BridgeXwalkClient(BridgeWebView view) {
        super(view);
        this.view = view;
    }

    @Override
    public void onPageLoadStopped(XWalkView vview, String url, LoadStatus status) {

        super.onPageLoadStopped(view, url, status);
        if (BridgeWebView.toLoadJs != null) {
            BridgeUtil.webViewLoadLocalJs(view, BridgeWebView.toLoadJs);
        }

        //
        if (view.getStartupMessage() != null) {
            for (Message m : view.getStartupMessage()) {
                view.dispatchMessage(m);
            }
            view.setStartupMessage(null);
        }
    }



}
