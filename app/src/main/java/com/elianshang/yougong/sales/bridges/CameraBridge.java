package com.elianshang.yougong.sales.bridges;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import org.json.JSONException;
import org.json.JSONObject;

import static com.elianshang.yougong.sales.utils.L.*;

/**
 * @author zjh
 * @description
 * @date 16/8/23.
 */
public class CameraBridge {

  public final static String BRIDGE_NAME = "takePhotoes_bridge";
  private static CallBackFunction callBackFunction;

  public static void bindToWebview(BridgeWebView bridgeWebView, final Activity activity,
      final int requestCode) {
    bridgeWebView.registerHandler(BRIDGE_NAME, new BridgeHandler() {
      @Override
      public void handler(String data, CallBackFunction function) {
        activity.startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), requestCode);
        callBackFunction = function;
      }
    });
  }

  public static void handleActivityResult(Bundle bundle) {
    if (callBackFunction == null) return;
    Bitmap bm = (Bitmap) bundle.get("data");
    JSONObject jo = new JSONObject();
    File save = saveBitmap(bm);
    try {
      jo.put(ResultConstants.errorCode,ResultConstants.ERRORCODE_SUCCESS);
      jo.put("root", "" + save.getAbsolutePath());
      jo.put("uri", "" + Uri.fromFile(save).toString());
      jo.put("base64", bitmaptoString(bm));
    } catch (JSONException e) {
      e.printStackTrace();
    }
    i(jo.toString());
    callBackFunction.onCallBack(jo.toString());
  }

  public static String bitmaptoString(Bitmap bitmap) {
    // 将Bitmap转换成Base64字符串
    StringBuffer string = new StringBuffer();
    ByteArrayOutputStream bStream = new ByteArrayOutputStream();

    try {
      bitmap.compress(Bitmap.CompressFormat.WEBP, 100, bStream);
      bStream.flush();
      bStream.close();
      byte[] bytes = bStream.toByteArray();
      string.append(Base64.encodeToString(bytes, Base64.DEFAULT));
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println("string.." + string.length());
    return string.toString();
  }


  public static String MD5_Hash(String s) {
    MessageDigest m = null;

    try {
      m = MessageDigest.getInstance("MD5");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    m.update(s.getBytes(),0,s.length());
    String hash = new BigInteger(1, m.digest()).toString(16);
    return hash;
  }



  public static File saveBitmap(Bitmap bitmap) {
    String sdcardState = Environment.getExternalStorageState();
    if (Environment.MEDIA_UNMOUNTED.equals(sdcardState)) {
      return null;
    }
    String name = String.valueOf(UUID.randomUUID());
    File out = new File(Environment.getExternalStorageDirectory(), name + ".jpg");
    if (!out.exists()) {
      try {
        out.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try {
      FileOutputStream fos = new FileOutputStream(out);
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      fos.close();

      return out;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }
}
