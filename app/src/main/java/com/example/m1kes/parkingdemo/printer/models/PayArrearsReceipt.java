package com.example.m1kes.parkingdemo.printer.models;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PayArrearsReceipt extends Receipt {


    private String reg_number;
    private String EntryTime;
    private String precinctName;
    private String currentUser;
    private String recieptNumber;
    private String zone;
    private double AmountDue;
    private double tender;
    private double change;
    private double currentBalance;
    private double newBalance;
    private int bayNumber;
    private boolean isOffline;
    private String TYPE;
    private DecimalFormat df = new DecimalFormat("0.00");
    private String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");



    @Override
   public String getPrintData() {
        return getReceipt();
    }

    @Override
   public String getPrintType() {
        return TYPE;
    }


    /**
     *
     * @return String representation of a fiscal Receipt
     */

    public String getReceipt(){

            StringBuilder sb = new StringBuilder();

        if(currentBalance>0){
            newBalance = currentBalance - AmountDue;
        }else{
            newBalance = currentBalance + AmountDue;
        }

        newBalance = currentBalance - AmountDue;

            sb.append(getReceiptHeader());
            sb.append("        ").append(date).append("\r\n");
            sb.append("\r\n");
        if(newBalance<0){
            TYPE = TYPE_PREPAYMENT;
            sb.append("   - Prepayment Receipt - ").append("\r\n");
        }else{
            TYPE = TYPE_PAY_ARREARS;
            sb.append("    - Arrears Receipt - ").append("\r\n");
        }
            //sb.append("         - Receipt - ");
            sb.append("\r\n");
            sb.append("\r\n");
            sb.append("Site        : City Parking (PVT)");
            sb.append("Zone        : ").append(zone).append("\r\n");
            sb.append("Precinct    : ").append(precinctName).append("\r\n");
            sb.append("Marshal     : ").append(currentUser).append("\r\n");
            sb.append("Receipt     : ").append(recieptNumber).append("\r\n");
            sb.append("Reg No.     : ").append(reg_number).append("\r\n");
        if(currentBalance>0){
            sb.append("Tot Arrears : $").append(df.format(currentBalance)).append("\r\n");
        }else if(currentBalance==0){
            sb.append("Balance     : $").append(df.format(currentBalance)).append("\r\n");
        }else{
            sb.append("Tot Prepaid : $").append(df.format(Math.abs(currentBalance))).append("\r\n");
        }
            sb.append("Amt Paid    : $").append(df.format(AmountDue)).append(" 15% VAT Inc").append("\r\n");
            sb.append("Tendered    : $").append(df.format(tender)).append("\r\n");
            sb.append("Change      : $").append(df.format(change)).append("\r\n");



        if(newBalance<0){
            sb.append("Prepaid     : $").append(df.format(Math.abs(newBalance))).append("\r\n");
        }else{
            sb.append("Now Owing   : $").append(df.format(newBalance)).append("\r\n");
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

    public String getPrecinctName() {
        return precinctName;
    }

    public void setPrecinctName(String precinctName) {
        this.precinctName = precinctName;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public String getRecieptNumber() {
        return recieptNumber;
    }

    public void setRecieptNumber(String recieptNumber) {
        this.recieptNumber = recieptNumber;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public double getAmountDue() {
        return AmountDue;
    }

    public void setAmountDue(double amountDue) {
        AmountDue = amountDue;
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

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public int getBayNumber() {
        return bayNumber;
    }

    public void setBayNumber(int bayNumber) {
        this.bayNumber = bayNumber;
    }

    public boolean isOffline() {
        return isOffline;
    }

    public void setOffline(boolean offline) {
        isOffline = offline;
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

    public double getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(double newBalance) {
        this.newBalance = newBalance;
    }
}
