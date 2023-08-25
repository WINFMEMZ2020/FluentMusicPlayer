package com.fluentmusicplayer.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class ViewPager_test extends AppCompatActivity {

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
        setContentView(R.layout.activity_view_pager_test);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏



        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setVisibility(View.GONE);


        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        private String[] titles = {"Tab 1", "Tab 2", "Tab 3"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Tab1Fragment();
                case 1:
                    return new Tab2Fragment();
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

//废了，ViewPager真的好难。。。 2023.08.09