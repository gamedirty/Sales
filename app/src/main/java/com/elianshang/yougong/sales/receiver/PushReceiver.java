package com.elianshang.yougong.sales.receiver;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import cn.jpush.android.api.JPushInterface;
import com.elianshang.yougong.sales.HullActivity;
import com.elianshang.yougong.sales.R;
import com.elianshang.yougong.sales.utils.L;
import java.util.Random;
import java.util.Set;
import org.json.JSONObject;

/**
 * @author zjh
 * @description
 * @date 16/9/23.
 */

public class PushReceiver extends BroadcastReceiver {
  private static final String Notification_Open = "cn.jpush.android.intent.NOTIFICATION_OPENED";

  @Override public void onReceive(Context context, Intent intent) {
    print(intent.getExtras());
    if (intent == null || context == null || intent.getExtras() == null) {
      return;
    }
    Bundle bundle = intent.getExtras();

    String action = intent.getAction();
    if (TextUtils.isEmpty(action)) {
      return;
    }
    if (action.equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
      //收到自定义通知
      String title = bundle.getString(JPushInterface.EXTRA_TITLE);
      String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
      String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
      String messageId = bundle.getString(JPushInterface.EXTRA_MSG_ID);

      int t = getExtraT(extras);
      //String i = getExtraI(extras);
      //boolean n = getExtraN(extras);
      //if (n) {
      //  if (!BaseApplication.get().isLogin()) {
      //    //                    LogTool.e("lhz", "要求登录的push，用户未登录");
      //    PushTools.setTagAndAlias();
      //    return;
      //  }
      //}
      if (!neeedShowNotif(t)) {
        return;
      }
      showNotification(context, title, message, messageId, extras);
    } else {
      //String title = bundle.getString("title");
      //String message = bundle.getString("message");
      //String messageId = bundle.getString("messageId");
      String extras = bundle.getString("extras");
      //String url = bundle.getString("cn.jpush.android.ALERT");
      String i = getExtraI(extras);
      boolean n = getExtraN(extras);
      L.i("接到推送:" + i);
      openNotification(context, 1, i, n);
    }
  }

  private void showNotification(Context context, String title, String message, String messageId,
      String extras) {

    if (TextUtils.isEmpty(title)) {
      //title = context.getString(R.string.app_name);
    }
    NotificationCompat.BigTextStyle textStyle = new NotificationCompat.BigTextStyle();
    textStyle
        .setBigContentTitle(title)
        //                .setSummaryText("SummaryText")
        .bigText(message);//支持长文本显示

    int notificationId = new Random().nextInt();
    Intent intent = new Intent(Notification_Open);
    intent.putExtra("extras", extras);
    intent.putExtra("title", title);
    intent.putExtra("message", message);
    intent.putExtra("messageId", messageId);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent,
        PendingIntent.FLAG_UPDATE_CURRENT);
    Notification notify = new NotificationCompat.Builder(context)
        .setSmallIcon(
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? R.mipmap.notification_icon
                : R.mipmap.icon_sales)
        //.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
        //    R.mipmap.icon_sales)) //通知图标以largeicon和smallicon叠加显示
        .setTicker(title)
        .setContentTitle(title)
        .setContentText(message)
        .setStyle(textStyle)
        .setContentIntent(pendingIntent)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)//设置锁屏时候显示通知
        .setPriority(1000)//提高优先级，使能在弹出float window
        .build();
    notify.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS;
    notify.flags |= Notification.FLAG_AUTO_CANCEL;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      notify.color = context.getResources().getColor(android.R.color.holo_orange_light);//设置通知图标背景色
    }
    //NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    NotificationManagerCompat manager = NotificationManagerCompat.from(context);
    manager.notify(notificationId, notify);
  }

  /**
   * 是否需要显示通知。
   */
  private boolean neeedShowNotif(int type) {
    return type >= 1 && type <= 10;
  }

  private void openNotification(Context context, int type, String data, boolean n) {
    switch (type) {
      case 1: //普通类型
        HullActivity.launch4Push(context, data);
        return;
    }
  }

  private int getExtraT(String extras) {
    if (!TextUtils.isEmpty(extras)) {
      try {
        JSONObject jsonObject = new JSONObject(extras);
        return jsonObject.optInt("t");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return -1;
  }

  private String getExtraI(String extras) {
    if (!TextUtils.isEmpty(extras)) {
      try {
        JSONObject jsonObject = new JSONObject(extras);
        return jsonObject.optString("i");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return "";
  }

  private boolean getExtraN(String extras) {
    if (!TextUtils.isEmpty(extras)) {
      try {
        JSONObject jsonObject = new JSONObject(extras);
        return jsonObject.optBoolean("n");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  private void print(Bundle extras) {
    Set<String> keys = extras.keySet();
    for (String key : keys) {
      L.i(key + "," + extras.get(key));
    }
  }
}
