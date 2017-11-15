package com.example.m1kes.parkingdemo.printer.models;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BalanceEnquiry extends Receipt {


    private String shiftID;
    private String vehicleReg;
    private String owner;
    private double balance;
    private String lastlogged;
    private String responseDateTime;
    private String currentMarshalUsername;
    private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
    private DecimalFormat df = new DecimalFormat("0.00");


    @Override
   public String getPrintData() {

        /**
         *
         * @return String representation of a fiscal Receipt
         */

            StringBuilder sb = new StringBuilder();
            sb.append(getReceiptHeader());
            sb.append("         ").append(date).append("\r\n");
            sb.append("\r\n");
            sb.append("   - Balance Enquiry Receipt - ");
            sb.append("\r\n");
            sb.append("\r\n");
            sb.append("Marshall    : ").append(currentMarshalUsername).append("\r\n");
            sb.append("Registration: ").append(vehicleReg).append("\r\n");
            sb.append("Ownr Details: ").append(owner).append("\r\n");
        if(balance>0){
            sb.append("Tot Arrears : $").append(df.format(balance)).append("\r\n");
        }else if(balance ==0){
            sb.append("Balance     : $").append(String.valueOf(balance)).append("\r\n");
        }else{
            sb.append("Tot Prepaid : $").append(df.format(Math.abs(balance))).append("\r\n");
        }
        if(lastlogged!=null){
        SimpleDateFormat ff = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = null;
        Date startDate  = null;
        try {
            startDate = df2.parse(lastlogged);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formattedDate = ff.format(startDate);

            sb.append("Last Login  : ").append(formattedDate).append("\r\n");
        }else {
            sb.append("Last Login  : Unavailable").append("\r\n");
        }
            sb.append(getReceiptFooter());
            this.printData = sb.toString();
            return sb.toString();
    }

    public BalanceEnquiry(String shiftID,String vehicleReg, String owner,double balance,String lastlogged,String responseDateTime,
                          String currentMarshalUsername){
        this.shiftID = shiftID;
        this.vehicleReg = vehicleReg;
        this.owner = owner;
        this.balance = balance;
        this.lastlogged = lastlogged;
        this.responseDateTime = responseDateTime;
        this.currentMarshalUsername = currentMarshalUsername;
     }


    @Override
   public String getPrintType() {
        return TYPE_BALANCE_ENQUIRY;
    }

    public String getShiftID() {
        return shiftID;
    }

    public void setShiftID(String shiftID) {
        this.shiftID = shiftID;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }

    public void setVehicleReg(String vehicleReg) {
        this.vehicleReg = vehicleReg;
    }

    public String getCurrentMarshalUsername() {
        return currentMarshalUsername;
    }

    public void setCurrentMarshalUsername(String currentMarshalUsername) {
        this.currentMarshalUsername = currentMarshalUsername;
    }

    public String getOwner_details() {
        return owner;
    }

    public void setOwner_details(String owner_details) {
        this.owner = owner_details;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getLastlogged() {
        return lastlogged;
    }

    public void setLastlogged(String lastlogged) {
        this.lastlogged = lastlogged;
    }

    public String getResponseDateTime() {
        return responseDateTime;
    }

    public void setResponseDateTime(String responseDateTime) {
        this.responseDateTime = responseDateTime;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public SimpleDateFormat getFormat() {
        return format;
    }

    public void setFormat(SimpleDateFormat format) {
        this.format = format;
    }


    @Override
    public String toString() {
        return "BalanceEnquiry{" +
                "shiftID=" + shiftID +
                ", vehicleReg='" + vehicleReg + '\'' +
                ", owner='" + owner + '\'' +
                ", balance=" + balance +
                ", lastlogged='" + lastlogged + '\'' +
                ", responseDateTime='" + responseDateTime + '\'' +
                ", currentMarshalUsername='" + currentMarshalUsername + '\'' +
                ", format=" + format +
                '}';
    }
}
