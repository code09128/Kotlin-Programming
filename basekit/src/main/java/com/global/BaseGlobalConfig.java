package com.global;

/**
 * Created by Eric on 2018/1/26.
 */

public class BaseGlobalConfig {

    /*語系參數*/
    public static final String LANGUAGE_TW = "TW";
    public static final String LANGUAGE_CN = "CN";

    /*權限設定*/
    public static final int CAMRA_REQUEST_CODE = 0x01;//照相權限
    public static final int CALL_PHONE_REQUEST_CODE = 0x02;//撥打電話權限
    public static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 0x03;//讀取外部儲存
    public static final int LOCATION_SENSORS_REQUEST_CODE = 0x04;//定位、傳感器資訊權限
    public static final int LOCATION_REQUEST_CODE = 0x05;//位置權限

    /*WebService失敗參數*/
    public static final int SUCCESS = 0;//成功
    public static final int TIMEOUT = 1;//請求逾時
    public static final int HTTPS_ERROR_401 = 2;//Https 401錯誤碼
    public static final int HTTPS_ERROR_500 = 3;//Https 500錯誤碼
    public static final int OTHER_ERROR = 4;//其他錯誤

    /*相本*/
    public static final int	SHOW_ITEM_COUNT = 15;//一次顯示的數量
    public static final int	RAW_ITEM_COUNT = 3;//相本一列數量

    /*浮點數運算*/
    static final int ADDITION = 0;//加法
    static final int SUBTRACTION = 1;//減法
    static final int MULTIPLICATION = 2;//乘法
    static final int DIVISION = 3;//除法

    /*請求功能參數*/
    public static final int BLUE_TOOTH_OPEN = 0x01;//藍芽請求

    /*藍芽2.0功能*/
    public static final String UUID = "00001101-0000-1000-8000-00805F9B34FB";
    public static final String CONNECT_RESULT_KEY = "result";//傳送藍芽連線結果key對應
    public static final String BLUE_TOOTH_DEVICE_KEY = "btDevice";//傳送藍芽設備參數key對應
    public static final String READ_MESSAGE_KEY = "read";//接收藍芽設備參數key對應

    public static final String WEB_SERVICE_RESULT = "result";//WebService 回傳資料的KEY值
}
