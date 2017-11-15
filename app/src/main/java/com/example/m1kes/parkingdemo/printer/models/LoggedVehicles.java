package com.example.m1kes.parkingdemo.printer.models;


import java.text.DecimalFormat;
import java.util.List;

public class LoggedVehicles extends Receipt {

    private List<Vehicle> loggedVehicles;
    private DecimalFormat df = new DecimalFormat("0.00");


    public LoggedVehicles(List<Vehicle> loggedVehicles){
        this.loggedVehicles = loggedVehicles;
    }


    @Override
   public String getPrintData() {
        return makeMPTFormatReciept();
    }

    @Override
   public String getPrintType() {
        return TYPE_LOGGED_VEHICLES;
    }


    private String makeMPTFormatReciept(){

        StringBuilder sb = new StringBuilder();
        sb.append(getReceiptHeader());
        sb.append("     - Vehicles Logged IN - ");
        sb.append("\r\n");
        int countedAmount = 1;
        for(Vehicle vehicle : loggedVehicles){
            sb.append(countedAmount).append(".").append("\r\n");
            sb.append("Vehicle Reg : ").append(vehicle.getVehicleReg()).append("\r\n");
            sb.append("Bay Number  : ").append(vehicle.getBaynumber()).append("\r\n");
            sb.append("Deposit     : $").append(df.format(vehicle.getDeposit())).append("\r\n");
            sb.append("Entry Time  : ").append(vehicle.getIn_datetime()).append("\r\n");
            countedAmount++;
        }
        sb.append("\r\n");
        sb.append(getReceiptFooter());
        return sb.toString();
    }



}
