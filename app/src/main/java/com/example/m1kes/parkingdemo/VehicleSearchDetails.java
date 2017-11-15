package com.example.m1kes.parkingdemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.m1kes.parkingdemo.util.StartupUtils;

public class VehicleSearchDetails extends AppCompatActivity {

    private Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vehicle_search_details);
        StartupUtils.setupActionBar("ADW-5312",this);
        c = VehicleSearchDetails.this;




    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(c,SearchVehicle.class));
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id== android.R.id.home){
            this.finish();
            startActivity(new Intent(this,SearchVehicle.class));
            return true;
        }

        return false;

    }



}
