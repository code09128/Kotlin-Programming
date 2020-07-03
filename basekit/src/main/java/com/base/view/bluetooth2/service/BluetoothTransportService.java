package com.base.view.bluetooth2.service;

/*
 * Created by Eric on 2018/5/24
 */

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.base.view.bluetooth2.model.Bluetooth;
import com.global.ActionCallback;
import com.global.BaseGlobalConfig;
import com.global.BaseGlobalData;
import com.global.BaseGlobalFunction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.UUID;

/**藍芽數據傳輸邏輯層*/
@SuppressLint("MissingPermission")
public class BluetoothTransportService {
    private final static String TAG = BluetoothTransportService.class.getSimpleName();

    private LinkedHashMap<String,Bluetooth> devices = BaseGlobalData.BLUETOOTH;

    private static InputStream inputStream;
    private static OutputStream outputStream;

    private BluetoothSocket btSocket;
    private BluetoothDevice btDevice;

    private Bundle bundle = new Bundle();
    private BluetoothRunnable runnable = new BluetoothRunnable();

    /**1.提供目前要使用的藍芽Socket*/
    public void setSocket(BluetoothSocket socket,BluetoothDevice device){
        btSocket = socket;
        btDevice = device;

        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
        } catch (IOException e) {
            BaseGlobalFunction.showErrorMessage(TAG,e);
        }
    }

    /**
     * 發送請求
     * @param buffer 請求參數
     * @param readSize 回應byte數
     * @param timeout 允許多少時間等待回應
     * */
    public void write(byte[] buffer,int readSize,int timeout,ActionCallback.byteData callback){
        try {
            runnable.setData(buffer,readSize,timeout,callback);
            new Thread(runnable).start();
        } catch(Exception e){
            BaseGlobalFunction.showErrorMessage(TAG,e);
        }
    }

    /**收完藍芽資料後，切回主執行緒，進行Callback回調*/
    private Handler handler = new Handler(Looper.getMainLooper()){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            byte[] buffer = msg.getData().getByteArray(BaseGlobalConfig.READ_MESSAGE_KEY);

            ActionCallback.byteData callback = (ActionCallback.byteData)msg.getData().getSerializable("callback");
            assert callback != null;

            callback.onByteResult(buffer);
        }
    };

    /**斷線重連*/
    private void reConnection(){
        try {
            if(btSocket.isConnected()){
                btSocket.close();
            }

            btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString(BaseGlobalConfig.UUID));
            btSocket.connect();

            Bluetooth data = devices.get(btDevice.getAddress());

            if(data != null){
                data.setSocket(btSocket);
                setSocket(btSocket,btDevice);
            }
        }catch(IOException e){
            BaseGlobalFunction.showErrorMessage(TAG,e);
        }
    }

    /**接收數據*/
    class BluetoothRunnable implements Runnable{
        private byte[] writeBuffer;//向藍芽請求的資訊
        private int readSize;//藍芽本次回傳要的byte數(回應不到該數表示資料要續傳)
        private int timeout;//資料超過多少毫秒沒回來就通知重送
        private ActionCallback.byteData callback;

        void setData(byte[] writeBuffer,int readSize,int timeout,ActionCallback.byteData callback){
            this.writeBuffer = writeBuffer;
            this.readSize = readSize;
            this.timeout = timeout;
            this.callback = callback;
        }

        @Override
        public void run() {
            try{
                outputStream.write(writeBuffer);

                byte[] buffer = new byte[1024];
                bundle.clear();
                int retry = 0;

                while(true){
                    /*超過timeout時間還沒拿到資料，通知前端重送*/
                    if(retry == timeout){
                        bundle.putSerializable("callback",callback);

                        Message message = handler.obtainMessage();
                        message.setData(bundle);

                        handler.sendMessage(message);
                        break;
                    }

                    if(inputStream.available() > 0){
                        int bytes = inputStream.read(buffer);//讀取串流

                        /*如果資料沒接完，繼續接*/
                        if(bytes != readSize){
                            retry = 0;
                            continue;
                        }

                        /*寫進串流資訊*/
                        byte[] result = new byte[bytes];
                        System.arraycopy(buffer,0,result,0,bytes);

                        bundle.putByteArray(BaseGlobalConfig.READ_MESSAGE_KEY, result);
                        bundle.putSerializable("callback",callback);

                        Message message = handler.obtainMessage();
                        message.setData(bundle);

                        handler.sendMessage(message);
                        break;
                    }
                    else{
                        retry++;
                        SystemClock.sleep(1);
                    }
                }
            }catch(Exception e){
                BaseGlobalFunction.showErrorMessage(TAG,e);
                reConnection();
            }
        }
    }
}
