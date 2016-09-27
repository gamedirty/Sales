package com.elianshang.yougong.sales.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * Created by qujc on 15/6/16.
 */
public class T {

    private static Toast singleToast = null;

    private static Context mSingleToastContext = null;


    public static void show(Context context, String msg) {
        show(context, msg, Toast.LENGTH_SHORT);
    }


    public static void show(final Context context, final String msg, final int duration) {
        if (context != null) {

            if (Thread.currentThread() == context.getMainLooper().getThread()) {
                Toast.makeText(context, msg, duration).show();

            } else {
                Handler handler = new Handler(context.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, msg, duration).show();
                    }
                });
            }
            /*if (toastText == null) {
                toastText = (TextView)UITool.inflate(context, R.layout.layout_theme_toast, null);
            }

            toastText.setText(msg);

            if (themeToast == null) {
                themeToast = new Toast(context);
                themeToast.setView(toastText);
//                themeToast.setGravity(Gravity.CENTER, 0, 0);
            }

            themeToast.setDuration(duration);
            themeToast.show();*/


        }
    }

    public static void show(Context context, int resId) {
        show(context, context.getString(resId), Toast.LENGTH_SHORT);
    }


    public static void show(Context context, int resId, int duration) {
        show(context, context.getString(resId), duration);
    }

    /**
     * 单例toast 多个toast即时刷新
     *
     * @param context
     * @param msgId
     */
    public static void showSingleToast(Context context, int msgId) {
        showSingleToast(context, context.getString(msgId), Toast.LENGTH_SHORT);
    }

    /**
     * @param context
     * @param msg
     */
    public static void showSingleToast(Context context, String msg) {
        showSingleToast(context, msg, Toast.LENGTH_SHORT);
    }

    /**
     * 单例toast 多个toast即时刷新
     *
     * @param context
     * @param msgId
     * @param duraion
     */
    public static void showSingleToast(Context context, int msgId, int duraion) {
        showSingleToast(context, context.getString(msgId), duraion);
    }

    /**
     * 单例toast 多个toast即时刷新
     *
     * @param context
     * @param msg
     * @param duraion
     */
    public static void showSingleToast(Context context, String msg, int duraion) {
        try {
            if (singleToast == null || mSingleToastContext != context) {//context对象变化时。重新初始化toast
                mSingleToastContext = context;
                singleToast = Toast.makeText(context, msg, duraion);
            } else {
                singleToast.setText(msg);
                singleToast.setDuration(duraion);
            }
            singleToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
