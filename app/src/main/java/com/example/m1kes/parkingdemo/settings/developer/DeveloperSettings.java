package com.example.m1kes.parkingdemo.settings.developer;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.m1kes.parkingdemo.R;

import static android.content.Context.MODE_PRIVATE;

public class DeveloperSettings {

    public static final String DEVELOPER_SETTINGS = "DeveloperSettings";

    public static void setDeveloperModeEnabled(boolean enabled,Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(DEVELOPER_SETTINGS, MODE_PRIVATE).edit();
        editor.putBoolean("Devmode",enabled);
        editor.apply();
    }

    public static boolean isDeveloperModeEnabled(Context context){
        SharedPreferences prefs = context.getSharedPreferences(DEVELOPER_SETTINGS, MODE_PRIVATE);
        return prefs.getBoolean("Devmode", false);
    }


}
