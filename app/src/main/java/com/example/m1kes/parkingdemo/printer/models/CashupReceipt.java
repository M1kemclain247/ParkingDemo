package com.example.m1kes.parkingdemo.printer.models;


import com.example.m1kes.parkingdemo.transactions.models.CashupTransaction;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CashupReceipt extends Receipt {

    private CashupTransaction trans;
    private DecimalFormat df = new DecimalFormat("0.00");

    private String recieptPrintDateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());

    public CashupReceipt(CashupTransaction cashupTransaction){
        this.trans = cashupTransaction;
    }


    @Override
  public   String getPrintData() {
        return getReceipt() +"\n"+getReceipt();
    }

    @Override
   public String getPrintType() {
        return TYPE_CASHUP;
    }


    /**
     *
     * @return String representation of a fiscal Receipt
     */

    public String getReceipt(){

        StringBuilder sb = new StringBuilder();
        sb.append(getReceiptHeader());
        sb.append("      ").append(recieptPrintDateTime).append("\r\n");
        sb.append("\r\n");
        sb.append("     - Shift CashUp Receipt - ");
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("Site     :  ").append(trans.getSiteName()).append("\r\n");
        sb.append("Zone     :  ").append(trans.getZoneName()).append("\r\n");
        if(trans.getPrecinctName()!=null){
            sb.append("Precinct : ").append(trans.getPrecinctName()).append("\r\n");
        }
        sb.append("Reciept  :  ").append(trans.getRecieptNumber()).append("\r\n");
        sb.append("ShiftID  :  ").append(trans.getShiftID()).append("\r\n");
        sb.append("Marshal  :  ").append(trans.getMarshalDetails()).append("\r\n");
        sb.append("Superv.  :  ").append(trans.getSupervisor()).append("\r\n");
        sb.append("Start    :  ").append(trans.getShiftStartTime()).append("\r\n");
        sb.append("End      :  ").append(trans.getShiftEndTime()).append("\r\n");
        sb.append("Duration :  ").append(trans.getShiftDuration()).append("\r\n");
        sb.append("--------------------------------\r\n");
        sb.append("Total    :  $").append(df.format(trans.getTotalAmount())).append("\r\n");
        sb.append("E-payment:  $").append(df.format(trans.getEpaymentAmount())).append("\r\n");
        sb.append("Cash     :  $").append(df.format(trans.getExpectedAmount())).append("\r\n");
        //sb.append("Counted      :  $").append(trans.getCountedAmount().ToString(df)).Append("\r\n");
        sb.append("Declared :  $").append(df.format(trans.getCountedAmount())).append("(15% VAT Incl)").append("\r\n");
        sb.append("--------------------------------\r\n");
        if(trans.getShortAmount()<0){
        sb.append("Over Amt :  $").append(df.format(Math.abs(trans.getShortAmount()))).append("\r\n");
        }else{
        sb.append("Short Amt:  $").append(df.format(trans.getShortAmount())).append("\r\n");
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



}
