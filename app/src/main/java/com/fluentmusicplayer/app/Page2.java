package com.fluentmusicplayer.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class Page2 extends AppCompatActivity {
    private GestureDetectorCompat gestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 判断手势是否为左滑
                if (e1.getX() < e2.getX() / 2) {
                    Intent intent = new Intent(Page2.this, MainActivity.class);
                    finish();
                }
                return true;
            }
        });


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Page2.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

}