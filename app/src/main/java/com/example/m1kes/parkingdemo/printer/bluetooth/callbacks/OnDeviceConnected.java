package com.example.m1kes.parkingdemo.printer.bluetooth.callbacks;


import com.example.m1kes.parkingdemo.printer.bluetooth.DeviceItem;

public interface OnDeviceConnected {
    void onConnectSuccess(DeviceItem deviceItem);
    void onConnectFail();
}
