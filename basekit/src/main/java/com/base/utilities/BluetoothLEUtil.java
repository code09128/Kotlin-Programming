package com.base.utilities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.Objects;
import java.util.UUID;

/**
 * Created by JyunWu on 2018/5/28.
 */
@SuppressLint("MissingPermission")
public class BluetoothLEUtil {
    private static final String CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";//client UUID配置

    private static BluetoothLEUtil bluetoothLE;
    private static StateCallbackListeners stateCallback;
    private static BLEDataCallbackListeners dataCallback;
    private boolean broadcastRegister = false;
    private BluetoothAdapter bluetoothAdapter;
    private String[] devicesName;
    private BluetoothGattService measureGattService;
    private BluetoothGattService batteryGattService;
    private BluetoothGatt gatt;

    public interface StateCallbackListeners {
         void onDevicePairResult(BluetoothDevice device);
         void onConnectedResult();
         void onDisConnectResult();
         void onBlueToothONResult();
    }

    public interface BLEDataCallbackListeners {
       void onServicesDiscovered(BluetoothGatt gatt, int status);
       void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
       void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    }

    public void setStateCallbackListeners(StateCallbackListeners stateCallback) {
        BluetoothLEUtil.stateCallback = stateCallback;
    }

    public void setBLEDataCallbackListeners(BLEDataCallbackListeners dataCallback) {
        BluetoothLEUtil.dataCallback = dataCallback;
    }

    /** 藍芽BLE模組*/
    public static BluetoothLEUtil getBluetoothLEUtil() {
        if (bluetoothLE == null) {
            bluetoothLE = new BluetoothLEUtil();
        }

        return bluetoothLE;
    }

    /**設置藍牙服務*/
    public void setBluetoothManager(Context context) {
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = Objects.requireNonNull(bluetoothManager).getAdapter();
        setBluetoothAdapter(adapter);

        IntentFilter filter = new IntentFilter();//廣播過濾物件
        filter.addAction(BluetoothDevice.ACTION_FOUND);//找到設備參數設置
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//搜索完成參數設置
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(receiver, filter);//註冊廣播
        setBroadcastRegister(true);
    }

    /**檢查藍牙開關*/
    public boolean isEnabled() {
        return getBluetoothAdapter() != null & getBluetoothAdapter().isEnabled();
    }

    /**藍牙開啟*/
    public void enabled() {
        if (!isEnabled())
            getBluetoothAdapter().enable();
    }

    /**開始掃描*/
    public void startScan() {
        if (getBluetoothAdapter().isDiscovering()) {
            getBluetoothAdapter().cancelDiscovery();
        }
        getBluetoothAdapter().startDiscovery();
    }

    /**停止掃描*/
    public void stopScan() {
        if (getBluetoothAdapter().isDiscovering()) {
            getBluetoothAdapter().cancelDiscovery();
        }
    }

    /**註銷藍芽裝置搜尋廣播器*/
    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(receiver);
    }

    /**藍芽裝置搜尋廣播器*/
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();//收到的廣播類型

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {//發現設備
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);//獲取設備資訊

                if (device.getName() != null) {
                    for (String deviceName : getDevicesName()) {
                        /*找到藍芽設備*/
                        if (device.getBondState() != BluetoothDevice.BOND_BONDED && device.getName().contains(deviceName)) {
                            unregisterReceiver(context);
                            setBroadcastRegister(false);

                            if (isNotEntity(stateCallback)){
                                stateCallback.onDevicePairResult(device);
                            }
                        }
                    }
                }
            }
            /*藍芽狀態發生了變化*/
            else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch(state) {
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.STATE_ON:
                        if (isNotEntity(stateCallback)){
                            stateCallback.onBlueToothONResult();
                        }

                        setGatt(null);
                        break;
                }
            }
        }
    };

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        /**設備連線狀態*/
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED://已連線
                    setGatt(gatt);
                    if (isNotEntity(stateCallback))
                        stateCallback.onConnectedResult();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED://已斷線
                    gatt.close();
                    setGatt(null);
                    if (isNotEntity(stateCallback))
                        stateCallback.onDisConnectResult();
                    break;
            }
        }

        /**遠端設備中的服務寫入狀態*/
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            switch (status) {
                case BluetoothGatt.GATT_SUCCESS://寫入成功
                    dataCallback.onServicesDiscovered(gatt, status);
                    break;
                case BluetoothGatt.GATT_FAILURE://寫入失敗
                    break;
                case BluetoothGatt.GATT_WRITE_NOT_PERMITTED://沒有寫入的權限
                    break;
            }
        }

        /**寫入Characteristic成功與否的監聽*/
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }

        /**Characteristic的狀態為可讀時的監聽*/
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (isNotEntity(dataCallback))
                dataCallback.onCharacteristicRead(gatt, characteristic, status);
        }

        /**設備的Characteristic資訊回調*/
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (isNotEntity(dataCallback))
                dataCallback.onCharacteristicChanged(gatt, characteristic);
        }

        /**寫入Descriptor*/
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        }
    };

    /**寫入特徵*/
    public void onCharacteristicWrite(BluetoothGatt gatt, String uuid) {
        BluetoothGattCharacteristic characteristic = getMeasureGattService().getCharacteristic(UUID.fromString(uuid));
        gatt.setCharacteristicNotification(characteristic, true);//啟用或禁止使用通知/指定的特徵。
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR_UUID));
        if (descriptor != null) {
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);//設置特性
            gatt.readCharacteristic(characteristic);
        }
    }

    /**讀取特徵*/
    public void onCharacteristicRead(BluetoothGatt gatt, String uuid) {
        BluetoothGattCharacteristic characteristic = getBatteryGattService().getCharacteristic(UUID.fromString(uuid));
        gatt.setCharacteristicNotification(characteristic, true);//啟用或禁止使用通知/指定的特徵。
        gatt.readCharacteristic(characteristic);
    }

    /**取得遠端設備*/
    public void connent(Context context, String address) {
        BluetoothDevice device = getBluetoothAdapter().getRemoteDevice(address);
        device.connectGatt(context, false, gattCallback);
    }

    /**斷開連線*/
    public void disconnect() {
        if (getGatt() != null) {
            getGatt().disconnect();
        }
    }

    /**取得藍芽適配器*/
    private BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    /**設置藍芽適配器*/
    private void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        this.bluetoothAdapter = bluetoothAdapter;
    }

    /**判斷物件是否不為空*/
    private boolean isNotEntity(Object obj) {
        return obj != null;
    }

    /**取得設備名稱*/
    private String[] getDevicesName() {
        return devicesName;
    }

    /**設置設備名稱*/
    public void setDevicesName(String[] devicesName) {
        this.devicesName = devicesName;
    }

    /**取得量測資訊gatt服務*/
    private BluetoothGattService getMeasureGattService() {
        return measureGattService;
    }

    /**設置量測資訊gatt服務*/
    public void setMeasureGattService(BluetoothGattService measureGattService) {
        this.measureGattService = measureGattService;
    }

    /**取得電池資訊gatt服務*/
    private BluetoothGattService getBatteryGattService() {
        return batteryGattService;
    }

    /**設置電池資訊gatt服務*/
    public void setBatteryGattService(BluetoothGattService batteryGattService) {
        this.batteryGattService = batteryGattService;
    }

    /**取得BLEgatt物件*/
    public BluetoothGatt getGatt() {
        return gatt;
    }

    /**設置BLEgatt物件*/
    private void setGatt(BluetoothGatt gatt) {
        this.gatt = gatt;
    }

    /**藍芽搜尋廣播器註冊狀態*/
    public boolean isBroadcastRegister() {
        return broadcastRegister;
    }

    /**設置藍芽搜尋廣播器註冊狀態*/
    private void setBroadcastRegister(boolean broadcastRegister) {
        this.broadcastRegister = broadcastRegister;
    }
}