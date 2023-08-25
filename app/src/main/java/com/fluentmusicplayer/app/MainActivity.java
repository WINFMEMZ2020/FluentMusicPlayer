package com.fluentmusicplayer.app;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    //public static final String REQUEST_ADDRESS = "http://47.92.125.110:3000/";
    private static final int LOGIN_REQUEST_CODE = 1;
    private static final int SEARCH_REQUEST_CODE = 2;

    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String filePath = "/data/data/" + AppConstants.package_name + "/files/cookie";
        File file = new File(filePath);
        if (file.exists()) {
            AppConstants.login_status = 1;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            byte[] buffer = new byte[(int) file.length()];
            try {
                fis.read(buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String cookieData = new String(buffer);
            AppConstants.cookie = cookieData; //读取cookie
            String user_info_result = null;
            try {
                user_info_result = HttpRequest.sendGetRequest(AppConstants.REQUEST_ADDRESS + "login/status?cookie=" + URLEncoder.encode(AppConstants.cookie, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            Log.d("Appinfo", user_info_result);

            try {
                JSONObject jsonObject = new JSONObject(user_info_result);

                JSONObject dataObject = jsonObject.getJSONObject("data");
                if (!dataObject.isNull("profile")) {
                    // 获取需要的字段值
                    String userId = jsonObject.getJSONObject("data").getJSONObject("profile").getString("userId");
                    String nickname = jsonObject.getJSONObject("data").getJSONObject("profile").getString("nickname");
                    String avatarUrl = jsonObject.getJSONObject("data").getJSONObject("profile").getString("avatarUrl");
                    AppConstants.userID = userId;
                    Log.d("favourite_page",AppConstants.userID);
                    TextView textView = findViewById(R.id.nickname_show);
                    textView.setText(nickname);
                    ImageView imageView = findViewById(R.id.avatar_show);
                    Picasso.get().load(avatarUrl).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Bitmap circularBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                            Paint paint = new Paint();
                            paint.setShader(shader);
                            Canvas canvas = new Canvas(circularBitmap);
                            float radius = bitmap.getWidth() / 2f;
                            canvas.drawCircle(radius, radius, radius, paint);
                            imageView.setImageBitmap(circularBitmap);
                            imageView.setBackgroundColor(Color.TRANSPARENT);
                            AppConstants.login_status = 1;//设置登录状态
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            // 处理加载失败的情况
                            Toast.makeText(getApplicationContext(), "图片加载失败", Toast.LENGTH_LONG).show();
                            AppConstants.login_status = 1;
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            // 图片加载之前的准备工作：暂无
                        }
                    });
                } else {
                    //返回的数据为null
                    AppConstants.login_status = 0;//设置登录状态
                }

            } catch (Exception e) {
                e.printStackTrace();//我TM也不知道为什么强制我捕获异常
                Log.e("Appinfo", "捕获到异常:", e);
            }

        } else {
            //找不到这个文件，那就是没登录
            AppConstants.login_status = 0;//设置登录状态
        }


        File files_data_dir = getFilesDir();
        File agreedFile = new File(files_data_dir, "agreed");

        if (!agreedFile.exists()) {
            Intent intent = new Intent(this, User_Agreement.class);
            startActivity(intent);
            finish();
        }



    }





    public void onLinearLayoutClick(View view) {
        int id = view.getId();
        if (id == R.id.user_center) {
            if (AppConstants.login_status == 0){

                Intent intent = new Intent(MainActivity.this, login_page.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }else{
                Intent intent = new Intent(MainActivity.this, user_center.class);
                startActivity(intent);
            }
        } else if (id == R.id.search_button) {
            Intent intent = new Intent(MainActivity.this, searchpage.class);
            startActivity(intent);
        } else if (id == R.id.my_favourite_button) {
            //File index_file = new File(AppConstants.data_path_name + "music_index");
            //File files_data_dir = getFilesDir();
            File music_index_file = new File(AppConstants.data_path_name + "files/music_index");
            File recent_add_file = new File (AppConstants.data_path_name + "files/recent_add");
            if (music_index_file.exists() && recent_add_file.exists()){
                Intent intent = new Intent(MainActivity.this, favourite_songs.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(MainActivity.this, build_music_index_page.class);
                startActivity(intent);
            }

        }else if (id == R.id.now_playing) {
            //来点不怎么好看的动画（真TM难写）
            LinearLayout now_playing = (LinearLayout) view;
            TextView now_playing_text = now_playing.findViewById(R.id.now_playing_text_show);
            ImageView now_playing_pic = now_playing.findViewById(R.id.now_playing_pic_show);

            // 设置不可见
            now_playing_text.setVisibility(View.INVISIBLE);
            now_playing_pic.setVisibility(View.INVISIBLE);

            // 获取屏幕宽度和高度
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            final int screenWidth = displayMetrics.widthPixels;
            final int screenHeight = displayMetrics.heightPixels;

            // 计算缩放比例
            final float scaleX = (float) screenWidth / view.getWidth();
            final float scaleY = (float) screenHeight / view.getHeight();

            // 创建属性动画对象
            ValueAnimator scaleAnimator = ValueAnimator.ofFloat(0, 2);
            scaleAnimator.setDuration(400);
            scaleAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

            scaleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    now_playing.setScaleX(value * scaleX);
                    now_playing.setScaleY(value * scaleY);
                }
            });

            scaleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            now_playing.setScaleX(1);
                            now_playing.setScaleY(1);
                            now_playing_text.setVisibility(View.VISIBLE);
                            now_playing_pic.setVisibility(View.VISIBLE);
                        }
                    }, 300);

                    getWindow().setDimAmount(0f);

                    // 启动新的Activity
                    Intent intent = new Intent(MainActivity.this, viewpager_play_page.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, 0);
                }
            });

            scaleAnimator.start();
        }else if(id == R.id.recent_add){
            Intent intent = new Intent(MainActivity.this, recent_add.class);
            startActivity(intent);

        }


    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_REQUEST_CODE) { // 判断requestCode是否是从login_page返回的结果
            //重新检测登录状态，并加载图片
            String user_info_result = null;
            try {
                user_info_result = HttpRequest.sendGetRequest(AppConstants.REQUEST_ADDRESS + "login/status?cookie=" + URLEncoder.encode(AppConstants.cookie, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            Log.d("Appinfo",user_info_result);
            try {
                JSONObject jsonObject = new JSONObject(user_info_result);
                JSONObject dataObject = jsonObject.getJSONObject("data");
                if (!dataObject.isNull("profile")) {
                    // 获取需要的字段值
                    String userId = jsonObject.getJSONObject("data").getJSONObject("profile").getString("userId");
                    String nickname = jsonObject.getJSONObject("data").getJSONObject("profile").getString("nickname");
                    String avatarUrl = jsonObject.getJSONObject("data").getJSONObject("profile").getString("avatarUrl");
                    AppConstants.userID = userId;
                    Log.d("favourite_page",AppConstants.userID);
                    TextView textView = findViewById(R.id.nickname_show);
                    textView.setText(nickname);
                    ImageView imageView = findViewById(R.id.avatar_show);

                    Picasso.get().load(avatarUrl).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            Bitmap circularBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                            BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                            Paint paint = new Paint();
                            paint.setShader(shader);

                            Canvas canvas = new Canvas(circularBitmap);
                            float radius = bitmap.getWidth() / 2f;
                            canvas.drawCircle(radius, radius, radius, paint);

                            imageView.setImageBitmap(circularBitmap);
                            imageView.setBackgroundColor(Color.TRANSPARENT);
                            AppConstants.login_status= 1 ;//设置登录状态
                        }
                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            // 处理加载失败的情况
                        }
                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            // 图片加载之前的准备工作
                        }
                    });
                }else {
                    // profile为null，很简单，什么也不做。
                    AppConstants.login_status= 0 ;//设置登录状态
                }
            } catch (Exception e) {
                e.printStackTrace();//我TM也不知道为什么强制我捕获异常
                Log.e("Appinfo", "捕获到异常:", e);
            }
            //Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();

        }
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}