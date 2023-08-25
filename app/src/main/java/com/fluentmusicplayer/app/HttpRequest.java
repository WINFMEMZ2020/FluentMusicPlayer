package com.fluentmusicplayer.app;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class HttpRequest {
    public static String sendGetRequest(String url) {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                StringBuilder response = new StringBuilder();

                try {
                    // 创建URL对象
                    URL requestUrl = new URL(url);

                    // 打开连接
                    HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();

                    // 设置请求方法为GET
                    connection.setRequestMethod("GET");

                    // 获取响应代码
                    int responseCode = connection.getResponseCode();

                    // 读取响应内容
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // 断开连接
                    connection.disconnect();
                } catch (Exception e) {
                    // 使用Log.e输出捕获到的异常信息
                    Log.e("HttpRequest", "Error occurred: " + e.toString());
                    e.printStackTrace();
                }

                return response.toString();
            }
        };

        FutureTask<String> futureTask = new FutureTask<>(callable);
        new Thread(futureTask).start();

        try {
            return futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }
}
