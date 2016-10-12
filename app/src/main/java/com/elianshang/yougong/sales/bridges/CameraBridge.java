package com.elianshang.yougong.sales.bridges;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;
import com.elianshang.yougong.sales.utils.L;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
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
  private static String imagePath;

  public static void bindToWebview(BridgeWebView bridgeWebView, final Activity activity,
      final int requestCode) {
    bridgeWebView.registerHandler(BRIDGE_NAME, new BridgeHandler() {
      @Override
      public void handler(String data, CallBackFunction function) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File out = new File(Environment.getExternalStorageDirectory(),
            "camera.jpg");
        imagePath = out.getAbsolutePath();
        Uri uri = Uri.fromFile(out);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        L.i("开启相机");
        activity.startActivityForResult(intent, requestCode);
        callBackFunction = function;
      }
    });
  }

  public static void handleActivityResult(int resultCode, Intent bundle) {
    if (callBackFunction == null) return;
    if (resultCode == Activity.RESULT_OK) {
      new AsyncTask<Void, Void, JSONObject>() {

        @Override protected JSONObject doInBackground(Void... params) {
          JSONObject jo = new JSONObject();
          File save = new File(imagePath);
          ByteArrayOutputStream baos = compressBitmap();
          String base = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

          try {
            jo.put(ResultConstants.errorCode, ResultConstants.ERRORCODE_SUCCESS);
            jo.put("root", imagePath);
            jo.put("uri", "" + Uri.fromFile(save).toString());
            jo.put("base64", base);
          } catch (JSONException e) {
            e.printStackTrace();
            try {
              jo.put(ResultConstants.errorCode, ResultConstants.ERRORCODE_FAIL);
            } catch (JSONException e1) {
              e1.printStackTrace();
            }
          }
          if (baos != null) {
            try {
              baos.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          save.delete();
          i(jo.toString());
          return jo;
        }

        @NonNull private ByteArrayOutputStream compressBitmap() {
          BitmapFactory.Options option = new BitmapFactory.Options();
          option.inSampleSize = 5;
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          Bitmap image = BitmapFactory.decodeFile(imagePath);
          image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
          int options = 30;
          while (baos.toByteArray().length / 1024 > 900) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
          }
          image.recycle();
          return baos;
        }

        @Override protected void onPostExecute(JSONObject result) {
          super.onPostExecute(result);
          callBackFunction.onCallBack(result.toString());
        }
      }.execute();
    } else if (resultCode == Activity.RESULT_CANCELED) {
      callBackFunction.onCallBack(ResultConstants.makeErrorResult("拍照被取消"));
    } else {
      callBackFunction.onCallBack(ResultConstants.makeErrorResult("拍照出错"));
    }
  }
}
