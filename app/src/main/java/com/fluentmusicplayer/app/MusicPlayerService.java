package com.fluentmusicplayer.app;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import androidx.annotation.Nullable;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.util.Objects;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class MusicPlayerService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}
