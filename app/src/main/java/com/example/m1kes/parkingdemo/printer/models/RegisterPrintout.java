package com.example.m1kes.parkingdemo.printer.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.example.m1kes.parkingdemo.RegisterAPrinter.MY_PREFS_NAME;



public class RegisterPrintout extends Receipt {


    private String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
    private Context context;
    private boolean status;

   public  RegisterPrintout(Context context,boolean status){
        this.context = context;
        this.status = status;
    }

    @Override
   public String getPrintData() {
        return getPrintoutData();
    }

    private String getPrintoutData(){


        StringBuilder sb = new StringBuilder();
        sb.append("\r\n");

        sb.append(" *** City Parking (Pvt) Ltd ***  ");
        sb.append("   **** Zimbabwe   **** \r\n");
        sb.append("   ****  VAT # :  10059604  *** \r\n");
        sb.append("\r\n");
        sb.append("        ").append(date).append("\r\n");
        sb.append("\r\n");
        sb.append("      Printer Registration \r\n");
        if(status){
            sb.append("   Registering Successful\r\n");
        }else{
            sb.append("   Registering failed\r\n");
        }
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("Marshal Username :  "+ UserAdapter.getLoggedInUser(context));
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("--------------------------------");
        sb.append("\r\n");
        sb.append("  *City Marshall Android v1.0* \r\n");
        sb.append(" --- -- --- -- --- -- --- -- --- \r\n");
        sb.append("\r\n");

        return sb.toString();
    }


    @Override
   public String getPrintType() {
        return TYPE_REGISTER;
    }
}
