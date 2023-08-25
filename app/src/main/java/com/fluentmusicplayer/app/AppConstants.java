package com.fluentmusicplayer.app;

import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;

public class AppConstants {
    public static final String REQUEST_ADDRESS = "http://47.92.125.110:3000/";
    public static final String package_name = "com.fluentmusicplayer.app";
    public static  final String data_path_name = "/data/data/" +  package_name + "/";
    public static int refresh_login_status = 0;
    public static String cookie = "";
    public static int login_status = 0;
    public static String userID = "";

    public static JSONArray music_detail_data = new JSONArray();
    public static JSONArray recent_add_data = new JSONArray();
    public static int now_play_id = 0;
    public static int play_state = 0;  //0:无播放 1：正在播放 2：上一曲 3：下一曲
    public static String music_path = "";
    public static boolean have_chase = false;


    private static MutableLiveData<Integer> playStateLiveData = new MutableLiveData<>();
    private static int playState;
    public static boolean music_control_first_open = true;

    public static MutableLiveData<Integer> getPlayStateLiveData() {
        return playStateLiveData;
    }

    public static void setPlayState(int state) {
        playState = state;
        playStateLiveData.setValue(playState);
    }

}
