package com.fluentmusicplayer.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class viewpager_play_page extends AppCompatActivity {

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_top); // 设置退出动画
    }




    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager_play_page);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        LayoutInflater inflater = LayoutInflater.from(viewpager_play_page.this);
        View music_control_view = inflater.inflate(R.layout.activity_music_control, null);
        AppConstants.getPlayStateLiveData().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer newState) {
                if (newState == 0){
                    //Toast.makeText(viewpager_play_page.this, "当前状态: " + newState + "\n暂停", Toast.LENGTH_SHORT).show();
                }else if(newState == 1){
                    //Toast.makeText(viewpager_play_page.this, "当前状态: " + newState + "\n播放", Toast.LENGTH_SHORT).show();
                }else if(newState == 2){
                    //Toast.makeText(viewpager_play_page.this, "当前状态: " + newState + "\n上一曲", Toast.LENGTH_SHORT).show();
                }else if(newState == 3){
                    //Toast.makeText(viewpager_play_page.this, "当前状态: " + newState + "\n下一曲", Toast.LENGTH_SHORT).show();
                }else{
                    //Toast.makeText(viewpager_play_page.this, "当前状态: " + newState + "\n这是什么值", Toast.LENGTH_SHORT).show();
                }

            }
        });







        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setVisibility(View.GONE);


        Pager_Adapter adapter = new Pager_Adapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);





    }

    private void hideActivity() {
        ViewGroup rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.setVisibility(View.GONE);
    }

    private void showActivity() {
        ViewGroup rootView = getWindow().getDecorView().findViewById(android.R.id.content);
        rootView.setVisibility(View.VISIBLE);
    }
    private class Pager_Adapter extends FragmentPagerAdapter {

        private String[] titles = {"音乐", "歌词", "评论"};

        public Pager_Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new music_control();
                case 1:
                    return new Tab1Fragment();
                case 2:
                    return new Tab3Fragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}

