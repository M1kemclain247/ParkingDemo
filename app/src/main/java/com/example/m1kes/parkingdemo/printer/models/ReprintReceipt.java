package com.example.m1kes.parkingdemo.printer.models;



public class ReprintReceipt extends Receipt {

    private String printData;


    public ReprintReceipt(String printData) {
        this.printData = printData;
    }

    public ReprintReceipt(PrinterJob printerJob) {
        this.printData = printerJob.getPrintData();
    }

    @Override
    public String getPrintData() {

        StringBuilder sb = new StringBuilder();
        sb.append("********************************\r\n");
        sb.append(" ********  REPRINT COPY  ******\r\n");
        sb.append("********************************\r\n");
        sb.append(printData);

        return sb.toString();
    }

    @Override
    public String getPrintType() {
        return TYPE_REPRINT;
    }
}
