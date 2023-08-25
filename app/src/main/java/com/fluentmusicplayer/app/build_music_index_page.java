package com.fluentmusicplayer.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class build_music_index_page extends AppCompatActivity {
    private Handler handler;
    private GestureDetectorCompat gestureDetector;
    private Button start_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_music_index_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Button start_button = findViewById(R.id.start_button);
        start_button = findViewById(R.id.start_button);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            start_button.setText("构建中...");
            build_index task = new build_index();
            task.execute();
            }
        });

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
    }


    private class build_index extends AsyncTask<Void, Void, ArrayList<HashMap<String, String>>> {
        @Override
        protected ArrayList<HashMap<String, String>> doInBackground(Void... voids) {
            ArrayList<HashMap<String, String>> music_list = new ArrayList<>();

            String getUserFavouriteLink = null;
            try {
                getUserFavouriteLink = AppConstants.REQUEST_ADDRESS + "likelist?uid=" + AppConstants.userID + "&cookie=" + URLEncoder.encode(AppConstants.cookie, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            String all_like_data = HttpRequest.sendGetRequest(getUserFavouriteLink);

            Log.d("build_index", all_like_data);

            try {
                JSONObject jsonObject = new JSONObject(all_like_data);
                JSONArray idsArray = jsonObject.getJSONArray("ids");

                for (int i = 0; i < idsArray.length(); i++) {
                    long song_id = idsArray.getLong(i);

                    String Get_music_detail_address = AppConstants.REQUEST_ADDRESS + "song/detail?ids=";
                    String Get_music_detail_result = HttpRequest.sendGetRequest(Get_music_detail_address + Long.toString(song_id));

                    try {
                        JSONObject music_detail_info = new JSONObject(Get_music_detail_result);
                        JSONArray songsArray = music_detail_info.getJSONArray("songs");

                        if (songsArray.length() > 0) {
                            JSONObject songObject = songsArray.getJSONObject(0);
                            String name = songObject.getString("name");
                            int id = songObject.getInt("id");
                            JSONArray artistArray = songObject.getJSONArray("ar");
                            StringBuilder artists = new StringBuilder();
                            for (int a = 0; a < artistArray.length(); a++) {
                                String artistName = artistArray.getJSONObject(a).getString("name");
                                artists.append(artistName);
                                if (a < artistArray.length() - 1) {
                                    artists.append("; ");
                                }
                            }
                            String album = songObject.getJSONObject("al").getString("name");
                            String albumId = songObject.getJSONObject("al").getString("id"); // 获取专辑ID

                            HashMap<String, String> music_hashMap = new HashMap<>();
                            music_hashMap.put("name",name);
                            music_hashMap.put("artist",artists.toString());
                            music_hashMap.put("music_id",Integer.toString(id));
                            music_hashMap.put("album_name",album);
                            music_hashMap.put("album_id",albumId);

                            music_list.add(music_hashMap);

                            Log.d("build_index", music_hashMap.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    // 将music_list转换为JSON字符串
                    JSONArray jsonMusicList = new JSONArray(music_list);
                    JSONObject allMusicJsonObject = new JSONObject();
                    allMusicJsonObject.put("all_music", jsonMusicList);
                    allMusicJsonObject.put("list", idsArray);
                    String allMusicJson = allMusicJsonObject.toString();
                    Log.d("build_index", allMusicJson);
                    Write_File_To_Data.writeToFile(getApplicationContext(), "music_index", allMusicJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject recent_add = new JSONObject();
                JSONArray recent_add_array = new JSONArray();
                recent_add.put("recent_add", recent_add_array);
                String jsonString = recent_add.toString(); // 将JSONObject转换为字符串
                Write_File_To_Data.writeToFile(getApplicationContext(), "recent_add", jsonString);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return music_list;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String, String>> music_list) {
            start_button.setText("构建完成");
        }
    }

//这是类的引号


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}