package com.example.m1kes.parkingdemo.printer.models;


import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PayFineReceipt extends Receipt {

    private String reg_number;
    private int num_fines_owing;
    private double fines_owing;
    private double fine_payment;
    private double tender_amount;
    private double change;
    private DecimalFormat df = new DecimalFormat("#.00");
    private String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());


    public PayFineReceipt() {


    }

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
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("         City Marshall    \r\n");
        sb.append("    Technology Co. Ltd.  \r\n");
        sb.append("\r\n");
        sb.append("     House 1, School Of Java  \r\n");
        sb.append("        Harare,Zimbabwe \r\n");
        sb.append("\r\n");
        sb.append("       ").append(date).append("\r\n");
        sb.append("\r\n");
        sb.append("Registration           ").append(reg_number).append("\r\n");
        sb.append("--------------------------------\r\n");
        sb.append("Due Fine's                    ").append(num_fines_owing).append("\r\n");
        sb.append("Fine's Owning             $").append(df.format(fines_owing)).append("\r\n");
        sb.append("--------------------------------\r\n");
        sb.append("Fine Payment              $").append(df.format(fine_payment)).append("\r\n");
        sb.append("Tender                    $").append(df.format(tender_amount)).append("\r\n");
        sb.append("Change                    $").append(df.format(change)).append("\r\n");
        sb.append("--------------------------------\r\n");
        sb.append("Total                     $").append(df.format(fine_payment)).append("\r\n");
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("\r\n");

        this.printData = sb.toString();


        return sb.toString();
    }


    public int getNum_fines_owing() {
        return num_fines_owing;
    }

    public void setNum_fines_owing(int num_fines_owing) {
        this.num_fines_owing = num_fines_owing;
    }

    public String getReg_number() {
        return reg_number;
    }

    public void setReg_number(String reg_number) {
        this.reg_number = reg_number;
    }

    public double getFines_owing() {
        return fines_owing;
    }

    public void setFines_owing(double fines_owing) {
        this.fines_owing = fines_owing;
    }

    public double getFine_payment() {
        return fine_payment;
    }

    public void setFine_payment(double fine_payment) {
        this.fine_payment = fine_payment;
    }

    public double getTender_amount() {
        return tender_amount;
    }

    public void setTender_amount(double tender_amount) {
        this.tender_amount = tender_amount;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }


    /**
     *
     * Template for Printing
     *
     StringBuilder sb = new StringBuilder();
     sb.append("    Parking Demo Applicaton    \n");
     sb.append("         Technology Co., Ltd.  \n");
     sb.append("\n");
     sb.append("    Room 305A, School Of Java  \n");
     sb.append("    I.T. Zone, Harare,Zimbabwe \n");
     sb.append("         2016-11-01\n");
     sb.append("Registration             ABW5321\n");
     sb.append("--------------------------------\n");
     sb.append("Fine's Owning              $0.00\n");
     sb.append("--------------------------------\n");
     sb.append("Fine Payment               $2.00\n");
     sb.append("Customer's payment         $5.00\n");
     sb.append("Change                     $3.00\n");
     sb.append("--------------------------------\n");
     sb.append("Total                      $2.00\n");

     */


}
