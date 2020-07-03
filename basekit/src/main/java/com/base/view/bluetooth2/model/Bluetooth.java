package com.base.view.bluetooth2.model;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/*
 * Created by Eric on 2018/5/25
 */
public class Bluetooth {
    private BluetoothDevice device;//藍芽設備
    private BluetoothSocket socket;//藍芽socket

    /**藍芽設備*/
    public BluetoothDevice getDevice() {
        return device;
    }

    /**藍芽設備*/
    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    /**藍芽socket*/
    public BluetoothSocket getSocket() {
        return socket;
    }

    /**藍芽socket*/
    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }
}
