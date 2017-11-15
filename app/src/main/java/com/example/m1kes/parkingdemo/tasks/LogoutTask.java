package com.example.m1kes.parkingdemo.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.LoginScreen;
import com.example.m1kes.parkingdemo.SplashScreen;
import com.example.m1kes.parkingdemo.models.User;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;


public class LogoutTask extends AsyncTask<String,Void,Void> {


    private ProgressDialog dialog;
    private Context context;
    private Handler mHandler;

    public LogoutTask(Context context){
        this.context = context;
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        mHandler = new Handler();

    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(String... params) {


        mHandler.post(new Runnable() {
            @Override
            public void run() {
                dialog.setMessage("Logging Out....");
                dialog.show();
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


       String username = UserAdapter.getLoggedInUser(context);

        if(username!=null){
            boolean userLoggedOut =  UserAdapter.logoutUser(username,context);
            if(userLoggedOut){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        context.startActivity(new Intent(context, LoginScreen.class));
                        Toast.makeText(context,"Logged Out!",Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(context,"Failed to Logout!",Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }else{
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    context.startActivity(new Intent(context, LoginScreen.class));
                    Toast.makeText(context,"Logged Out!",Toast.LENGTH_SHORT).show();
                }
            });
        }









        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }
}
