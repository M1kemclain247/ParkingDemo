package com.example.m1kes.parkingdemo.callbacks;



public interface CashupResponse {
    void onCashupComplete(String response);
    void onCashupFailed(String response);
}
