package com.example.m1kes.parkingdemo.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.format.DateUtils;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.MainActivity;
import com.example.m1kes.parkingdemo.ShiftManager;
import com.example.m1kes.parkingdemo.callbacks.LoginUserResponse;
import com.example.m1kes.parkingdemo.models.ShiftUserData;
import com.example.m1kes.parkingdemo.models.User;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserShiftDataAdapter;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;

import java.util.Calendar;
import java.util.List;


public class LoginUserTask extends AsyncTask<String,Void,Boolean> {

    private Context context;
    private Handler mHandler;
    private LoginUserResponse response;


    public LoginUserTask(Context context, LoginUserResponse response){
        this.context = context;
        mHandler = new Handler();
        this.response = response;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected Boolean doInBackground(String... params) {

        final String username = params[0];
        String password = params[1];

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        System.out.println("Trying to login user!");

        boolean authenticated = UserAdapter.loginUser(new User(username,password),context);
        boolean isAlreadyCreated = false;

        if(authenticated) {
            isAlreadyCreated = checkIfShiftOpenedToday(username);
        }



        if(authenticated){
            if(isAlreadyCreated){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        response.onLoginSameDay();
                    }
                });
            }else{
                addNewDataEntry(username);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        response.onLoginSuccess(username);
                    }
                });
            }

        }else{
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    response.onLoginFailed();
                }
            });
        }

        return authenticated;

    }



    @Override
    protected void onPostExecute(Boolean authenticated) {
        super.onPostExecute(authenticated);

    }


    private boolean checkIfShiftOpenedToday(String username){

        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(System.currentTimeMillis());
        Calendar cal2 = Calendar.getInstance();

        List<ShiftUserData> dataList =  UserShiftDataAdapter.getData(context);
        for(ShiftUserData data : dataList){
            if(data.getUsername().equalsIgnoreCase(username)){
                if(DateUtils.isToday(data.getDate())){
                    return true;
                }
            }
        }

        return false;
    }

    private void addNewDataEntry(String username){
        ShiftUserData data = new ShiftUserData();
        data.setUsername(username);
        data.setDate(System.currentTimeMillis());
        UserShiftDataAdapter.putData(data,context);
    }
}
