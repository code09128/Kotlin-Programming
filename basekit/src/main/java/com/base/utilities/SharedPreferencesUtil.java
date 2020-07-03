package com.base.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.global.BaseGlobalData;

/**
 * Created by Eric on 2018/1/10.
 */

@SuppressLint("CommitPrefEdits")
public class SharedPreferencesUtil {
    private static SharedPreferencesUtil sharedPreferencesUtil;

    /*KEY 值可以任意給定，只要用同一個 KEY 就可以存取同一個 SharedPreferences 檔案*/
    private static final String KEY = "App_SharedPreferences";

    private SharedPreferences spref = null;
    private SharedPreferences.Editor editor = null;

    /**key/value資料儲存*/
    public static SharedPreferencesUtil getSharedPreferencesUtil(){
        if(sharedPreferencesUtil == null){
            sharedPreferencesUtil = new SharedPreferencesUtil();
        }

        return sharedPreferencesUtil;
    }

    /**App啟動時先呼叫init進行初始化，然後再存入資料*/
    public void init(Context context){
		/*
		 * 取得一個 SharedPreferences 物件讓同一個 App 裡面的不同 ACTIVITY 可以共同使用
		 * 產生的 SharedPreferences 檔案「無法讓其他 App 存取」
		 */
        spref = context.getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
        editor = spref.edit();//由 SharedPreferences 中取出 Editor 物件，透過 Editor 物件將資料存入
    }

    /**存入資料*/
    public void setData(String key,Object value){
        putData(key,value);
    }

    /**取出string型態的資料，若資料不存在則回傳null*/
    public String getString(String key){
        return spref.getString(key, null);
    }

    /**取出boolean型態的資料，若資料不存在則回傳false*/
    public boolean getBoolean(String key){
        return spref.getBoolean(key, false);
    }

    /**取出float型態的資料，若資料不存在則回傳FLOAT_KEY_NOT_EXIST之值*/
    public float getFloat(String key){
        return spref.getFloat(key, BaseGlobalData.FLOAT_KEY_NOT_EXIST);
    }

    /**取出int型態的資料，若資料不存在則回傳INT_KEY_NOT_EXIST之值*/
    public int getInt(String key){
        return spref.getInt(key, BaseGlobalData.INT_KEY_NOT_EXIST);
    }

    /**清除 SharedPreferences 檔案中的特定資料*/
    public void remove(String key){
        editor.remove(key);
        editor.commit();
    }

    /**清除 SharedPreferences 檔案中所有資料*/
    public void clear(){
        editor.clear();
        editor.commit();
    }

    /**寫入資料*/
    private void putData(String key, Object value){
        /*
         * 將目前對 SharedPreferences 的異動寫入檔案中
         * 如果沒有呼叫apply()，則異動的資料不會生效
         */
        editor.apply();

        if(value instanceof String){
            editor.putString(key,(String)value);
        }
        else if(value instanceof Boolean){
            editor.putBoolean(key,(boolean)value);
        }
        else if(value instanceof Float){
            editor.putFloat(key, (float)value);
        }
        else if(value instanceof Integer){
            editor.putInt(key, (int)value);
        }
        else if(value instanceof Long){
            editor.putLong(key,(long)value);
        }

        /*
         * 將目前對 SharedPreferences 的異動寫入檔案中
         * 如果沒有呼叫commit()，則異動的資料不會生效
         */
        editor.commit();
    }
}
