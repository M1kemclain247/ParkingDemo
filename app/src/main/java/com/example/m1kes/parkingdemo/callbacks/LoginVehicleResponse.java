package com.example.m1kes.parkingdemo.callbacks;


import com.example.m1kes.parkingdemo.models.Transaction;

public interface LoginVehicleResponse {
    void onVehicleLoggedIn(String response, Transaction transaction);
    void onVehicleLoginFailed(String response,Transaction transaction);
}
