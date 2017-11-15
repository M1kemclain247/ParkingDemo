package com.example.m1kes.parkingdemo.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.example.m1kes.parkingdemo.VehicleLogin;
import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.models.TransactionType;
import com.example.m1kes.parkingdemo.printer.PrinterDevices;
import com.example.m1kes.parkingdemo.printer.models.LoggedVehicles;
import com.example.m1kes.parkingdemo.printer.models.MakePaymentReceipt;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.printer.models.Vehicle;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrecinctAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrintJobAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.util.ObjectParser;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;

import org.w3c.dom.ls.LSException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class VehiclesLoggedPrintTask extends AsyncTask<Void,Void,Void> {

    private Context context;
    private Handler mHandler;


    public VehiclesLoggedPrintTask(Context context){
        mHandler = new Handler();
        this.context = context;
    }



    @Override
    protected Void doInBackground(Void ...params) {


        List<Vehicle> loggedVehicleTransactions = TransactionAdapter.getAllLoginPrintoutTransactions(context);
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        PrinterJob printerJob = new PrinterJob();
        Receipt receipt =  getPaymentReceipt(loggedVehicleTransactions);
        printerJob.setPrintAttemptTime(new Date());//id
        printerJob.setShiftId(shiftID);
        printerJob.setPrintData(receipt.getPrintString());
        printerJob.setPrintType(receipt.getPrintTypeAll());
        printerJob.setPrintStatus(PrinterJob.STATUS_PENDING);
        PrintJobAdapter.addPrintJob(printerJob,context);

        final Intent i = new Intent(context,PrinterDevices.class);
        Bundle b = new Bundle();
        try {
            b.putByteArray("printerJob", ObjectParser.object2Bytes(printerJob));
        } catch (IOException e) {
            e.printStackTrace();
        }
        i.putExtras(b);

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                context.startActivity(i);
            }
        });

        return null;
    }



    private LoggedVehicles getPaymentReceipt(List<Vehicle> loggedVehicles){
        return new LoggedVehicles(loggedVehicles);
    }



}
