package com.global;

import android.bluetooth.BluetoothDevice;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Eric on 2017/12/21.
 */

public interface ActionCallback extends Serializable{

    interface Int extends ActionCallback {
        void onIntResult(int result);
    }

    interface LongValue extends ActionCallback {
        void onLongResult(long result);
    }

    interface string extends ActionCallback {
        void onStringResult(String result);
    }

    interface bool extends ActionCallback {
        void onBooleanResult(boolean result);
    }

    interface data<E> extends ActionCallback {
        void onDataResult(E result);
    }

    interface multidata<E, K> extends ActionCallback {
       void onDataResult(E result, K data);
    }

    interface arrayData<E> extends ActionCallback {
        void onArrayDataResult(ArrayList<E> result);
    }

    /**
     * 回調函數
     * 傳入參數:json資料
     * */
    interface dataResult extends ActionCallback {
        void onDataResult(JSONObject result);
    }

    interface bluetooth extends ActionCallback {
        /**
         * 回調函數
         * @param result 連線結果:true=成功 false=失敗
         * @param device 連接的藍芽設備
         * */
        void onBlueToothResult(boolean result,BluetoothDevice device);
    }

    /**回傳藍芽資料*/
    interface byteData extends ActionCallback {
        /**
         * @param buffer 如果為null值，表示沒取到資料，請求要重送
         * */
        void onByteResult(byte[] buffer);
    }
}
