package com.fluentmusicplayer.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;


public class Tab1Fragment extends Fragment {



    public Tab1Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab1, container, false);

        Button exit_button = view.findViewById(R.id.exit_button);
        exit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });

        return view;
    }




}

