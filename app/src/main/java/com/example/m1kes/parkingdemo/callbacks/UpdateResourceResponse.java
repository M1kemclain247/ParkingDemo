package com.example.m1kes.parkingdemo.callbacks;


public interface UpdateResourceResponse {

    void updateComplete();
    void updateFailed();
    void onNoConnection();
    void onProgressUpdated(String message);

}
