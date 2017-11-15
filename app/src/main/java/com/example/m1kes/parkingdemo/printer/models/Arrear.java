package com.example.m1kes.parkingdemo.printer.models;


public class Arrear {

    private int id;
    private String precinctName;
    private String zoneName;
    private String transactionType;
    private String vehicleReg;
    private String in_datetime;
    private String out_datetime;
    private int duration;
    private double amount;
    private double balance;
    private String marshall;
    private String receiptNo;
    private double tarrif;


    public Arrear() {
    }

    public Arrear(int id, String precinctName, String transactionType, String vehicleReg, String in_datetime, String out_datetime, int duration, double amount, double balance, String marshall) {
        this.id = id;
        this.precinctName = precinctName;
        this.transactionType = transactionType;
        this.vehicleReg = vehicleReg;
        this.in_datetime = in_datetime;
        this.out_datetime = out_datetime;
        this.duration = duration;
        this.amount = amount;
        this.balance = balance;
        this.marshall = marshall;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrecinctName() {
        return precinctName;
    }

    public void setPrecinctName(String precinctName) {
        this.precinctName = precinctName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        this.receiptNo = receiptNo;
    }

    public double getTarrif() {
        return tarrif;
    }

    public void setTarrif(double tarrif) {
        this.tarrif = tarrif;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }

    public void setVehicleReg(String vehicleReg) {
        this.vehicleReg = vehicleReg;
    }

    public String getIn_datetime() {
        return in_datetime;
    }

    public void setIn_datetime(String in_datetime) {
        this.in_datetime = in_datetime;
    }

    public String getOut_datetime() {
        return out_datetime;
    }

    public void setOut_datetime(String out_datetime) {
        this.out_datetime = out_datetime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getMarshall() {
        return marshall;
    }

    public void setMarshall(String marshall) {
        this.marshall = marshall;
    }

    @Override
    public String toString() {
        return "Arrear{" +
                "id=" + id +
                ", precinctName='" + precinctName + '\'' +
                ", zoneName='" + zoneName + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", vehicleReg='" + vehicleReg + '\'' +
                ", in_datetime='" + in_datetime + '\'' +
                ", out_datetime='" + out_datetime + '\'' +
                ", duration=" + duration +
                ", amount=" + amount +
                ", balance=" + balance +
                ", marshall='" + marshall + '\'' +
                ", receiptNo='" + receiptNo + '\'' +
                ", tarrif=" + tarrif +
                '}';
    }
}
