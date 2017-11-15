package com.example.m1kes.parkingdemo.util;


import android.content.Context;
import android.content.SharedPreferences;

import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Shift;

import static android.content.Context.MODE_PRIVATE;

public class ShiftDataManager {


    private static final String SHIFTS_PREFS = "ShiftData";

    public static void setDefaultTerminalID(int terminalID ,Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHIFTS_PREFS, MODE_PRIVATE).edit();
        editor.putInt("TerminalID",terminalID);
        editor.apply();
    }

    public static int getDefaultTerminalID(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SHIFTS_PREFS, MODE_PRIVATE);
        return prefs.getInt("TerminalID", 1);
    }

    public static void setCurrentActiveShiftID(String shiftID, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(SHIFTS_PREFS, MODE_PRIVATE).edit();
        editor.putString("ShiftID",shiftID);
        editor.apply();
    }

    public static String getCurrentActiveShiftID(Context context){
        SharedPreferences prefs = context.getSharedPreferences(SHIFTS_PREFS, MODE_PRIVATE);
        return prefs.getString("ShiftID", null);
    }

}
