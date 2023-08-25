package com.fluentmusicplayer.app;

import androidx.fragment.app.Fragment;

import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;



public class music_control extends Fragment {

    private SoundPool soundPool;
    private int soundId;


    public music_control() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.activity_music_control, container, false);
        Button return_button = view.findViewById(R.id.return_button);


        return_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        ImageButton previousButton = view.findViewById(R.id.previous_song);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppConstants.setPlayState(2);

            }
        });


        ImageButton pause_botton = view.findViewById(R.id.pause_button);
        ImageButton play_botton = view.findViewById(R.id.play_button);


        if(AppConstants.music_control_first_open == true){

            AppConstants.setPlayState(0);
            AppConstants.play_state = 0;
            pause_botton.setVisibility(View.INVISIBLE);
            play_botton.setVisibility(View.VISIBLE);
            AppConstants.music_control_first_open = false;

        }else{
            if (AppConstants.play_state == 0){

                pause_botton.setVisibility(View.INVISIBLE);
                play_botton.setVisibility(View.VISIBLE);
            }else{
                pause_botton.setVisibility(View.VISIBLE);
                play_botton.setVisibility(View.INVISIBLE);

            }
        }


        pause_botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConstants.play_state == 0){
                    AppConstants.setPlayState(1);
                    AppConstants.play_state = 1;
                    pause_botton.setVisibility(View.VISIBLE);
                    play_botton.setVisibility(View.INVISIBLE);
                }else{
                    AppConstants.setPlayState(0);
                    AppConstants.play_state = 0;
                    pause_botton.setVisibility(View.INVISIBLE);
                    play_botton.setVisibility(View.VISIBLE);

                }

            }
        });

        play_botton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppConstants.play_state == 0){
                    AppConstants.setPlayState(1);
                    AppConstants.play_state = 1;
                    pause_botton.setVisibility(View.VISIBLE);
                    play_botton.setVisibility(View.INVISIBLE);
                }else{
                    AppConstants.setPlayState(0);
                    AppConstants.play_state = 0;
                    pause_botton.setVisibility(View.INVISIBLE);
                    play_botton.setVisibility(View.VISIBLE);

                }

            }
        });

        ImageButton next_button = view.findViewById(R.id.next_song);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppConstants.setPlayState(3);


            }
        });

        String get_file_url = null;
        try {
            get_file_url = AppConstants.REQUEST_ADDRESS + "song/url?id=" + AppConstants.now_play_id + "&cookie=" + URLEncoder.encode(AppConstants.cookie, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        String savePath = AppConstants.data_path_name + "files/music/" + AppConstants.now_play_id + ".mp3";



        String Get_file_url_result = HttpRequest.sendGetRequest(get_file_url);


        File music_chace_file = new File(savePath);

        if (!music_chace_file.exists()) {
            //本地有对应缓存的情况
            AppConstants.have_chase = true;
        }else{
            AppConstants.have_chase = false;
        }


        if (Get_file_url_result != null){
            try{
                JSONObject jsonObject = new JSONObject(Get_file_url_result);
                JSONArray dataArray = jsonObject.getJSONArray("data");
                JSONObject dataObject = dataArray.getJSONObject(0);
                String Get_music_url = dataObject.getString("url");
                Log.d("Get_link",Get_music_url);

                download_file fileDownloader = new download_file();
                fileDownloader.execute(Get_music_url, savePath);

                AppConstants.music_path = savePath;
            }catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }





        return view;
    }
}