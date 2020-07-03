package com.base.view.bluetooth2.service;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.base.R;
import com.base.utilities.BasePopupUtil;
import com.base.utilities.PermissionUtil;
import com.base.view.bluetooth2.model.Bluetooth;
import com.global.ActionCallback;
import com.global.BaseGlobalConfig;
import com.global.BaseGlobalData;
import com.global.BaseGlobalFunction;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;

/*
 * Created by Eric on 2018/5/23
 */

/**藍芽設備連接邏輯層*/
@SuppressLint("MissingPermission")
public class BluetoothConnectionService {
    private final String TAG = getClass().getSimpleName();
    private final PermissionUtil permissionUtil = PermissionUtil.getPermissionUtil();
    private final BasePopupUtil basePopupUtil = BasePopupUtil.getBasePopupUtil();
    private final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    private LinkedHashMap<String,Bluetooth> devices = BaseGlobalData.BLUETOOTH;

    /**
     * 1.確認行動裝置是否支持藍芽
     * @return true:支援 false:不支援
     * */
    public boolean supportBlueTooth(){
        return adapter != null;
    }

    /**2.確認權限(沒有該權限無法搜尋附近藍芽設備)*/
    public void checkPermission(Fragment fragment,ActionCallback.bool callback){
        permissionUtil.getLocationPermission(fragment,callback);
    }

    /**3.獲取已配對的藍芽設備*/
    public Set<BluetoothDevice> getBluetoothDevice(){
        return adapter.getBondedDevices();
    }

    /**
     * 4.註冊藍芽配對
     * @param callback true:藍芽開啟
     * */
    public void setFilter(Fragment fragment,BroadcastReceiver receiver,ActionCallback.bool callback){
        IntentFilter intentFliter = new IntentFilter();

        intentFliter.addAction(BluetoothDevice.ACTION_FOUND);//搜索發現設備
        intentFliter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//狀態改變

        intentFliter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//搜索結束
        intentFliter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行動掃描模式改變了
        intentFliter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//動作狀態發生了變化

        fragment.getContext().registerReceiver(receiver, intentFliter);

        /*如果沒開藍芽，請求開啟*/
        if (!adapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            fragment.startActivityForResult(intent, BaseGlobalConfig.BLUE_TOOTH_OPEN);
        } else {
            if(callback != null){
                callback.onBooleanResult(true);
            }
        }
    }

    /**
     * 5.藍芽連線
     * handler: true=連線成功 false=連線失敗
     * */
    public void blueToothConnection(final String uuid,final BluetoothDevice btDevice,final ActionCallback.bluetooth callback){
        Context context = BaseGlobalFunction.getActivity();
        basePopupUtil.showLoadingPopup(context,context.getString(R.string.connection));

        /*需在子線程執行*/
        new Thread(new Runnable(){
            @Override
            public void run() {
                boolean result = connection(uuid,btDevice);

                Bundle bundle = new Bundle();
                Message message = handler.obtainMessage();

                bundle.putBoolean(BaseGlobalConfig.CONNECT_RESULT_KEY,result);
                bundle.putParcelable(BaseGlobalConfig.BLUE_TOOTH_DEVICE_KEY,btDevice);
                bundle.putSerializable("callback",callback);

                message.setData(bundle);

                handler.sendMessage(message);
            }
        }).start();
    }

    /**藍芽連線*/
    private boolean connection(String uuid, BluetoothDevice btDevice){
        BluetoothSocket btSocket = null;

        try{
            if(devices.get(btDevice.getAddress()) == null){
                btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
            }else{
                Bluetooth bluetooth = devices.get(btDevice.getAddress());
                btSocket = bluetooth.getSocket();

                if(btSocket.isConnected()){
                    return true;
                }
                /*舊有連線已斷線*/
                else{
                    devices.remove(btDevice.getAddress());
                    btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(uuid));
                }
            }

            Bluetooth data = new Bluetooth();
            data.setDevice(btDevice);
            data.setSocket(btSocket);

            devices.put(btDevice.getAddress(),data);
            btSocket.connect();
        }catch(Exception e){
            BaseGlobalFunction.showErrorMessage(TAG,e);

            try {
                if(btSocket != null){
                    btSocket.close();
                    devices.remove(btDevice.getAddress());
                }
            } catch (IOException e2) {
                BaseGlobalFunction.showErrorMessage(TAG,e2);
            }

            return false;
        }

        return true;
    }

    /**連線動作執行後，切回主執行緒，進行Callback回調*/
    private Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            basePopupUtil.closeLoadingPopup();
            boolean result = msg.getData().getBoolean(BaseGlobalConfig.CONNECT_RESULT_KEY);
            BluetoothDevice device = msg.getData().getParcelable(BaseGlobalConfig.BLUE_TOOTH_DEVICE_KEY);

            ActionCallback.bluetooth callback = (ActionCallback.bluetooth)msg.getData().getSerializable("callback");

            assert callback != null;
            callback.onBlueToothResult(result,device);
        }
    };

    /**中斷連線*/
    public boolean disconnect(BluetoothDevice btDevice){
        Bluetooth data = devices.get(btDevice.getAddress());

        try{
            if(data.getSocket() != null){
                devices.remove(btDevice.getAddress());

                if(data.getSocket().isConnected()){
                    data.getSocket().close();
                }
            }
        }catch(Exception e){
            BaseGlobalFunction.showErrorMessage(TAG,e);
            return false;
        }

        return true;
    }

    /**開啟搜尋*/
    public void openSearch(){
        /*如果已經在搜尋，不再開啟*/
        if(adapter.isDiscovering()){
            return;
        }

        /*如果無法搜尋*/
        if(!adapter.startDiscovery()){
            closeSearch();
            BaseGlobalFunction.setToast(BaseGlobalFunction.getActivity(), BaseGlobalFunction.getActivity().getString(R.string.search_error));
        }
    }

    /**關閉搜尋*/
    public void closeSearch(){
        if (adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }
    }
}
