package com.example.m1kes.parkingdemo.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.example.m1kes.parkingdemo.util.DBUtils.getDateParts;

public class GeneralUtils {


    public static String getDeviceId(Context context) {
            return android.os.Build.SERIAL;
    }

    public static String getDeviceIMEI(Context context) {
        final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (deviceId != null) {
            System.out.println("Device Serial/IMEI: "+deviceId);
            return deviceId;
        } else {
            System.out.println("Device Serial/IMEI: "+android.os.Build.SERIAL);
            return android.os.Build.SERIAL;
        }
    }

    /**
     * For now we are using the simcard phone number
     * **/
    public static String getTerminalNo(Context context){
        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return  tMgr.getLine1Number();
    }


    public static String GenerateRecieptNumber(String precinctID,String userId,String i){

        String precinctIdNew,empIdNew,iNew;
        System.out.println(precinctID.length());

        precinctIdNew = precinctID.trim();
        if(precinctIdNew.length()== 1){

            precinctIdNew = "0000"+precinctIdNew;
            System.out.println("if stmnt");
            System.out.println(precinctIdNew);


        }else if(precinctIdNew.length()== 2){


            precinctIdNew= "000"+precinctIdNew;
            System.out.println("else if 1 stmnt");


        }else if(precinctIdNew.length()== 3){
            precinctIdNew= "00"+precinctIdNew;
            System.out.println("else if 2 stmnt");
        }else if(precinctIdNew.length()== 4){
            precinctIdNew= "0"+precinctIdNew;
            System.out.println("else if 3 stmnt");
        }



        empIdNew = userId.trim();
        if(empIdNew.length()== 1){
            empIdNew= "00"+empIdNew;

        }else if(empIdNew.length()== 2){
            System.out.println("else if of emp id");
            empIdNew= "0"+empIdNew;
        }


        iNew = i.trim();
        if(iNew.length()== 1){
            iNew= "00"+iNew;

        }else if(iNew.length()== 2){
            System.out.println("else if of emp id");
            iNew= "0"+iNew;
        }

        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        System.out.println(date);
        System.out.println(precinctIdNew);
        System.out.println(empIdNew);


        String finalCode = date +empIdNew+""+iNew+precinctIdNew;
        return finalCode;

    }

    public static  Printer generatePrinter(PrinterJob printerJob){

        if(printerJob!=null
                &&printerJob.getPrinterModel()!=null
                &&printerJob.getPrinterIMEI()!=null){
            System.out.println("PrintJob is OK");
            return new Printer(printerJob.getPrinterCode(),printerJob.getPrinterModel(),printerJob.getPrinterIMEI());
        }else{
            System.out.println("Problem with PrintJob");
            return null;
        }
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }


    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    private void sleep(long sleepTime){
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * Responsible for removing all special characters and spaces from the Vehicle Reg
     * **/
    public static String cleanRawVehicleReg(String rawVehicleReg){

        String firstClean;

        firstClean = rawVehicleReg.replaceAll("[^a-zA-Z0-9]+", " ");
        String secondClean =  firstClean.replaceAll("\\s+","");
        String lastClean =  secondClean.trim();

        return lastClean;
    }


    public static String getCurrentDateTime(){

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        return  sf.format(calendar.getTime());

    }

    public static String getCurrentDateTimeFormatted(){

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        String unformatted = sf.format(calendar.getTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        Calendar cal = Calendar.getInstance();
        String[] parts= getDateParts(unformatted);
        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
        Date date = cal.getTime();
        return formatter.format(date);
    }

    public static String formatRawLongTime(long timeInMillis){

        String unformatted = ""+timeInMillis;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar cal = Calendar.getInstance();
        String[] parts= getDateParts(unformatted);
        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
        Date date = cal.getTime();
        return formatter.format(date);
    }

    public static String getFormattedDate(long datetime){
        String rawDateString  = ""+datetime;
        //Format the start Time
        SimpleDateFormat pf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = null;
        Date startDate  = null;
        try {
            startDate = df2.parse(rawDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formattedDate = pf.format(startDate);
        return formattedDate;
    }

    public static String getVersionNumber(Context context){

        //Overall information about the contents of a package
        //This corresponds to all of the information collected from AndroidManifest.xml.
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = null;
        //get the app version Name for display
        if(pInfo!=null){
            version = pInfo.versionName;
        }else{
            System.out.println("Failed to Get version Number for app");
        }
        System.out.println("Returning version No: "+version);
        return version;
    }

}
