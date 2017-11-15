package com.example.m1kes.parkingdemo.modules.supervisor.printer.models;

import com.example.m1kes.parkingdemo.printer.models.Receipt;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class SpotCheckReceipt extends Receipt {

    private String siteName;
    private String zoneName;
    private String precinctName;
    private String recieptNumber;
    private String marshalDetails;
    private String supervisor;

    private double expectedAmount;
    private double prepaidAmount;
    private double countedAmount;
    private double shortAmount;


    private DecimalFormat df = new DecimalFormat("0.00");
    private String recieptPrintDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());


    @Override
    public String getPrintData() {
        return getReceipt();
    }

    @Override
    public String getPrintType() {
        return TYPE_SPOT_CHECK;
    }

    public String getReceipt(){

        StringBuilder sb = new StringBuilder();
        sb.append(getReceiptHeader());
        sb.append("      ").append(recieptPrintDateTime).append("\r\n");
        sb.append("\r\n");
        sb.append("     - Spot Check Receipt - ");
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("Site     :  ").append(siteName).append("\r\n");
        sb.append("Zone     :  ").append(zoneName).append("\r\n");
        if(precinctName!=null){
            sb.append("Precinct : ").append(precinctName).append("\r\n");
        }
        sb.append("Reciept  :  ").append(recieptNumber).append("\r\n");
        sb.append("Marshal  :  ").append(marshalDetails).append("\r\n");
        sb.append("Superv.  :  ").append(supervisor).append("\r\n");
        sb.append("--------------------------------\r\n");
        sb.append("Prepaid  :  $").append(df.format(prepaidAmount)).append("\r\n");
        sb.append("Expected :  $").append(df.format(expectedAmount)).append("\r\n");
        sb.append("Counted  :  $").append(df.format(countedAmount)).append("(15% VAT Incl)").append("\r\n");
        sb.append("--------------------------------\r\n");
        if(shortAmount<0){
            sb.append("Over Amt :  $").append(df.format(Math.abs(shortAmount))).append("\r\n");
        }else{
            sb.append("Short Amt:  $").append(df.format(shortAmount)).append("\r\n");
        }
        sb.append("--------------------------------\r\n");
        sb.append("\r\n");
        sb.append("Marshal  : _ _ _ _ _ _ _ _ _ _ _\r\n");
        sb.append("\r\n");
        sb.append("Supervisor : _ _ _ _ _ _ _ _ _ _ \r\n");
        sb.append("\r\n");
        sb.append(getReceiptFooter());

        this.printData = sb.toString();

        return sb.toString();
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
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

    public String getMarshalDetails() {
        return marshalDetails;
    }

    public void setMarshalDetails(String marshalDetails) {
        this.marshalDetails = marshalDetails;
    }

    public String getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(String supervisor) {
        this.supervisor = supervisor;
    }

    public double getExpectedAmount() {
        return expectedAmount;
    }

    public void setExpectedAmount(double expectedAmount) {
        this.expectedAmount = expectedAmount;
    }

    public double getPrepaidAmount() {
        return prepaidAmount;
    }

    public void setPrepaidAmount(double prepaidAmount) {
        this.prepaidAmount = prepaidAmount;
    }

    public double getCountedAmount() {
        return countedAmount;
    }

    public void setCountedAmount(double countedAmount) {
        this.countedAmount = countedAmount;
    }

    public double getShortAmount() {
        return shortAmount;
    }

    public void setShortAmount(double shortAmount) {
        this.shortAmount = shortAmount;
    }




}
