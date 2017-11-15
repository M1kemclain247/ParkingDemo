package com.example.m1kes.parkingdemo.printer.models;


import com.example.m1kes.parkingdemo.BuildConfig;
import com.example.m1kes.parkingdemo.util.GeneralUtils;

import java.io.Serializable;

public abstract class Receipt implements Serializable{

    public String printData;

    public static final String TYPE_MAKE_FINE ="MakeFine";
    public static final String TYPE_MAKE_PAYMENT ="MakePayment";
    public static final String TYPE_PAY_ARREARS ="PayArrears";
    public static final String TYPE_PREPAYMENT ="Prepayment";
    public static final String TYPE_SEARCH_VEHICLE ="SearchVehicle";
    public static final String TYPE_LOGGED_VEHICLES ="LoggedVehicles";
    public static final String TYPE_CASHUP ="Cashup";
    public static final String TYPE_REGISTER ="Register";
    public static final String TYPE_SPOT_CHECK ="SpotCheck";

    public static final String TYPE_BALANCE_ENQUIRY ="BalanceEnquiry";
    public static final String TYPE_ARREARS_REPORT ="ArrearsReport";
    public static final String TYPE_REPRINT ="Reprint";


    public Receipt() {
    }


    public Receipt(String printData) {
        this.printData = printData;
    }

    public String getPrintString(){
        return getPrintData();
    }

    public String getPrintTypeAll(){
        return getPrintType();
    }

    public abstract String getPrintData();

    public abstract String getPrintType();

    public String getReceiptHeader(){
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append(" *** City Parking (Pvt) Ltd ***  ");
        sb.append("   **** Zimbabwe   **** \r\n");
        sb.append("   ****  VAT # :  10059604  *** \r\n");
        sb.append("\r\n");
        return sb.toString();
    }

    public String getReceiptFooter(){

        StringBuilder sb = new StringBuilder();

        sb.append("--------------------------------");
        sb.append("\r\n");
        sb.append("For any queries or Complaints,\r\n contact:\r\n");
        sb.append("  Customer Care \r\n");
        sb.append("  Phone : 707 506/315/876 \r\n");
        sb.append("  Cell  : 073 988 5896 \r\n");
        sb.append(" customercare@cityparking.co.zw \r\n");
        sb.append(" --- -- --- -- --- -- --- -- ---");
        sb.append("    *Powered By City Parking*\r\n");
        String versionName = BuildConfig.VERSION_NAME;
        sb.append("  *City Marshal Android v").append(versionName).append("* \r\n");
        sb.append(" --- -- --- -- --- -- --- -- --- \r\n");
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("\n");
        return sb.toString();

    }

}
