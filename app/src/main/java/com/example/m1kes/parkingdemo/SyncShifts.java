package com.example.m1kes.parkingdemo;

import android.content.Context;
import android.content.Intent;
import android.os.ConditionVariable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.adapters.recyclerviews.AllTransactionsAdapter;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.SyncShiftsAdapter;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.decoration.DividerItemDecoration;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnShiftItemSelected;
import com.example.m1kes.parkingdemo.base.callbacks.OnSyncAllShiftsResponse;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.settings.activities.DeviceSettingsManager;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.tasks.SyncAllShifts;
import com.example.m1kes.parkingdemo.util.StartupUtils;
import com.example.m1kes.parkingdemo.util.SyncUtils;

import java.util.List;

public class SyncShifts extends AppCompatActivity {

    private Context context;
    private Button btnSyncShifts;
    private RecyclerView recyclerShifts;
    private SyncShiftsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Shift> shifts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sync_shifts);
        context = SyncShifts.this;
        StartupUtils.setupActionBar("Sync Shifts",this);
        initGui();
        setupOnClicks();
        setupRecyclerView(ShiftsAdapter.getAllLocalShifts(context));



    }

    private void initGui(){
        btnSyncShifts = (Button)findViewById(R.id.btnSyncShifts);
        recyclerShifts = (RecyclerView)findViewById(R.id.recyclerShifts);

    }

    private void setupOnClicks(){

        btnSyncShifts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SyncAllShifts(context,onSyncAllShiftsResponse).execute();
            }
        });

    }


    OnSyncAllShiftsResponse onSyncAllShiftsResponse = new OnSyncAllShiftsResponse() {
        @Override
        public void onSyncSuccessful() {
            Toast.makeText(context,"Shifts Synced!",Toast.LENGTH_SHORT).show();
            updateList();
        }

        @Override
        public void onSyncFailed() {
            Toast.makeText(context,"Failed Syncing shifts!",Toast.LENGTH_SHORT).show();
            updateList();
        }
    };

    OnShiftItemSelected onShiftItemSelected = new OnShiftItemSelected() {
        @Override
        public void onClicked(Shift shift) {

        }
    };


    private void setupRecyclerView(List<Shift> shifts){
        adapter = new SyncShiftsAdapter(context,shifts,onShiftItemSelected);
        layoutManager = new LinearLayoutManager(context);
        recyclerShifts.setLayoutManager(layoutManager);
        recyclerShifts.setHasFixedSize(true);
        recyclerShifts.addItemDecoration(new DividerItemDecoration(context));
        recyclerShifts.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        this.shifts = shifts;
    }

    private void updateList(){
        adapter = new SyncShiftsAdapter(context, ShiftsAdapter.getAllLocalShifts(context),
                onShiftItemSelected);
        recyclerShifts.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id== android.R.id.home){
            this.finish();
            startActivity(new Intent(this,LoginScreen.class));
            return true;
        }

        return false;
    }


}
