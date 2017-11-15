package com.example.m1kes.parkingdemo.modules.supervisor.util;


import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SupervisorDataManager {


    private static final String SUPERVISOR_PREFS = "SupervisorData";

    public static void setActiveSupervisor(String username,Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SUPERVISOR_PREFS, MODE_PRIVATE).edit();
        editor.putString("Supervisor-name",username);
        editor.apply();
    }

    public static String getActiveSupervisor(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SUPERVISOR_PREFS, MODE_PRIVATE);
        return prefs.getString("Supervisor-name", null);
    }

}
