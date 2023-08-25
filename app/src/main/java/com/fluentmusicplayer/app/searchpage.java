package com.fluentmusicplayer.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fluentmusicplayer.app.AppConstants;



public class searchpage extends AppCompatActivity {

    private GestureDetectorCompat gestureDetector;
    private Handler handler;
    private LinearLayout linearLayout;
    boolean isSwipe = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchpage);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏

        Button return_button = findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



        gestureDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                // 判断手势是否为左滑
                if (e1.getX() < e2.getX() / 2) {
                    finish();
                } else {
                    isSwipe = true;
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                // 每次手指按下时重置isSwipe标志为false
                isSwipe = false;
                return super.onDown(e);
            }
        });

        Button searchButton = findViewById(R.id.button_search);
        linearLayout = findViewById(R.id.linear_layout);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Tag", "Debug message");
                // 隐藏软键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                // 获取搜索关键词
                EditText editText = findViewById(R.id.editText);
                String keyword = editText.getText().toString();

                linearLayout.removeAllViews();

                // 发起搜索请求
                searchSongs(keyword);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void searchSongs(String keyword) {
        String request_address = AppConstants.REQUEST_ADDRESS;
        String url = request_address + "search?keywords=" + keyword;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject result = response.getJSONObject("result");
                            JSONArray songs = result.getJSONArray("songs");

                            int songCount = Math.min(songs.length(), 30); // 最多展示30首歌曲

                            for (int i = 0; i < songCount; i++) {
                                JSONObject song = songs.getJSONObject(i);
                                String songName = song.getString("name");

                                JSONArray artists = song.getJSONArray("artists");
                                StringBuilder artistNames = new StringBuilder();
                                for (int j = 0; j < artists.length(); j++) {
                                    JSONObject artist = artists.getJSONObject(j);
                                    String artistName = artist.getString("name");
                                    artistNames.append(artistName).append(", ");
                                }
                                artistNames.delete(artistNames.length() - 2, artistNames.length()); // 删除最后的逗号和空格

                                int songId = song.getInt("id");

                                addSongView(songName, artistNames.toString(), songId);
                            }

                            Log.d("TAG", "Response: " + response.toString()); // 输出返回的JSON数据
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        Volley.newRequestQueue(this).add(request);
    }

    private void addSongView(String songName, String artistNames, int songId) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int paddingStartEnd = (int) getResources().getDimension(R.dimen.textview_padding_start_end); // 获取5dp对应的像素值
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        layoutParams.setMargins(margin, margin, margin, margin);

        LinearLayout songLayout = new LinearLayout(this);
        songLayout.setLayoutParams(layoutParams);
        songLayout.setBackgroundResource(R.drawable.item_border); // 设置白色描边背景
        songLayout.setOrientation(LinearLayout.VERTICAL);


        TextView songTextView = new TextView(this);
        songTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        songTextView.setTextAppearance(this, R.style.TextAppearance_AppCompat_Medium);
        songTextView.setPadding(margin, margin, margin, margin);
        songTextView.setText(songName);
        songTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15); // 设置字号为11sp
        songTextView.setGravity(Gravity.CENTER_VERTICAL);
        songTextView.setTextColor(getResources().getColor(R.color.white));
        songTextView.setPadding(paddingStartEnd, 0, paddingStartEnd, 0);


        TextView artistTextView = new TextView(this);
        artistTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        artistTextView.setText(artistNames);
        artistTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8); // 设置字号为8sp
        artistTextView.setPadding(margin, margin, margin, margin);
        artistTextView.setGravity(Gravity.CENTER_VERTICAL);
        artistTextView.setTextColor(getResources().getColor(R.color.white_dark));
        artistTextView.setPadding(paddingStartEnd, 0, paddingStartEnd, 0);

        TextView idTextView = new TextView(this);
        idTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        idTextView.setText("ID：" + songId);
        idTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8); // 设置字号为8sp
        idTextView.setPadding(margin, margin, margin, margin);
        idTextView.setGravity(Gravity.CENTER_VERTICAL);
        idTextView.setTextColor(getResources().getColor(R.color.white_dark));
        idTextView.setPadding(paddingStartEnd, 0, paddingStartEnd, 0);

        songLayout.addView(songTextView);
        songLayout.addView(artistTextView);
        songLayout.addView(idTextView);

        songLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSwipe) {
                    String idValue = idTextView.getText().toString();
                    Toast.makeText(searchpage.this, "" + idValue, Toast.LENGTH_SHORT).show();
                }
            }
        });


        linearLayout.addView(songLayout);
        songLayout.setBackgroundResource(R.drawable.result_round_corner);
    }
}
