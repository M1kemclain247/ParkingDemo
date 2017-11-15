package com.example.m1kes.parkingdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.tasks.LogoutTask;
import com.example.m1kes.parkingdemo.util.GeneralUtils;
import com.example.m1kes.parkingdemo.util.StartupUtils;

public class ShiftChecker extends AppCompatActivity {


    private TextView txtVersionNo;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_shift_checker);
        StartupUtils.setupActionBar("Shift Already Created",this);
        context = ShiftChecker.this;
        txtVersionNo = (TextView)findViewById(R.id.txtVersionNo);
        setupVersionSigning();




    }

    @Override
    public void onBackPressed() {
        showConfirmLogoutAction();

    }

    private void showConfirmLogoutAction() {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Confirm Logout");
        alertDialog.setMessage("Are you sure you want to logout?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Logout",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new LogoutTask(context).execute();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    private void setupVersionSigning(){

        txtVersionNo = (TextView)findViewById(R.id.txtVersionNo);
        String version =  GeneralUtils.getVersionNumber(context);
        if(version!=null){
            txtVersionNo.setText("Version "+version);
        }else{
            txtVersionNo.setText("Unknown Version");
        }
    }

}
