package com.example.beauty.common.utils;

import android.app.Activity;


import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author huanlin-zhl
 * @date 2020/5/4 9:07
 */
public class PermissionUtil {

    public static void requestPermission(Activity activity, String[] permissions){
        if (EasyPermissions.hasPermissions(activity, permissions)) {

        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(activity, "需要获取相关权限才可以继续操作", 100, permissions);
        }
    }
}
