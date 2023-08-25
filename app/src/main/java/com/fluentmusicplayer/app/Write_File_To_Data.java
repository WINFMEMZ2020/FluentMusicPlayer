package com.fluentmusicplayer.app;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;

public class Write_File_To_Data {
    public static void writeToFile(Context context, String fileName, String content) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
            System.out.println("文件写入成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
