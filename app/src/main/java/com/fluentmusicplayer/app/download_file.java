package com.fluentmusicplayer.app;

import android.media.SoundPool;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class download_file extends AsyncTask<String, Void, Boolean> {
    private SoundPool soundPool;
    private int soundId;

    private static final String TAG = download_file.class.getSimpleName();

    @Override
    protected Boolean doInBackground(String... params) {
        String fileUrl = params[0];
        String savePath = params[1];

        try {
            URL url = new URL(fileUrl);
            BufferedInputStream inputStream = new BufferedInputStream(url.openStream());
            FileOutputStream outputStream = new FileOutputStream(savePath);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer, 0, 1024)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error downloading file: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Log.i("download_file", "File downloaded successfully!");
        } else {
            Log.e("download_file", "Failed to download file.");
        }
    }
}
