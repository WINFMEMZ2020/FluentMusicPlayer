package com.fluentmusicplayer.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class User_Agreement extends AppCompatActivity {

    private GestureDetectorCompat gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏


        gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 判断手势是否为左滑
                if (e1.getX() < e2.getX() / 2) {
                    finishAffinity();  // 结束当前Activity及其所有关联的Activity
                    System.exit(0);   // 强制退出整个应用程序
                }
                return true;
            }
        });


        Button iagree_button = findViewById(R.id.iagree);
        iagree_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FileOutputStream outputStream = openFileOutput("agreed", Context.MODE_PRIVATE); //用户同意协议，写一个agreed文件
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);  //我TM也不知道为什么强制要求要抛出异常
                }

                Intent intent = new Intent(User_Agreement.this, MainActivity.class);
                startActivity(intent);

            }
        });


        Button idontgagree_button = findViewById(R.id.idontagree);
        idontgagree_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    finishAffinity();  // 结束当前Activity及其所有关联的Activity
                    System.exit(0);   // 强制退出整个应用程序
            }
        });



    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}