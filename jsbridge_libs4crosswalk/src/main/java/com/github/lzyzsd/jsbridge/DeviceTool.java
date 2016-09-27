package com.github.lzyzsd.jsbridge;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.lang.reflect.Method;

public class DeviceTool {

    public static String getAndroidOsSystemProperties(String key) {
        String ret;
        try {
            Method systemProperties_get = Class.forName("android.os.SystemProperties").getMethod("get", String.class);
            if ((ret = (String) systemProperties_get.invoke(null, key)) != null)
                return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return "";
    }

    public static String getSerialNo() {
        String[] propertys = {"ro.boot.serialno", "ro.serialno"};
        String sn = null;
        for (String key : propertys) {
            sn = getAndroidOsSystemProperties(key);
            if (!TextUtils.isEmpty(sn)) {
                break;
            }
        }

        return sn;
    }


    public static String getDeviceID(Context context, String imei) {
        if (TextUtils.isEmpty(imei)) {
            return "unknown";
        }
        String sn = getSerialNo();
        String androidId = getAndroidId(context);

        String key = sn + imei + androidId;
        if (TextUtils.isEmpty(key)) {
            key = "unknown";
        } else {
            key = MD5Tool.toMd5(key);
        }
        return key;
    }


    /**
     * 获取AndroidId
     */
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取IMEI
     * <p/>
     * IMEI是International Mobile Equipment Identity （国际移动设备标识）的简称
     * IMEI由15位数字组成的”电子串号”，它与每台手机一一对应，而且该码是全世界唯一的
     * 其组成为：
     * 1. 前6位数(TAC)是”型号核准号码”，一般代表机型
     * 2. 接着的2位数(FAC)是”最后装配号”，一般代表产地
     * 3. 之后的6位数(SNR)是”串号”，一般代表生产顺序号
     * 4. 最后1位数(SP)通常是”0″，为检验码，目前暂备用
     */
    public static String getIMEI(Context context) {
        return ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
    }

    /**
     * 获取IMSI
     * <p/>
     * IMSI是国际移动用户识别码的简称(International Mobile Subscriber Identity)
     * IMSI共有15位，其结构如下：
     * MCC+MNC+MIN
     * MCC：Mobile Country Code，移动国家码，共3位，中国为460;
     * MNC:Mobile NetworkCode，移动网络码，共2位
     * <p/>
     * 在中国，移动的代码为电00和02，联通的代码为01，电信的代码为03
     * 合起来就是（也是Android手机中APN配置文件中的代码）：
     * 中国移动：46000 46002
     * 中国联通：46001
     * 中国电信：46003
     * 举例，一个典型的IMSI号码为460030912121001
     */
    public static String getIMSI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();
        return IMSI;
    }

    /**
     * 获取mac地址
     *
     * @param context
     * @return
     */
    public static String getMac(Context context) {
        return ((WifiManager) context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress();
    }

    /**
     * 得到设备名字
     */
    public static String getDeviceName() {
        String model = android.os.Build.MODEL;
        if (model == null || model.length() <= 0) {
            return "";
        } else {
            return model;
        }
    }

    /**
     * 得到品牌名字
     */
    public static String getBrandName() {
        String brand = android.os.Build.BRAND;
        if (brand == null || brand.length() <= 0) {
            return "";
        } else {
            return brand;
        }
    }

    /**
     * 得到操作系统版本号
     */
    public static String getOSVersionName() {
        String version = android.os.Build.VERSION.RELEASE;
        if (version == null || version.length() <= 0) {
            return "";
        } else {
            return version;
        }
    }

    /**
     * 得到客户端版本信息
     *
     * @param context
     * @return
     */
    public static String getClientVersionName(Context context) {
        if (context == null) {
            return "";
        }
        try {
            PackageInfo packInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getNetWorkType(Context context) {
        int mNetWorkType = 0;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            networkInfo.getType();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = 1;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                mNetWorkType = getMobileNetworkType(context);
            }
        }
        return mNetWorkType;
    }

    public static int getMobileNetworkType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return 3;//2G
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return 4;//3G
            case TelephonyManager.NETWORK_TYPE_LTE:
                return 5;//4G
            default:
                return 4;
        }
    }

}
