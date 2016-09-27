package com.elianshang.yougong.sales.bridges;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import com.elianshang.yougong.sales.utils.L;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 需要启动activity的桥 都要在继承这个baseactivity的activity中注册
 *
 * @author zjh
 * @description
 * @date 16/8/23.
 */
public class BaseBridgeActivity extends AppCompatActivity {
    public static final int REQUESTCODE_TAKE_PHOTO = 0x01;


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        print(data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUESTCODE_TAKE_PHOTO:
                    CameraBridge.handleActivityResult(data.getExtras());
                    break;
            }
        }
    }

    private void print(Intent data) {
        if (data == null) return;
        JSONObject jo = new JSONObject();
        Bundle b = data.getExtras();
        if (b == null) return;
        Set<String> set = b.keySet();
        for (String s : set) {
            L.i(s + "," + b.get(s));
            try {
                jo.put(s, b.get(s));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
