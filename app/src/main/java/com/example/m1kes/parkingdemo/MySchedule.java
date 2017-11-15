package com.example.m1kes.parkingdemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.WindowManager;

import com.example.m1kes.parkingdemo.adapters.recyclerviews.MyScheduleAdapter;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.PendingJobsAdapter;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.decoration.DividerItemDecoration;
import com.example.m1kes.parkingdemo.models.Schedule;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.util.StartupUtils;

import java.util.ArrayList;
import java.util.List;

public class  MySchedule extends AppCompatActivity {

    RecyclerView recyclerView;
    MyScheduleAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<Object> allData;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_schedule);
        context = MySchedule.this;
        StartupUtils.setupActionBar("My Schedule",this);

        recyclerView = (RecyclerView)findViewById(R.id.recyclerMySchedule);
        populateMainList();
        setupRecyclerView(allData);




    }



    private void populateMainList() {

        allData = new ArrayList<>();
        allData.add("Monday");
        allData.add(new Schedule("1.00 pm","Zone 1","CHICKEN INN",50.00));
        allData.add("Tuesday");
        allData.add(new Schedule("1.00 pm","Zone 19","CBZ WEST-Clamp",80.00));
        allData.add("Wednesday");
        allData.add(new Schedule("1.00 pm","Zone 1","DHL-Clamp",50.00));
        allData.add("Thursday");
        allData.add(new Schedule("1.00 pm","Zone 12","NSSA NORTH (CHURCH SIDE)",60.00));
        allData.add("Friday");
        allData.add(new Schedule("1.00 pm","Zone 1","ABERCORN STREET",10.00));
        allData.add("Saturday");
        allData.add(new Schedule("1.00 pm","Zone 3","PM MOTORS",47.00));
    }

    private void setupRecyclerView(List<Object> allData){
        adapter = new MyScheduleAdapter(allData);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context));
    }


    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            this.finish();
            return true;
        }

        return false;
    }

}
