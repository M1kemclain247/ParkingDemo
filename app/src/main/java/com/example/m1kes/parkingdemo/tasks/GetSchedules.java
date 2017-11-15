package com.example.m1kes.parkingdemo.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.MySchedule;


public class GetSchedules extends AsyncTask<String,Void,Void> {

    private Context context;
    private Handler mHandler;
    private ProgressDialog dialog;

    public GetSchedules(Context context){
        this.context = context;
        mHandler = new Handler();

    }

    @Override
    protected Void doInBackground(String... params) {


        mHandler.post(new Runnable() {
            @Override
            public void run() {
                dialog = new ProgressDialog(context);
                dialog.setCancelable(false);
                dialog.setMessage("Downloading your Weekly Schedule....");
                dialog.show();
            }
        });

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //Download resources make the Request:


        boolean downloaded  = true;

        if(downloaded){

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage("Downloaded Successfully");
                    dialog.dismiss();
                    Intent i = new Intent(context, MySchedule.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                    context.startActivity(i);
                }
            });
        }else{

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage("Failed to Download");
                    dialog.dismiss();
                    Toast.makeText(context,"Failed to download, Please try again!",Toast.LENGTH_LONG).show();
                }
            });

        }




        return null;
    }
}
