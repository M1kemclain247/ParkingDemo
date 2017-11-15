package com.example.m1kes.parkingdemo.settings.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.example.m1kes.parkingdemo.settings.models.DeviceSettings;
import com.example.m1kes.parkingdemo.util.DBUtils;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;

import static android.content.Context.MODE_PRIVATE;

public class AppSettingsManager {


    /**
     * This is a SharedPreference Manager class used for getting device details aswell
     * as setting the terminalID assigned and the Activation State
     * */

    private static final String APP_SETTINGS_URI = "AppSettings";




    public static DeviceSettings getDeviceSettings(Context context){

        String deviceModel = getDeviceModel();
        String deviceIMEI = getDeviceIMEI(context);
        int terminalID = ShiftDataManager.getDefaultTerminalID(context);
        boolean activationStatus = DBUtils.convertIntToBool(getActivationStatus(context));

        return new DeviceSettings(deviceModel,deviceIMEI,terminalID,activationStatus);
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


    public static String getDeviceModel() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) +" "+ model;
        }
    }
    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


    public static void setActivationStatus(Context context,int activatedStatus){
        //1 true 0 false
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_SETTINGS_URI, MODE_PRIVATE).edit();
        editor.putInt("Activated",activatedStatus);
        editor.apply();
    }

    public static int getActivationStatus(Context context){

        SharedPreferences prefs = context.getSharedPreferences(APP_SETTINGS_URI, MODE_PRIVATE);
        //If no terminalID is found it will return 0
        return prefs.getInt("Activated", 0);
    }




}
