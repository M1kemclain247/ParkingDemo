package com.example.m1kes.parkingdemo.printer.models;


import android.speech.RecognizerIntent;

import com.example.m1kes.parkingdemo.models.Printer;

import java.io.Serializable;
import java.util.Date;

public class PrinterJob implements Serializable{


    public static final String STATUS_PENDING = "Print_Pending";
    public static final String STATUS_COMPLETE = "Print_Complete";

    private int id;
    //For the printer that tried to print it
    private String printerModel;
    private String printerCode;
    private String printerIMEI;

    private Date printAttemptTime;
    private String shiftId;

    private String printType;
    private String printData;
    private String printStatus;
    private int numAttempts;



    public PrinterJob() {
    }

    public PrinterJob(String printerModel, String printerCode, String printerIMEI, Date printAttemptTime, String shiftId,Receipt receipt) {
        this.printerModel = printerModel;
        this.printerCode = printerCode;
        this.printerIMEI = printerIMEI;
        this.printAttemptTime = printAttemptTime;
        this.shiftId = shiftId;
        this.printData = receipt.getPrintData();
        this.printType = receipt.getPrintType();

    }

    public Printer createPrinter(String printerCode,String printerModel,String printerIMEI){
        return new Printer(printerCode,printerModel,printerIMEI);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPrinterModel() {
        return printerModel;
    }

    public void setPrinterModel(String printerModel) {
        this.printerModel = printerModel;
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

    public Date getPrintAttemptTime() {
        return printAttemptTime;
    }

    public void setPrintAttemptTime(Date printAttemptTime) {
        this.printAttemptTime = printAttemptTime;
    }

    public String getShiftId() {
        return shiftId;
    }

    public void setShiftId(String shiftId) {
        this.shiftId = shiftId;
    }

    public String getPrintType() {
        return printType;
    }

    public void setPrintType(String printType) {
        this.printType = printType;
    }

    public String getPrintData() {
        return printData;
    }

    public void setPrintData(String printData) {
        this.printData = printData;
    }


    public String getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(String printStatus) {
        this.printStatus = printStatus;
    }

    public int getNumAttempts() {
        return numAttempts;
    }

    public void setNumAttempts(int numAttempts) {
        this.numAttempts = numAttempts;
    }

    @Override
    public String toString() {
        return "PrinterJob{" +
                "id=" + id +
                ", printerModel='" + printerModel + '\'' +
                ", printerCode='" + printerCode + '\'' +
                ", printerIMEI='" + printerIMEI + '\'' +
                ", printAttemptTime=" + printAttemptTime +
                ", shiftId='" + shiftId + '\'' +
                ", printType='" + printType + '\'' +
                ", printData='" + printData + '\'' +
                ", printStatus='" + printStatus + '\'' +
                ", numAttempts=" + numAttempts +
                '}';
    }
}

