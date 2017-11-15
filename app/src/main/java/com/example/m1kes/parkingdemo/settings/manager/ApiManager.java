package com.example.m1kes.parkingdemo.settings.manager;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.settings.developer.DeveloperSettings;

import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

public class ApiManager {

    public static final String API_PREFS = "Apis";


    public static void setServerIp(String mode,Context context){

        String ipAddress = null;
            if(mode.equalsIgnoreCase("Local Server")){
                ipAddress = context.getResources().getString(R.string.localIP);
            }else if(mode.equalsIgnoreCase("Remote Server")){
                ipAddress = context.getResources().getString(R.string.remoteIP);
            } else if(mode.equalsIgnoreCase("Hosted Server")){
                ipAddress = context.getResources().getString(R.string.hostedIP);
            }else{
                ipAddress = null;
            }

        if(ipAddress!=null)
            setApiPrefs(context,ipAddress);
    }


    private static String getPreferedConnectionMode(String ip , Context context){

        String connectionMode = null;
        if(ip.equalsIgnoreCase(context.getResources().getString(R.string.localIP))){
            connectionMode = "Local Server";
        }else if(ip.equalsIgnoreCase(context.getResources().getString(R.string.remoteIP))){
            connectionMode = "Remote Server";
        } else if(ip.equalsIgnoreCase(context.getResources().getString(R.string.hostedIP))){
            connectionMode = "Hosted Server";
        }else{
            connectionMode = null;
        }
        return connectionMode;
    }

    public static String getServerType(Context context){

      return getPreferedConnectionMode(getServerIP(context),context);

    }


    public static void setApiPrefs(Context context, String api_url){

        SharedPreferences.Editor editor = context.getSharedPreferences(API_PREFS, MODE_PRIVATE).edit();
        editor.putString("CurrentServer",api_url);
        editor.apply();
    }


    /**
     * This is a helper method to return the ShiftID whenever needed.
     * **/
    public static String getServerIP(Context context){
        SharedPreferences prefs = context.getSharedPreferences(API_PREFS, MODE_PRIVATE);
        String api = prefs.getString("CurrentServer", null);


        if (api != null) {
            return api;
        }else{
            //IF there was never a pref set for any reason then just use the hosted IP by default
            return context.getResources().getString(R.string.hostedIP);
        }
    }

    public static String[] getAvailableIps(Context context){

        if(DeveloperSettings.isDeveloperModeEnabled(context)){
            String hostedIp = context.getResources().getString(R.string.dev_hostedIP);
            String localIp = context.getResources().getString(R.string.dev_localIP);
            return new String[]{hostedIp,localIp};
        }else{
            String hostedIp = context.getResources().getString(R.string.hostedIP);
            String localIp = context.getResources().getString(R.string.localIP);
            return new String[]{hostedIp,localIp};
        }
    }



}
