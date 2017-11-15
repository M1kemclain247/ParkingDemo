package com.example.m1kes.parkingdemo.callbacks;



public interface StartShiftResponse {
    void onShiftStartSuccess(String shiftID,String username);
    void onShiftStartFailed(String errorResponse);
    void onAuthenticationFailed(String errorMessage);

}
