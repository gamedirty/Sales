package com.elianshang.yougong.sales.bridges;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.elianshang.yougong.sales.InitApplication;
import com.elianshang.yougong.sales.utils.L;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;

/**
 * @author zjh
 * @description
 * @date 16/8/18.
 */
public class LocationBridge {
  private static final String BRIDGE_NAME = "geoLocation_bridge";
  private static BridgeHandler bridgeHandler;

  public static void bindToWebview(BridgeWebView bridgeWebView) {
    if (bridgeWebView != null) {
      if (bridgeHandler == null) bridgeHandler = new LocationBridgeHandler();
      bridgeWebView.registerHandler(BRIDGE_NAME, bridgeHandler);

    }
  }

  static class LocationBridgeHandler implements BridgeHandler {

    @Override
    public void handler(String options, CallBackFunction function) {
      doLocation(options, function);
    }
  }

  private static void doLocation(String options, final CallBackFunction function) {

    L.i("定位option是 :" + options);
    GPSOptions g = null;
    if (g == null) g = new GPSOptions();

    AMapLocationClientOption aMapLocationClientOption = new AMapLocationClientOption();
    aMapLocationClientOption.setHttpTimeOut(g.getTimeout());
    aMapLocationClientOption.setInterval(g.getIntervel());
    aMapLocationClientOption.setKillProcess(g.isKillProcess());
    aMapLocationClientOption.setLocationCacheEnable(g.isLocationCache());
    aMapLocationClientOption.setGpsFirst(g.isGpsFirst());
    aMapLocationClientOption.setOnceLocation(g.isOnceLocation());
    aMapLocationClientOption.setLocationMode(g.getLocationMode());
    final AMapLocationClient mapLocationClient =
        new AMapLocationClient(InitApplication.getInstance());
    mapLocationClient.setLocationOption(aMapLocationClientOption);
    mapLocationClient.startLocation();

    AMapLocationListener mapLocationListener = new AMapLocationListener() {

      @Override
      public void onLocationChanged(final AMapLocation aMapLocation) {
        function.onCallBack(aMapLocation.toStr().toLowerCase());
        mapLocationClient.stopLocation();
      }
    };
    mapLocationClient.setLocationListener(mapLocationListener);
  }

  private static class GPSOptions {
    boolean gpsFirst;
    boolean killProcess;
    long timeout;
    long intervel;
    boolean locationCache;
    int locationMode;//1,Battery_Saving,2:Device_Sensors,3:Hight_Accuracy
    boolean onceLocation;

    public boolean isGpsFirst() {
      return gpsFirst;
    }

    public void setGpsFirst(boolean gpsFirst) {
      this.gpsFirst = gpsFirst;
    }

    public boolean isKillProcess() {
      return killProcess;
    }

    public void setKillProcess(boolean killProcess) {
      this.killProcess = killProcess;
    }

    public long getTimeout() {
      return timeout;
    }

    public void setTimeout(long timeout) {
      this.timeout = timeout;
    }

    public long getIntervel() {
      return intervel;
    }

    public void setIntervel(long intervel) {
      this.intervel = intervel;
    }

    public boolean isLocationCache() {
      return locationCache;
    }

    public void setLocationCache(boolean locationCache) {
      this.locationCache = locationCache;
    }

    public AMapLocationClientOption.AMapLocationMode getLocationMode() {
      switch (locationMode) {
        case 1:
          return AMapLocationClientOption.AMapLocationMode.Battery_Saving;
        case 2:
          return AMapLocationClientOption.AMapLocationMode.Device_Sensors;
        case 3:
          return AMapLocationClientOption.AMapLocationMode.Hight_Accuracy;
      }
      return AMapLocationClientOption.AMapLocationMode.Battery_Saving;
    }

    public void setLocationMode(int locationMode) {
      this.locationMode = locationMode;
    }

    public boolean isOnceLocation() {
      return onceLocation;
    }

    public void setOnceLocation(boolean onceLocation) {
      this.onceLocation = onceLocation;
    }

    @Override
    public String toString() {
      return "GPSOptions{" +
          "gpsFirst=" + gpsFirst +
          ", killProcess=" + killProcess +
          ", timeout=" + timeout +
          ", intervel=" + intervel +
          ", locationCache=" + locationCache +
          ", locationMode=" + locationMode +
          ", onceLocation=" + onceLocation +
          '}';
    }
  }
}
