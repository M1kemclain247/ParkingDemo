package com.example.m1kes.parkingdemo.models;



public class SearchHistory {

    private int id;
    private String vehicleReg;
    private String searchDateTime;

    public SearchHistory() {
    }

    public SearchHistory(int id, String vehicleReg, String searchDateTime) {
        this.id = id;
        this.vehicleReg = vehicleReg;
        this.searchDateTime = searchDateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }

    public void setVehicleReg(String vehicleReg) {
        this.vehicleReg = vehicleReg;
    }

    public String getSearchDateTime() {
        return searchDateTime;
    }

    public void setSearchDateTime(String searchDateTime) {
        this.searchDateTime = searchDateTime;
    }


    @Override
    public String toString() {
        return "SearchHistory{" +
                "vehicleReg='" + vehicleReg + '\'' +
                ", searchDateTime='" + searchDateTime + '\'' +
                '}';
    }
}
