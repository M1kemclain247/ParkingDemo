package com.example.m1kes.parkingdemo.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.SyncTransactions;
import com.example.m1kes.parkingdemo.base.ShiftSyncManager;
import com.example.m1kes.parkingdemo.base.callbacks.OnSyncAllShiftsResponse;


public class SyncAllShifts extends AsyncTask<Void,Void,Void> {

    private Context context;
    private Handler mHandler;
    private OnSyncAllShiftsResponse response;

    public SyncAllShifts(Context context,OnSyncAllShiftsResponse response){
        this.context = context;
        mHandler = new Handler();
        this.response = response;
    }



    @Override
    protected Void doInBackground(Void... params) {

        System.out.println("Attempting to sync all Shifts");

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,"Trying to Sync all Shifts",Toast.LENGTH_SHORT).show();
            }
        });

        //This may be heavy operation
        ShiftSyncManager syncManager = new ShiftSyncManager(context);
        boolean worked =  syncManager.startSyncing();
        
        if(worked){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Should have completely synced all shifts and Transactions");
                    response.onSyncSuccessful();
                }
            });
        }else{
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Failed to completely sync all shifts and Transactions");
                    response.onSyncFailed();
                }
            });
        }







        return null;
    }
}
