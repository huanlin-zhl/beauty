package com.example.beauty.common.utils;

import com.example.beauty.common.domain.ClothScheduledLog;


import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author huanlin-zhl
 * @date 2020/5/3 10:19
 */
public class HttpClientUtils {

    private static OkHttpClient httpClient = new OkHttpClient();

    public static void post(String url, ClothScheduledLog scheduledLog, Callback callback){
        FormBody body = new FormBody.Builder()
                .add("clothScheduledLog", GsonUtils.toJsonString(scheduledLog))
                .build();

        upload(url, body, callback);
    }

    public static void get(String url, Callback callback){
        Request request = new Request.Builder()
                .get()
                .url(url)
                .build();
        httpClient.newCall(request).enqueue(callback);
    }

    public static void delete(String url, Callback callback){
        Request request = new Request.Builder()
                .delete()
                .url(url)
                .build();
        httpClient.newCall(request).enqueue(callback);
    }

    public static void upload(String url, RequestBody requestBody, Callback callback){
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        httpClient.newCall(request).enqueue(callback);
    }
}
