package com.example.m1kes.parkingdemo.transactions.callbacks;



public interface OnVehicleLogin {
    void onSuccess(String response);
    void onFailed(String response);
}

