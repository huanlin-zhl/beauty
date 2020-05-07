package com.example.beauty.common.utils;

import android.app.Activity;
import android.widget.Toast;

import com.example.beauty.ClothScheduleActivity;

/**
 * @author huanlin-zhl
 * @date 2020/5/3 14:22
 */
public class ToastUtils {

    public static void showToast(Activity activity, String msg){
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
    }
}
