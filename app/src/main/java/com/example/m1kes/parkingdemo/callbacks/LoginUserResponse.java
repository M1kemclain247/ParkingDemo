package com.example.m1kes.parkingdemo.callbacks;



public interface LoginUserResponse {
    void onLoginSuccess(String username);
    void onLoginFailed();
    void onLoginSameDay();
}
