package com.example.m1kes.parkingdemo.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;
import com.example.m1kes.parkingdemo.base.callbacks.OnSyncAllShiftsResponse;
import com.example.m1kes.parkingdemo.tasks.SyncAllShifts;
import java.util.Timer;
import java.util.TimerTask;

public class AlarmReceiver extends BroadcastReceiver {
    private Context context;
    private int failedCount = 0;
    OnSyncAllShiftsResponse onSyncAllShiftsResponse = new OnSyncAllShiftsResponse() {
        public void onSyncSuccessful() {
            AlarmReceiver.this.failedCount = 0;
            Toast.makeText(AlarmReceiver.this.context, "Shifts Synced!", Toast.LENGTH_SHORT).show();
        }

        public void onSyncFailed() {
            AlarmReceiver.this.failedCount = AlarmReceiver.this.failedCount + 1;
            Toast.makeText(AlarmReceiver.this.context, "Failed Syncing shifts!",Toast.LENGTH_SHORT).show();
            if (AlarmReceiver.this.failedCount < 10) {
                new Timer().scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        try {
                            new SyncAllShifts(AlarmReceiver.this.context, AlarmReceiver.this.onSyncAllShiftsResponse).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
                        } catch (Exception e) {
                        }
                    }
                }, 0, 4000);
            }
        }
    };

    public void onReceive(Context context, Intent intent) {
        this.failedCount = 0;
        this.context = context;
        new SyncAllShifts(context, this.onSyncAllShiftsResponse).execute(new Void[0]);
    }
}
