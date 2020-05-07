package com.example.beauty.common.utils;

import com.example.beauty.common.domain.ClothScheduledLog;
import com.example.beauty.common.response.BeautyResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * @author huanlin-zhl
 * @date 2020/5/3 10:33
 */
public class GsonUtils {

    private static Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    public static String toJsonString(Object object){
        return gson.toJson(object);
    }

    public static <T>T toEntity(String jsonStr, Class<T> type){
        if(jsonStr == null || "".equals(jsonStr)){
            return null;
        }
        return gson.fromJson(jsonStr, type);
    }


    public static LinkedList<ClothScheduledLog> getLogListFromBeautyResult(String str){
        BeautyResult beautyResult = toEntity(str, BeautyResult.class);
        String logListStr = toJsonString(beautyResult.getData());
        Type type = new TypeToken<LinkedList<ClothScheduledLog>>(){}.getType();
        return  (LinkedList<ClothScheduledLog>)gson.fromJson(logListStr, type);
    }
}
