package com.example.m1kes.parkingdemo.printer.models;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MakePaymentReceipt extends Receipt {

    private String reg_number;
    private String EntryTime;
    private String expiryTime;
    private String precinctName;
    private String currentUser;
    private String recieptNumber;
    private String zone;
    private double AmountDue;
    private double AmountPaid;
    private double newBalance;
    private double tender;
    private double change;
    private double currentBalance;
    private int bayNumber;
    private boolean isOffline;
    private DecimalFormat df = new DecimalFormat("0.00");
    private String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");




    @Override
    public String getPrintData() {
        return getReceipt();
    }

    @Override
    public String getPrintType() {
        return TYPE_MAKE_PAYMENT;
    }

    /**
     *
     * @return String representation of a fiscal Receipt
     */

    public String getReceipt(){

        StringBuilder sb = new StringBuilder();
        sb.append(getReceiptHeader());
        sb.append("        ").append(date).append("\r\n");
        sb.append("\r\n");
        sb.append("     - Vehicle IN Receipt - ");
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("Site      :  City Parking (PVT) ");
        sb.append("Zone      :  ").append(zone).append("\r\n");
        sb.append("Precinct  : ").append(precinctName).append("\r\n");
        sb.append("Marshal   :  ").append(currentUser).append("\r\n");
        sb.append("Receipt   :  ").append(recieptNumber).append("\r\n");
        sb.append("Reg No.   :  ").append(reg_number).append("\r\n");
        sb.append("Bay No.   :  ").append(bayNumber).append("\r\n");
        sb.append("Entry Time:  ").append(EntryTime).append("\r\n");
        sb.append("Expiry Time: ").append(expiryTime).append("\r\n");
        sb.append("Receipted :  $").append(df.format(AmountPaid)).append("\r\n");
        sb.append("Tendered  :  $").append(df.format(tender)).append(" 15% VAT Incl").append("\r\n");
        sb.append("Change    :  $").append(df.format(change)).append("\r\n");

        if(currentBalance>0){
            newBalance = currentBalance + AmountDue - AmountPaid;
        }else{
            newBalance = currentBalance + AmountDue - AmountPaid;
        }

        if(newBalance==0){
            sb.append("Balance   :  $").append(df.format(newBalance)).append("\r\n");
        }else if(currentBalance >0){
            sb.append("Arrears   :  $").append(df.format(newBalance)).append("\r\n");
        }else{
            sb.append("Prepaid   :  $").append(df.format(Math.abs(newBalance))).append("\r\n");
        }

        if(isOffline){
            sb.append("\r\n");
            sb.append("--------------------------------");
            sb.append("\r\n");
            sb.append("      --Printed Offline--\r\n");
            sb.append("  This Reciept was generated\r\n");
            sb.append("  offline your balance may\r\n");
            sb.append("  not be accurate.\r\n");
        }

        sb.append(getReceiptFooter());

        this.printData = sb.toString();

        return sb.toString();
    }


    public String getReg_number() {
        return reg_number;
    }

    public void setReg_number(String reg_number) {
        this.reg_number = reg_number;
    }

    public String getEntryTime() {
        return EntryTime;
    }

    public void setEntryTime(String entryTime) {
        EntryTime = entryTime;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(double newBalance) {
        this.newBalance = newBalance;
    }

    public double getAmountPaid() {
        return AmountPaid;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    public void setAmountPaid(double amountPaid) {
        AmountPaid = amountPaid;
    }

    public double getAmountDue() {
        return AmountDue;
    }

    public void setAmountDue(double amountDue) {
        AmountDue = amountDue;
    }

    public String getPrecinctName() {
        return precinctName;
    }

    public void setPrecinctName(String precinctName) {
        this.precinctName = precinctName;
    }

    public String getRecieptNumber() {
        return recieptNumber;
    }

    public void setRecieptNumber(String recieptNumber) {
        this.recieptNumber = recieptNumber;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public double getTender() {
        return tender;
    }

    public void setTender(double tender) {
        this.tender = tender;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public int getBayNumber() {
        return bayNumber;
    }

    public void setBayNumber(int bayNumber) {
        this.bayNumber = bayNumber;
    }

    public DecimalFormat getDf() {
        return df;
    }

    public void setDf(DecimalFormat df) {
        this.df = df;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }
}
