package com.example.m1kes.parkingdemo.firsttimeterminalreg;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by clive on 01/12/2016.
 * Checks if its the first time
 */

public class PrefManager {


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "androidhive-welcome";

    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        System.out.println("Opening The Shared Pref for editing...");
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        System.out.println("Setting first time launch: "+isFirstTime);
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        System.out.println("Checking if its the first time: "+pref.getBoolean(IS_FIRST_TIME_LAUNCH,true));
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

}
