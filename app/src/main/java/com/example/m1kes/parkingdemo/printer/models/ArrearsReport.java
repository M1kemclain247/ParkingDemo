package com.example.m1kes.parkingdemo.printer.models;


import org.w3c.dom.ls.LSInput;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ArrearsReport extends Receipt {

    private int id;
    private String zoneName;
    private String transactionType;
    private String vehicleReg;
    private long in_datetime;
    private long out_datetime;
    private int duration;
    private int amount;
    private double balance;
    private double totalArrears;
    private String currentMarshalUsername;

    private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private String currentTimeDate = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());

    private List<Arrear> arrearslist = null;

    public ArrearsReport(List<Arrear> arrearslist ,String vehicleReg,String currentMarshalUsername){
        this.arrearslist = arrearslist;
        this.vehicleReg = vehicleReg;
        this.currentMarshalUsername = currentMarshalUsername;
        totalArrears = generateTotalAmountDue();
    }

    private double generateTotalAmountDue() {

        double totalAmountDue = 0;

        for(Arrear arrear: arrearslist){
            totalAmountDue += arrear.getAmount();
        }

        return totalAmountDue;


    }


    @Override
   public String getPrintData() {

        /**
         *
         * @return String representation of a fiscal Receipt
         */

        StringBuilder sb = new StringBuilder();
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append(" *** City Parking (Pvt) Ltd ***  ");
        sb.append("   **** Zimbabwe   **** \r\n");
        sb.append("   ****  VAT # :  10059604  *** \r\n");
        sb.append("\r\n");
        sb.append("        ").append(currentTimeDate).append("\r\n");
        sb.append("\r\n");
        sb.append("     - Arrears Report Summary - ");
        sb.append("\r\n");
        sb.append("Vehicle Reg   : ").append(vehicleReg).append("\r\n");
        sb.append("No of Arrears : ").append(arrearslist.size()).append("\r\n");
        sb.append("Arrears Value : $").append(totalArrears).append("\r\n");
        sb.append("Printed By    : ").append(currentMarshalUsername).append("\r\n");
        sb.append("\r\n");
        if(arrearslist.size()==0){
            sb.append("  No Outstanding Arrears").append("\r\n");
        }else{
            sb.append("  Arrears Details :       ").append("\r\n");
        }
        sb.append("\r\n");

        int localCount= 1;
        for(Arrear arrear : arrearslist){




             sb.append("--------------------------------");
             sb.append("Arrear Number: ").append(localCount).append("\r\n");
             sb.append("Arrear Date  : ").append(convertoDate(arrear.getIn_datetime())).append("\r\n");
             sb.append(arrear.getZoneName()).append("      : ").append(arrear.getPrecinctName()).append("\r\n");
             sb.append("Marshall     : ").append(arrear.getMarshall()).append("\r\n");
             sb.append("Receipt No   : ").append(arrear.getReceiptNo()).append("\r\n");
             sb.append("Time In      : ").append(removeSeconds(arrear.getIn_datetime())).append("\r\n");
             sb.append("Duration     : ").append(arrear.getDuration()).append("hrs").append("\r\n");
             sb.append("Tarriff      : $").append(arrear.getTarrif()).append("\r\n");
             sb.append("Arrear Amount: $").append(arrear.getAmount()).append("\r\n");
             sb.append("--------------------------------");
             localCount++;
        }
        sb.append(getReceiptFooter());
        this.printData = sb.toString();



        return sb.toString();
    }

    private String convertoDate(String in_datetime){

        SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String reformattedStr = "";
        try {

            reformattedStr = yearDateFormat.format(defaultFormat.parse(in_datetime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;

    }

    private String removeSeconds(String in_datetime){

        SimpleDateFormat defaultFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat yearDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String reformattedStr = "";
        try {

            reformattedStr = yearDateFormat.format(defaultFormat.parse(in_datetime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return reformattedStr;

    }

    @Override
   public String getPrintType() {
        return TYPE_ARREARS_REPORT;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getVehicleReg() {
        return vehicleReg;
    }

    public void setVehicleReg(String vehicleReg) {
        this.vehicleReg = vehicleReg;
    }

    public long getIn_datetime() {
        return in_datetime;
    }

    public void setIn_datetime(long in_datetime) {
        this.in_datetime = in_datetime;
    }

    public long getOut_datetime() {
        return out_datetime;
    }

    public void setOut_datetime(long out_datetime) {
        this.out_datetime = out_datetime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }


    public String getCurrentMarshalUsername() {
        return currentMarshalUsername;
    }

    public void setCurrentMarshalUsername(String currentMarshalUsername) {
        this.currentMarshalUsername = currentMarshalUsername;
    }


    public double getTotalArrears() {
        return totalArrears;
    }

    public void setTotalArrears(double totalArrears) {
        this.totalArrears = totalArrears;
    }

    public List<Arrear> getArrearslist() {
        return arrearslist;
    }

    public void setArrearslist(List<Arrear> arrearslist) {
        this.arrearslist = arrearslist;
    }

    public String getCurrentTimeDate() {
        return currentTimeDate;
    }

    public void setCurrentTimeDate(String currentTimeDate) {
        this.currentTimeDate = currentTimeDate;
    }


    @Override
    public String toString() {
        return "ArrearsReport{" +
                "id=" + id +
                ", zoneName='" + zoneName + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", vehicleReg='" + vehicleReg + '\'' +
                ", in_datetime=" + in_datetime +
                ", out_datetime=" + out_datetime +
                ", duration=" + duration +
                ", amount=" + amount +
                ", balance=" + balance +
                ", totalArrears=" + totalArrears +
                ", currentMarshalUsername='" + currentMarshalUsername + '\'' +
                ", format=" + format +
                ", currentTimeDate='" + currentTimeDate + '\'' +
                ", arrearslist=" + arrearslist +
                '}';
    }
}
