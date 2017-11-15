package com.example.m1kes.parkingdemo.printer.models;



public class Vehicle {

    private String vehicleReg;
    private int baynumber;
    private double deposit;
    private String in_datetime;


    public Vehicle() {
    }

    public Vehicle(String vehicleReg, int baynumber, double deposit) {
        this.vehicleReg = vehicleReg;
        this.baynumber = baynumber;
        this.deposit = deposit;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }

    public void setVehicleReg(String vehicleReg) {
        this.vehicleReg = vehicleReg;
    }

    public int getBaynumber() {
        return baynumber;
    }

    public void setBaynumber(int baynumber) {
        this.baynumber = baynumber;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }


    public String getIn_datetime() {
        return in_datetime;
    }

    public void setIn_datetime(String in_datetime) {
        this.in_datetime = in_datetime;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "vehicleReg='" + vehicleReg + '\'' +
                ", baynumber=" + baynumber +
                ", deposit=" + deposit +
                ", in_datetime='" + in_datetime + '\'' +
                '}';
    }
}
