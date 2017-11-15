package com.example.m1kes.parkingdemo.printer.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.m1kes.parkingdemo.BuildConfig;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;
import static com.example.m1kes.parkingdemo.RegisterAPrinter.MY_PREFS_NAME;


/**
 * Created by clive on 21/11/2016.
 * darrel code
 */

public class PrinterStringMaker {

    private Context context;
    String msg;
    private String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());


    public PrinterStringMaker(Context context) {
        this.context = context;
    }

    /**
     *
     * @return String representation of a fiscal Receipt
     */

    public String verificationCodeGenerator(){

        String[] any = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n"};
        Random rand = new Random();
        int n = rand.nextInt(any.length-1)+1;
        String aa = any[n];

        Random randomNum = new Random();
        int numRand = rand.nextInt(90)+10;


        Random randomNum2 = new Random();
        int numRand2 = rand.nextInt(9)+1;

        String code = ""+numRand+aa.toUpperCase()+numRand2;

        return code;
    }



    public String getReceipt(){

        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
       // String restoredText = prefs.getString("text", null);

            this.msg = prefs.getString("msgOfSuccess", "Failed");//"No name defined" is the default value.
            if(msg.equalsIgnoreCase("{\"Success\":\"Paired to printer successfully\"}")){
                this.msg = "Successful";

            }else{
                this.msg = "Unsuccessful";
            }




        StringBuilder sb = new StringBuilder();
        sb.append("\r\n");

        sb.append(" *** City Parking (Pvt) Ltd ***  ");
        sb.append("   **** Zimbabwe   **** \r\n");
        sb.append("   ****  VAT # :  10059604  *** \r\n");
        sb.append("\r\n");
        sb.append("        ").append(date).append("\r\n");
        sb.append("\r\n");
        sb.append("      Printer Registration \r\n");
        sb.append("         "+msg);
        sb.append("\r\n");
        sb.append("\r\n");
        String username = UserAdapter.getLoggedInUser(context);
        if(username!=null){
            sb.append("Marshal Username :  "+UserAdapter.getLoggedInUser(context));
        }else{
            sb.append("");
        }

        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("--------------------------------");
        sb.append("\r\n");
        String versionName = BuildConfig.VERSION_NAME;
        sb.append("  *City Marshal Android v").append(versionName).append("* \r\n");
        sb.append(" --- -- --- -- --- -- --- -- --- \r\n");
        sb.append("\r\n");



        return sb.toString();
    }



}
