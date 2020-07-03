package com.base.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import com.global.ActionCallback;
import com.global.BaseGlobalConfig;

/**
 * Created by mars0925 on 2018/4/20.
 */

public class PermissionUtil {
    private static PermissionUtil permissionUtil;

    /**取得權限模組*/
    public static PermissionUtil getPermissionUtil(){
        if(permissionUtil == null){
            permissionUtil = new PermissionUtil();
        }

        return permissionUtil;
    }

    /**
     * (Fragment頁面)取得拍照權限
     */
    public boolean getCameraPermission(Fragment fragment) {
        /*尚未取得權限，詢問權限請求*/
        if (ContextCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, BaseGlobalConfig.CAMRA_REQUEST_CODE);

            return false;
        }

        return true;
    }

    /**取得撥打電話權限*/
    public void getCallPhonePermission(Activity activity, ActionCallback.bool callback){
        /*尚未取得權限，詢問權限請求*/
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, BaseGlobalConfig.CALL_PHONE_REQUEST_CODE);
        }
        /*已有權限*/
        else{
            callback.onBooleanResult(true);
        }
    }

    /**取得定位、傳感器權限*/
    public void getLocationSensorPermission(Activity activity, ActionCallback.bool callback){
        /*尚未取得權限，詢問權限請求*/
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.BODY_SENSORS}, BaseGlobalConfig.LOCATION_SENSORS_REQUEST_CODE);
        }
        /*已有權限*/
        else{
            callback.onBooleanResult(true);
        }
    }

    /**(Fragment頁面)取得位置權限*/
    public void getLocationPermission(Fragment fragment, ActionCallback.bool callback){
        /*尚未取得權限，詢問權限請求*/
        if(ContextCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, BaseGlobalConfig.LOCATION_REQUEST_CODE);
        }
        /*已有權限*/
        else{
            callback.onBooleanResult(true);
        }
    }

    /**(activity)取得位置權限*/
    public void getLocationPermission(Activity activity, ActionCallback.bool callback){
        /*尚未取得權限，詢問權限請求*/
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION}, BaseGlobalConfig.LOCATION_REQUEST_CODE);
        }
        /*已有權限*/
        else{
            callback.onBooleanResult(true);
        }
    }


    /**(Fragment頁面)取得定位權限*/
    public boolean getLocationPermission(Fragment fragment){
        /*尚未取得權限，詢問權限請求*/
        if(ContextCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, BaseGlobalConfig.LOCATION_REQUEST_CODE);

            return false;
        }

        return true;
    }

    /**取得讀取外部儲存體權限*/
    public void getReadExternalStorage(Fragment fragment, ActionCallback.bool callback) {
        /*尚未取得權限，詢問權限請求*/
        if (ContextCompat.checkSelfPermission(fragment.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, BaseGlobalConfig.READ_EXTERNAL_STORAGE_REQUEST_CODE);
        }
        /*已有權限*/
        else{
            callback.onBooleanResult(true);
        }
    }
}
