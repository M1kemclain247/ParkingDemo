package com.example.m1kes.parkingdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.adapters.recyclerviews.LoggedInAdapter;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.decoration.DividerItemDecoration;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.tasks.VehiclesLoggedPrintTask;
import com.example.m1kes.parkingdemo.util.StartupUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CheckVehiclesLoggedIn extends AppCompatActivity {

    RecyclerView recyclerView;
    LoggedInAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    private List<Transaction> transactions;
    private Context context;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;
    private Thread t = null;
    private Runnable runnable = null;
    private boolean refreshInfo = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check_vehicles_logged_in);
        context = CheckVehiclesLoggedIn.this;
        StartupUtils.setupActionBar("Logged In Vehicles",this);
        recyclerView = (RecyclerView) findViewById(R.id.Check_loggedVehicles_RecyclerView);
        addData();
        setupRecyclerView();

        setupCountDownTimer();


    }

    private void setupCountDownTimer(){

        t  = new Thread(){
            @Override
            public void run() {
                try {

                    while (refreshInfo){
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                refreshInformation();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();


    }

    @Override
    protected void onPause() {
        refreshInfo = false;
        System.out.println("Stop Thread = "+ refreshInfo);
        super.onPause();
    }



    private void addData(){
        transactions = new ArrayList<>();
        transactions = TransactionAdapter.getAllLoginTransactions(context);
    }

    private void setupRecyclerView(){
        adapter = new LoggedInAdapter(this,transactions);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
    }





    private void refreshInformation(){

        if(transactions!=null){
            transactions = new ArrayList<>();
            addData();
            adapter.notifyDataSetChanged();
        }
    }




    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,MainActivity.class));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logged_vehicles, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this,MainActivity.class));
            return true;
        }else if(id == R.id.action_print_list_vh) {


            if(!TransactionAdapter.getAllLoginTransactions(context).isEmpty()){
                VehiclesLoggedPrintTask  task = new VehiclesLoggedPrintTask(context);
                task.execute();
            }else{
                Toast.makeText(context,"No Logged In Vehicles!",Toast.LENGTH_LONG).show();
            }

            return true;
        } else {
            return false;
        }
    }

    private void setupExpiryCheck(){


        List<Transaction> expiringVehicles =  TransactionAdapter.checkIfExpiredNotifyIfNotNotified(context);

        if(expiringVehicles.size()>0){


            Transaction transaction = transactions.get(0);

            Intent resultIntent = new Intent(context, CheckVehiclesLoggedIn.class);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // the addAction re-use the same intent to keep the example short
            Notification n  = new Notification.Builder(this)
                    .setContentTitle("Auto logging")
                    .setContentText("Vehicle: "+transaction.getVehicleregNumber()+" Logged Out")
                    .setSmallIcon(R.drawable.ic_car_white_36dp)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            n.defaults |= Notification.DEFAULT_SOUND;
            n.defaults |= Notification.DEFAULT_VIBRATE;

            notificationManager.notify(0, n);

            //Set as notified
            transaction.setIs_Notified(true);
            TransactionAdapter.setNotified(transaction,context);

        }





    }
}
