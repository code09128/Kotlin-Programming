package com.global;

/*
 * Created by Eric on 2018/1/9.
 */

import android.app.Activity;

import com.base.view.bluetooth2.model.Bluetooth;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

/**專案在掛載時，可對以下參數進行設定*/
public class BaseGlobalData {

    /*App*/
    public static WeakReference<Activity> ACTIVITY              = null;//當前頁面的Activity
    public static String            APP_NAME                    = "";//App名稱
    public static int               APP_ICON                    = 0;//App圖案
    public static int               TIMEOUT                     = 40000;//等待時間
    public static int               SPAN_COUNT                  = 3;//grid一列幾個功能
    public static String            TOKEN                       = "";//TOKEN
    public static String            WEBVIEW_TOKEN               = "";//WEBVIEW_TOKEN

    /*SQLite設定*/
    public static String            DATABASE_NAME               = "AppName_db";
    public static int               DATABASE_VERSION            = 1;

    /*SharedPreferences的Key*/
    public static float 		    FLOAT_KEY_NOT_EXIST			= -9999;//當key值不存在時，所回傳的數值
    public static int 		        INT_KEY_NOT_EXIST           = -9999;//當key值不存在時，所回傳的數值

    /*藍芽相關*/
    /*藍芽，key=deviceAddress*/
    public static LinkedHashMap<String,Bluetooth> BLUETOOTH = new LinkedHashMap<>();
}
