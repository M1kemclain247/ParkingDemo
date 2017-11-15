package com.example.m1kes.parkingdemo.callbacks;


import com.example.m1kes.parkingdemo.printer.models.Receipt;

public interface ArrearsResponse {
    void getArrearsSuccessfull(Receipt receipt);
    void getArrearsFailed(String response);
}
