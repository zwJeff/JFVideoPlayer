package com.jeff.jfvideoplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * 说明：
 * 作者： 张武
 * 日期： 2017/4/11.
 * email:wuzhang4@creditease.cn
 */

public class PermissionUtils {

    public static final int REQUEST_ALL_NEEDED_PERMISSION_CODE=0x00;               //一次申请全部app需要的权限的请求码
    public static final int REQUEST_EXTERNAL_STORAGE_PERMISSION_CODE = 0x01;       //存储权限请求码
    public static final int REQUEST_CALL_PHONE_PERMISSION_CODE = 0x02;             //电话权限请求码
    public static final int REQUEST_ACCESS_COARSE_LOCATION_PERMISSION_CODE = 0x03; //定位权限请求码
    public static final int REQUEST_CAMERA_PERMISSION_CODE = 0x04; //相机权限请求码

    //需要的权限写在此处，启动app申请一次
    public  static String[] AllNeedPermissions =
            {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    /**
     * app所有必须权限的申请方法
     *
     * @param activity    请求权限的activity
     */
    public static void requestAllPermission(Activity activity) {

        ActivityCompat.requestPermissions(activity,
                AllNeedPermissions, REQUEST_ALL_NEEDED_PERMISSION_CODE);
    }

    /**
     * 判断是否已经授权
     *
     * @param permissions 权限数组（String...就是数组类型）
     * @return
     */
    public static boolean hasPermission(Activity activity, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                Log.i("jeff_permission", "权限未获取:"+permission);
                return false;
            }
        }
        Log.i("jeff_permission", "共申请了" + permissions.length + "权限全都有了");
        return true;
    }

    /**
     * 权限申请方法
     *
     * @param activity    请求权限的activity
     * @param code        请求码(统一定义)
     * @param permissions 请求的权限
     */
    public static void requestPermission(Activity activity, int code, String... permissions) {

        ActivityCompat.requestPermissions(activity,
                permissions, code);
    }

    /**
     * 检测请求结果码判定是否授权
     *
     * @param grantResults
     * @return
     */
    public static boolean checkPermissionResult(int[] grantResults) {
        if (grantResults != null) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }



    /**
     * 进入应用详情页
     *
     * @param activity
     */
    public static void gotoAppDetialsPage(Activity activity) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
        }
        activity.startActivity(localIntent);
    }
}
