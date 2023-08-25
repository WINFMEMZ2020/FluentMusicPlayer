package com.fluentmusicplayer.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import com.fluentmusicplayer.app.AppConstants;
import com.fluentmusicplayer.app.HttpRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import com.fluentmusicplayer.app.AppConstants;


public class login_page extends AppCompatActivity {
    private Handler handler;
    private GestureDetectorCompat gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
        //创建一个新的手势监视器
        gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 判断手势是否为左滑
                if (e1.getX() < e2.getX() / 2) {
                    finish();
                }
                return true;
            }
        });



        String request_address = AppConstants.REQUEST_ADDRESS;
        String request_key_address = request_address + "login/qr/key";

        String result = HttpRequest.sendGetRequest(request_key_address);
        Log.d("result", result);
        //解析json
        String unikey = null;
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(result, JsonObject.class);
            unikey = jsonObject.getAsJsonObject("data").get("unikey").getAsString();
            Log.d("result", "unikey=" + unikey);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String create_QRcode_address = request_address + "login/qr/create?key=" + unikey + "&qrimg=true";
        Log.d("result", "QRcode_address=" + create_QRcode_address);

        String QRcode_address_result = HttpRequest.sendGetRequest(create_QRcode_address);
        Log.d("QRcode_address_result=", QRcode_address_result);
        //解析返回的json数据，获取图片base64

        String QRcode_base64 = null;
        try {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(QRcode_address_result, JsonObject.class);
            QRcode_base64 = jsonObject.getAsJsonObject("data").get("qrimg").getAsString();
            Log.d("result", "qrimg=" + QRcode_base64);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //解码返回的base64图片
        String base64ImageData = QRcode_base64.substring(QRcode_base64.indexOf(",") + 1);

        byte[] decodedString = Base64.decode(base64ImageData, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(decodedBitmap);



        TextView textView = findViewById(R.id.status_show);

        handler = new Handler();
        String finalUnikey = unikey;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String scan_status_check_address = request_address + "login/qr/check?key=" + finalUnikey + "&noCookie=true&timestamp=" + System.currentTimeMillis();
                //Log.d("result", "check_address=" + scan_status_check_address);
                String scan_status_check_address_result = HttpRequest.sendGetRequest(scan_status_check_address);

                try {
                    JSONObject responseJson = new JSONObject(scan_status_check_address_result);
                    int code = responseJson.getInt("code");
                    String message = responseJson.getString("message");
                    //Log.d("response",responseJson.toString());
                    //Log.d("response","scan_status_check_address=" + scan_status_check_address);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 根据返回的结果更新TextView的文字内容
                            if (code == 801) {
                                textView.setText("等待扫码...\n请使用手机网易云音乐APP扫码登录");
                            } else if (code == 802) {
                                textView.setText("扫码成功\n请在手机端确认");
                            } else if (code == 803) {
                                //获取cookie
                                JSONObject jsonObject = null;
                                try {
                                    jsonObject = new JSONObject(responseJson.toString());
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                String cookie = null;
                                try {
                                    cookie = jsonObject.getString("cookie");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                Write_File_To_Data.writeToFile(getApplicationContext(), "cookie", cookie);
                                AppConstants.cookie = cookie;
                                textView.setText("登录成功\n页面即将返回...");
                                AppConstants.refresh_login_status = 1;
                                //延时1000ms退出页面
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setResult(RESULT_OK);
                                        finish();
                                    }
                                }, 3000);


                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // 800毫秒后再次执行任务
                handler.postDelayed(this, 800);
            }
        };


        // 开始循环任务
        handler.post(runnable);



    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 停止循环任务
        handler.removeCallbacksAndMessages(null);
    }

}