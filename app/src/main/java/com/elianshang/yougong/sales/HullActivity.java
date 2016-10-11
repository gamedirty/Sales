package com.elianshang.yougong.sales;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

import android.view.View;
import android.webkit.WebView;
import com.elianshang.yougong.sales.bridges.BaseBridgeActivity;
import com.elianshang.yougong.sales.bridges.CameraBridge;
import com.elianshang.yougong.sales.bridges.JpushBridge;
import com.elianshang.yougong.sales.bridges.LocationBridge;
import com.elianshang.yougong.sales.bridges.NetworkBridge;
import com.elianshang.yougong.sales.utils.L;
import com.elianshang.yougong.sales.utils.NetWorkTool;
import com.elianshang.yougong.sales.utils.T;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.*;

public class HullActivity extends BaseBridgeActivity
    implements EasyPermissions.PermissionCallbacks {

  private static final int RC_SETTINGS_SCREEN = 1109;
  private BridgeWebView webView;
  //private static final String ORIGIN_URL = "http://gl.market-sales-h5.wmdev2.lsh123.com/#test";
  //private static final String ORIGIN_URL =
  //    "http://gl.market-sales-h5.wmdev2.lsh123.com/#my/remind/list";
  private static final String ORIGIN_URL = "http://qa.market-sales-h5.wmdev2.lsh123.com";
  private static String URL2LOAD;

  private View netErrorView;
  private View netErrorButton;

  public static void launch4Push(Context context, String url) {
    URL2LOAD = url;
    Intent intent = new Intent(context, HullActivity.class);
    intent.putExtra("url", url);
    if (!(context instanceof Activity)) {
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    }
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    checkAndRequestPermissions();
    initViews();
    fetchData();
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    if (webView == null) return;
    justLoad(URL2LOAD);
  }

  private void justLoad(String url2LOAD) {
    if (TextUtils.isEmpty(url2LOAD)) {
      loadUrl(ORIGIN_URL);
    } else {
      loadUrl(url2LOAD);
    }
  }

  void loadUrl(String url) {
    if (webView != null) {
      webView.loadUrl(url);
    }
  }

  private void checkAndRequestPermissions() {
    String[] perms = new String[] {WRITE_EXTERNAL_STORAGE,
        READ_PHONE_STATE, ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION, CAMERA};
    if (!EasyPermissions.hasPermissions(this, perms)) {

      EasyPermissions.requestPermissions(this, getString(R.string.permis_request), R.string.confirm,
          R.string.exit, 1, perms);
    } else {
      if (null != webView) {
        webView.setUseragent(true);
      }
    }
  }

  private void fetchData() {
    String url = getIntent().getStringExtra("url");
    if (!chechNetwork()) return;
    justLoad(url);
  }

  private void initViews() {
    webView = (BridgeWebView) findViewById(R.id.webview);
    netErrorView = findViewById(R.id.net_error);
    netErrorButton = findViewById(R.id.net_error_btn);
    netErrorButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (NetWorkTool.isNetAvailable(HullActivity.this)) {
          netErrorView.setVisibility(View.GONE);
          webView.setVisibility(View.VISIBLE);
          justLoad(URL2LOAD);
        } else {
          T.show(HullActivity.this, "请检查网络");
        }
      }
    });
    bindBridges();
  }

  private void bindBridges() {
    LocationBridge.bindToWebview(webView);
    CameraBridge.bindToWebview(webView, this, REQUESTCODE_TAKE_PHOTO);
    JpushBridge.bindToWebview(webView);
    NetworkBridge.bindToWebview(webView);
  }

  private boolean chechNetwork() {
    boolean net = NetWorkTool.isNetAvailable(this);
    if (!net) {
      netErrorView.setVisibility(View.VISIBLE);
    }
    return net;
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    L.i("zhjh","onKeyDown:"+webView.canGoBack());
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (webView.canGoBack()) {
        webView.goBack();//返回上一页面
        return true;
      } else {
        finish();
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }

  @Override public void onPermissionsGranted(int requestCode, List<String> perms) {
    webView.setUseragent(true);
  }

  @Override public void onPermissionsDenied(int requestCode, List<String> perms) {

    L.i("zhjh", "deny回调");
    new AppSettingsDialog.Builder(this, getString(R.string.permis_request))
        .setTitle(getString(R.string.title_settings_dialog))
        .setPositiveButton(getString(R.string.setting))
        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
          @Override public void onClick(DialogInterface dialog, int which) {
            finish();
          }
        } /* click listener */)
        .setRequestCode(RC_SETTINGS_SCREEN)
        .build()
        .show();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SETTINGS_SCREEN) {
      checkAndRequestPermissions();
    }
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    
  }
}
