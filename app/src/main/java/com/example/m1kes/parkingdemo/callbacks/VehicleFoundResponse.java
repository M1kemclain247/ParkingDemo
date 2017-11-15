package com.example.m1kes.parkingdemo.callbacks;


import com.example.m1kes.parkingdemo.printer.models.Receipt;

public interface VehicleFoundResponse {
    void vehicleFound(String response);
    void notFound(String response);
}
