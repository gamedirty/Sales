package com.elianshang.yougong.sales;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.elianshang.yougong.sales.bridges.BaseBridgeActivity;
import com.elianshang.yougong.sales.bridges.CameraBridge;
import com.elianshang.yougong.sales.bridges.JpushBridge;
import com.elianshang.yougong.sales.bridges.LocationBridge;
import com.elianshang.yougong.sales.bridges.NetworkBridge;
import com.elianshang.yougong.sales.utils.L;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.DeviceTool;
import java.util.List;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.*;

public class HullActivity extends BaseBridgeActivity
    implements EasyPermissions.PermissionCallbacks {

  private static final int RC_SETTINGS_SCREEN = 1109;
  private BridgeWebView webView;
  private static final String ORIGIN_URL = "http://gl.market-sales-h5.wmdev2.lsh123.com/#test";
  //private static final String ORIGIN_URL = "http://gl.market-sales-h5.wmdev2.lsh123.com";
  private static String URL2LOAD;

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
    L.i("onNewIntent");
    if (TextUtils.isEmpty(URL2LOAD)) {
      webView.loadUrl(ORIGIN_URL);
    } else {
      webView.loadUrl(URL2LOAD);
    }
  }

  private void checkAndRequestPermissions() {
    String[] perms = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION, permission.CAMERA};
    if (!EasyPermissions.hasPermissions(this, perms)) {

      EasyPermissions.requestPermissions(this, getString(R.string.permis_request), R.string.confirm,
          R.string.exit, 1, perms);
    }
  }

  private void fetchData() {
    String url = getIntent().getStringExtra("url");
    L.i("zhjh", "推送消息:" + url);
    if (TextUtils.isEmpty(url)) {
      webView.loadUrl(ORIGIN_URL);
    } else {
      webView.loadUrl(url);
    }
  }

  private void initViews() {
    webView = (BridgeWebView) findViewById(R.id.webview);
    LocationBridge.bindToWebview(webView);
    CameraBridge.bindToWebview(webView, this, REQUESTCODE_TAKE_PHOTO);
    JpushBridge.bindToWebview(webView);
    NetworkBridge.bindToWebview(webView);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {

    if (keyCode == KeyEvent.KEYCODE_BACK) {
      webView.loadUrl("javascript:globalEventBack_LSH_SALES()");
      return true;
      //if (webView.canGoBack()) {
      //  webView.goBack();//返回上一页面
      //  return true;
      //} else {
      //  finish();
      //}
    }
    return super.onKeyDown(keyCode, event);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
  }

  @Override public void onPermissionsGranted(int requestCode, List<String> perms) {

  }

  @Override public void onPermissionsDenied(int requestCode, List<String> perms) {
    if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
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
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == RC_SETTINGS_SCREEN) {
      checkAndRequestPermissions();
    }
  }
}
