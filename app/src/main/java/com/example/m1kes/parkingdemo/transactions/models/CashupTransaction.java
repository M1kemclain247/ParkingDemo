package com.example.m1kes.parkingdemo.transactions.models;


import java.io.Serializable;

public class CashupTransaction implements Serializable{

    private String siteName;
    private String zoneName;
    private String precinctName;
    private String recieptNumber;
    private String marshalDetails;
    private String supervisor;
    private String shiftStartTime;
    private String shiftEndTime;
    private String shiftDuration;
    private String shiftID;

    private double expectedAmount;
    private double prepaidAmount;
    private double countedAmount;
    private double shortAmount;
    private double epaymentAmount;
    private double totalAmount;


    public CashupTransaction() {
    }

    public CashupTransaction(String siteName, String zoneName, String precinctName, String recieptNumber, String marshalDetails, String supervisor, String shiftStartTime, String shiftEndTime, String shiftDuration, double expectedAmount, double countedAmount) {
        this.siteName = siteName;
        this.zoneName = zoneName;
        this.precinctName = precinctName;
        this.recieptNumber = recieptNumber;
        this.marshalDetails = marshalDetails;
        this.supervisor = supervisor;
        this.shiftStartTime = shiftStartTime;
        this.shiftEndTime = shiftEndTime;
        this.shiftDuration = shiftDuration;
        this.expectedAmount = expectedAmount;
        this.countedAmount = countedAmount;
    }

    public void setShortAmount(double shortAmount) {
        this.shortAmount = shortAmount;
    }

    public double getEpaymentAmount() {
        return epaymentAmount;
    }

    public void setEpaymentAmount(double epaymentAmount) {
        this.epaymentAmount = epaymentAmount;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getShiftID() {
        return shiftID;
    }

    public void setShiftID(String shiftID) {
        this.shiftID = shiftID;
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

    public double getPrepaidAmount() {
        return prepaidAmount;
    }

    public void setPrepaidAmount(double prepaidAmount) {
        this.prepaidAmount = prepaidAmount;
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

    public String getShiftStartTime() {
        return shiftStartTime;
    }

    public void setShiftStartTime(String shiftStartTime) {
        this.shiftStartTime = shiftStartTime;
    }

    public String getShiftEndTime() {
        return shiftEndTime;
    }

    public void setShiftEndTime(String shiftEndTime) {
        this.shiftEndTime = shiftEndTime;
    }

    public String getShiftDuration() {
        return shiftDuration;
    }

    public void setShiftDuration(String shiftDuration) {
        this.shiftDuration = shiftDuration;
    }

    public double getExpectedAmount() {
        return expectedAmount;
    }

    public void setExpectedAmount(double expectedAmount) {
        this.expectedAmount = expectedAmount;
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

    public void setShortAmount(double expectedAmount,double countedAmount) {

        double shortAmount =  expectedAmount - countedAmount;
        this.shortAmount = shortAmount;
    }

    @Override
    public String toString() {
        return "CashupTransaction{" +
                "siteName='" + siteName + '\'' +
                ", zoneName='" + zoneName + '\'' +
                ", precinctName='" + precinctName + '\'' +
                ", recieptNumber='" + recieptNumber + '\'' +
                ", marshalDetails='" + marshalDetails + '\'' +
                ", supervisor='" + supervisor + '\'' +
                ", shiftStartTime='" + shiftStartTime + '\'' +
                ", shiftEndTime='" + shiftEndTime + '\'' +
                ", shiftDuration='" + shiftDuration + '\'' +
                ", shiftID='" + shiftID + '\'' +
                ", expectedAmount=" + expectedAmount +
                ", prepaidAmount=" + prepaidAmount +
                ", countedAmount=" + countedAmount +
                ", shortAmount=" + shortAmount +
                '}';
    }
}
