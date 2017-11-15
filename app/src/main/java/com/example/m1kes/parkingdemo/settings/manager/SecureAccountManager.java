package com.example.m1kes.parkingdemo.settings.manager;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.m1kes.parkingdemo.settings.models.Account;

import static android.content.Context.MODE_PRIVATE;

public class SecureAccountManager {

    private static final String SECURE_PREFS = "SecureData";

    public static void setFingerPrintSupported(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SECURE_PREFS, MODE_PRIVATE).edit();
        editor.putBoolean("FingerPrintSupport",true);
        editor.apply();
    }

    public static boolean getFingerPrintSupported(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SECURE_PREFS, MODE_PRIVATE);
        return prefs.getBoolean("FingerPrintSupport", false);
    }



    public static void setAccountLinked(Account account,Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SECURE_PREFS, MODE_PRIVATE).edit();
        System.out.println("Setting Account Linked: "+account);
        editor.putString("username",account.getUsername());
        editor.putString("password",account.getPassword());
        editor.apply();
    }

    public static Account getAccountLinked(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SECURE_PREFS, MODE_PRIVATE);
        String username = prefs.getString("username",null);
        String password = prefs.getString("password",null);
        if(username!=null&&password!=null){
            return new Account(username,password);
        }else{
            return null;
        }
    }

}
