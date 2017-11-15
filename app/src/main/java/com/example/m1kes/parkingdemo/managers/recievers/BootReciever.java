package com.example.m1kes.parkingdemo.managers.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.m1kes.parkingdemo.firsttimeterminalreg.WelcomeActivity;
import com.example.m1kes.parkingdemo.settings.manager.AppSettingsManager;


public class BootReciever extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent myIntent = new Intent(context, WelcomeActivity.class);
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(myIntent);
        }
    }
}
