package com.example.m1kes.parkingdemo.callbacks;


import HPRTAndroidSDK.HPRTPrinterHelper;

public interface OnPrinterConnected {
    void onFailedConnect();
    void onConnected(String printerName, String printerAddress);
}
