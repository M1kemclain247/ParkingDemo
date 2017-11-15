package com.example.m1kes.parkingdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.m1kes.parkingdemo.util.StartupUtils;

public class VehicleLog extends AppCompatActivity {

    EditText registrationNumber,entryTime,bayNumber,finePayment,feePayment,tender,amount,change;
    Button LoginVehicleOK,LoginVehicleCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vehicle_login_2);
        StartupUtils.setupTitleOnlyActionBar("Login Vehicle",this);
        initGui();


    }

    private void initGui(){

        registrationNumber = (EditText) findViewById(R.id.vehicleLogin_2_txtRegistration);//Registration editText
        entryTime = (EditText) findViewById(R.id.txtEntryTime);// entry time editText
        bayNumber = (EditText) findViewById(R.id.txtBayNumber);// BAY Number editText
        finePayment = (EditText) findViewById(R.id.txtFinePayment);// fine payment editText
        feePayment = (EditText) findViewById(R.id.txtFeePayment);// fee payment editText
        tender = (EditText) findViewById(R.id.txtTender);// tender editText
        amount = (EditText) findViewById(R.id.txtAmountDue);// amount editText
        change = (EditText) findViewById(R.id.txtChnge);// change editText

        LoginVehicleOK = (Button)findViewById(R.id.LoginVehicleOK);
        LoginVehicleCancel = (Button)findViewById(R.id.LoginVehicleCancel);
        LoginVehicleOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        LoginVehicleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VehicleLog.this,VehicleLogin.class));
            }
        });

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,VehicleLogin.class));
    }
}
