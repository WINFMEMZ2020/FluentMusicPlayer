package com.fluentmusicplayer.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class recent_add extends AppCompatActivity {
    boolean isSwipe = false;
    private GestureDetectorCompat gestureDetector;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_add);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        linearLayout = findViewById(R.id.linear_layout);

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

        Button return_button = findViewById(R.id.return_button);
        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recent_add.Update_songs_info update_task = new recent_add.Update_songs_info();
        update_task.execute();

        recent_add.ShowUserFavourite Showuserfavouritetask = new recent_add.ShowUserFavourite();
        Showuserfavouritetask.execute();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }



    public class Update_songs_info extends AsyncTask<Void, Void, String> {
        String getUserFavouriteLink;
        @Override
        protected String doInBackground(Void... voids) {
            try {
                getUserFavouriteLink = AppConstants.REQUEST_ADDRESS + "likelist?uid=" + AppConstants.userID + "&cookie=" + URLEncoder.encode(AppConstants.cookie, "UTF-8") + "&timestamp=" + System.currentTimeMillis();
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            String Getuserfavourire_result = HttpRequest.sendGetRequest(getUserFavouriteLink);

            try {
                JSONObject user_favourite = new JSONObject(Getuserfavourire_result);
                JSONArray idsArray = user_favourite.getJSONArray("ids");
                Log.d("update_favourite", idsArray.toString());

                String fileName_musicdict = AppConstants.data_path_name + "files/music_index";
                String fileName_recent_add = AppConstants.data_path_name + "files/recent_add";

                StringBuilder music_dict_content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(fileName_musicdict))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        music_dict_content.append(line);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONObject all_music_data = new JSONObject(music_dict_content.toString());
                JSONArray local_listArray = all_music_data.getJSONArray("list");
                JSONArray music_data = all_music_data.getJSONArray("all_music");


                StringBuilder recent_add_content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(fileName_recent_add))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        recent_add_content.append(line);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                JSONObject recent_data = new JSONObject(recent_add_content.toString());
                JSONArray recent_listArray = recent_data.getJSONArray("recent_add");



                // Log.d("update_favourite","list=" + listArray.toString());

                List<String> listA = new ArrayList<>();
                List<String> listB = new ArrayList<>();

                for (int i = 0; i < idsArray.length(); i++) {
                    try {
                        String element = idsArray.getString(i);
                        listA.add(element);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < local_listArray.length(); i++) {
                    try {
                        String element = local_listArray.getString(i);
                        listB.add(element);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                List<String> user_delete_items = new ArrayList<>(listB);
                user_delete_items.removeAll(listA); // A没有而B有的元素

                List<String> user_added_items = new ArrayList<>(listA);
                user_added_items.removeAll(listB); // A有而B没有的元素


                Log.d("update_favourite", "listA=" + listA.toString());
                Log.d("update_favourite", "listB=" + listB.toString());
                Log.d("update_favourite", "user_delete_items=" + user_delete_items.toString());
                Log.d("update_favourite", "user_added_items=" + user_added_items.toString());
                Log.d("update_favourite", "music_data=" + music_data.toString());
                Log.d("update_favourite", "music_data_length=" + music_data.length());


                //处理用户删除项
                if (user_delete_items != null && !user_delete_items.isEmpty()) {
                    for (int i = music_data.length() - 1; i >= 0; i--) {
                        try {
                            JSONObject music = music_data.getJSONObject(i);
                            int musicId = music.optInt("music_id");

                            for (String value : user_delete_items) {
                                try {
                                    int intValue = Integer.parseInt(value);
                                    if (intValue == musicId) {
                                        music_data.remove(i);
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }


                //处理用户增加项
                for (int i = 0; i < user_added_items.size(); i++) {
                    Log.d("update_favourite","user add music id =" + user_added_items.get(i));
                    String music_detail_info_result = null;
                    music_detail_info_result = HttpRequest.sendGetRequest(AppConstants.REQUEST_ADDRESS + "song/detail?ids=" + user_added_items.get(i));
                    JSONObject music_detail_info = new JSONObject(music_detail_info_result);
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

                        JSONObject json = new JSONObject();
                        json.put("name", name);
                        json.put("artist", artists.toString());
                        json.put("music_id", Integer.toString(id));
                        json.put("album_name", album);
                        json.put("album_id", albumId);
                        //Log.d("update_favourite",json.toString());
                        music_data.put(json);
                        recent_listArray.put(json);
                        //Log.d("update_recent",recent_listArray.toString());
                    }

                }

                JSONObject final_data = new JSONObject();
                final_data.put("all_music", music_data);
                final_data.put("list", idsArray);
                AppConstants.music_detail_data = music_data;
                Write_File_To_Data.writeToFile(getApplicationContext(), "music_index", final_data.toString());

                //接下来更新最近添加
                if (user_delete_items != null && !user_delete_items.isEmpty()) {
                    for (int i = recent_listArray.length() - 1; i >= 0; i--) {
                        try {
                            JSONObject music = recent_listArray.getJSONObject(i);
                            int musicId = music.optInt("music_id");

                            for (String value : user_delete_items) {
                                try {
                                    int intValue = Integer.parseInt(value);
                                    if (intValue == musicId) {
                                        recent_listArray.remove(i);
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("update_recent","catch e:" +  e);
                        }
                    }
                }
                Log.d("update_recent","done:delete");
                JSONObject recent_done = new JSONObject();
                recent_done.put("recent_add",recent_listArray);
                AppConstants.recent_add_data = recent_listArray;
                String jsonString = recent_done.toString();
                Write_File_To_Data.writeToFile(getApplicationContext(), "recent_add", jsonString);
                Log.d("update_favourite","update done.");

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            return Getuserfavourire_result;
        }
    }
    private class ShowUserFavourite extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            return "";
        }

        @Override
        protected void onPostExecute(String idsJsonResponse) {

            JSONArray recent_add_info_array = new JSONArray();
            recent_add_info_array = AppConstants.recent_add_data;

            for (int i = recent_add_info_array.length() - 1; i >= 0; i--) {
                JSONObject  music_detail_json = null;
                try {
                    music_detail_json = (JSONObject) recent_add_info_array.get(i);
                    int id = music_detail_json.optInt("music_id");
                    String songName = music_detail_json.optString("name");
                    String artistNames = music_detail_json.optString("artist");
                    Log.d("FetchFavouriteSongsTask","id = " + id);
                    Log.d("FetchFavouriteSongsTask","name = " + songName);
                    Log.d("FetchFavouriteSongsTask","artist = " + artistNames);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    int paddingStartEnd = (int) getResources().getDimension(R.dimen.textview_padding_start_end); // 获取5dp对应的像素值
                    layoutParams.setMargins(5, 3, 5, 3);
                    LinearLayout newLinearLayout = new LinearLayout(recent_add.this);
                    newLinearLayout.setOrientation(LinearLayout.VERTICAL);
                    newLinearLayout.setBackgroundResource(R.drawable.result_round_corner);
                    newLinearLayout.setLayoutParams(layoutParams);

                    TextView songIdTextView = new TextView(recent_add.this);
                    songIdTextView.setText("ID: " + id); // 显示歌曲ID
                    songIdTextView.setTextColor(getResources().getColor(R.color.white_dark));
                    songIdTextView.setPadding(paddingStartEnd, 0, paddingStartEnd, 0); // 设置文字与LinearLayout的边距为5dp
                    TextView songTextView = new TextView(recent_add.this);
                    songTextView.setText("Name: " + songName);
                    songTextView.setTextColor(getResources().getColor(R.color.white));
                    songTextView.setPadding(paddingStartEnd, 0, paddingStartEnd, 0);
                    TextView artistTextView = new TextView(recent_add.this);
                    artistTextView.setText("Artist: " + artistNames);
                    artistTextView.setTextColor(getResources().getColor(R.color.white_dark));
                    artistTextView.setPadding(paddingStartEnd, 0, paddingStartEnd, 0);


                    newLinearLayout.addView(songTextView);
                    newLinearLayout.addView(artistTextView);
                    newLinearLayout.addView(songIdTextView);



                    newLinearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!isSwipe) {
                                AppConstants.now_play_id = id; // 设置当前播放的歌曲ID
                                //Toast.makeText(favourite_songs.this, "ID="+ AppConstants.now_play_id, Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(recent_add.this, viewpager_play_page.class);
                                startActivity(intent);


                            }
                        }
                    });



                    linearLayout.addView(newLinearLayout);


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                //Log.d("FetchFavouriteSongsTask",music_detail_json.toString());
            }
        }
    }

}