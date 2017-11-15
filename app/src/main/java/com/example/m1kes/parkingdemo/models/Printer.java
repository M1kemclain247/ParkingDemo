package com.example.m1kes.parkingdemo.models;



public class Printer {

    private int id;
    private String printerCode;
    private String printerModel;
    private String printerIMEI;
    private boolean isPaired;


    public Printer() {
    }

    public Printer(int id, String printerCode, String printerIMEI) {
        this.id = id;
        this.printerCode = printerCode;
        this.printerIMEI = printerIMEI;
    }

    public Printer(String printerCode, String printerIMEI) {
        this.printerCode = printerCode;
        this.printerIMEI = printerIMEI;
    }

    public Printer(int id, String printerCode, String printerIMEI, String printerModel) {
        this.id = id;
        this.printerCode = printerCode;
        this.printerIMEI = printerIMEI;
        this.printerModel = printerModel;
    }


    public Printer(String printerCode,String printerModel ,String printerIMEI,boolean isPaired) {
        this.printerCode = printerCode;
        this.printerIMEI = printerIMEI;
        this.printerModel = printerModel;
        this.isPaired = isPaired;
    }

    public Printer(String printerCode,String printerModel ,String printerIMEI) {
        this.printerCode = printerCode;
        this.printerIMEI = printerIMEI;
        this.printerModel = printerModel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrinterCode() {
        return printerCode;
    }

    public void setPrinterCode(String printerCode) {
        this.printerCode = printerCode;
    }

    public String getPrinterIMEI() {
        return printerIMEI;
    }

    public void setPrinterIMEI(String printerIMEI) {
        this.printerIMEI = printerIMEI;
    }

    public String getPrinterModel() {
        return printerModel;
    }

    public void setPrinterModel(String printerModel) {
        this.printerModel = printerModel;
    }

    public boolean isPaired() {
        return isPaired;
    }

    public void setPaired(boolean paired) {
        isPaired = paired;
    }

    @Override
    public String toString() {
        return "Printer{" +
                "id=" + id +
                ", printerCode='" + printerCode + '\'' +
                ", printerModel='" + printerModel + '\'' +
                ", printerIMEI='" + printerIMEI + '\'' +
                ", isPaired=" + isPaired +
                '}';
    }
}
