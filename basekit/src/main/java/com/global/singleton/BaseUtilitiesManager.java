package com.global.singleton;

import com.base.utilities.BaseImageUtil;
import com.base.utilities.BasePopupUtil;
import com.base.utilities.BluetoothLEUtil;
import com.base.utilities.JudgeIllegalUtil;
import com.base.utilities.LocationUtil;
import com.base.utilities.PermissionUtil;
import com.base.utilities.SharedPreferencesUtil;
import com.base.utilities.WebServiceUtil;

/**
 * Created by Eric on 2018/1/9.
 */

public class BaseUtilitiesManager {
    /**WebService連線模組*/
    public WebServiceUtil getWebServiceUtil(){
        return WebServiceUtil.getWebServiceUtil();
    }

    /**提示視窗模組*/
    public BasePopupUtil getBasePopupUtil(){
        return BasePopupUtil.getBasePopupUtil();
    }

    /**轉換特殊字元，防止SQL injection*/
    public JudgeIllegalUtil getJudgeIllegalUtil(){
        return JudgeIllegalUtil.getJudgeIllegalUtil();
    }

    /**key/value資料儲存*/
    public SharedPreferencesUtil getSharedPreferencesUtil(){
        return SharedPreferencesUtil.getSharedPreferencesUtil();}

    /**權限模組*/
    public PermissionUtil getPermissionUtil(){
        return PermissionUtil.getPermissionUtil();
    }

    /**藍芽BLE模組*/
    public BluetoothLEUtil getBluetoothLEUtil(){
        return BluetoothLEUtil.getBluetoothLEUtil();
    }

    public BaseImageUtil getBaseImageUtil(){
        return BaseImageUtil.getBaseImageUtil();
    }

    /**定位模組*/
    public LocationUtil getLocationUtil(){
        return LocationUtil.getLocationUtil();
    }

}
