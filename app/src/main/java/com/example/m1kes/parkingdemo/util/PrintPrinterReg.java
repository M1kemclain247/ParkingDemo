package com.example.m1kes.parkingdemo.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Vibrator;
import android.widget.Toast;


import com.example.m1kes.parkingdemo.callbacks.OnPrintResponse;
import com.example.m1kes.parkingdemo.callbacks.OnPrinterConnected;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.PrinterStringMaker;
import com.example.m1kes.parkingdemo.printer.tasks.ConnectBluetoothPrinter;
import com.example.m1kes.parkingdemo.printer.tasks.GlobalPrint;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrinterAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;

import java.util.Date;

/**
 * Created by clive on 24/11/2016.
 * I put it here to keep the code manageable
 */

public class PrintPrinterReg {

    private Context context;
    public static boolean isSuccess;

    public PrintPrinterReg(Context context) {
        this.context = context;
    }


    public void printIfSuccessful(Printer p){
                isSuccess = true;
        try {
            connectPrinter(p);
        }catch (Exception e){
            System.out.println("Failed to load");
        }

    }


    OnPrintResponse onPrintResponse = new OnPrintResponse() {
        @Override
        public void onPrintComplete() {

            Toast.makeText(context,"Print Done",Toast.LENGTH_LONG).show();
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            BluetoothAdapter.getDefaultAdapter().disable();
        }

        @Override
        public void onPrintFailed() {
            Toast.makeText(context,"Print Failed",Toast.LENGTH_SHORT).show();
        }
    };








    private void connectPrinter(Printer printer){
        new ConnectBluetoothPrinter(context,printer,onPrinterConnected).execute();
    }

    OnPrinterConnected onPrinterConnected = new OnPrinterConnected() {
        @Override
        public void onFailedConnect() {
            Toast.makeText(context,"Failed to Connect to Printer",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnected(String printerName, String printerAddress) {

            String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
            PrinterJob printerJob = new PrinterJob();
            printerJob.setPrintAttemptTime(new Date());//id
            printerJob.setShiftId(shiftID);
            PrinterStringMaker maker = new PrinterStringMaker(context);
            printerJob.setPrintData(maker.getReceipt());
            printerJob.setPrinterModel(printerName);
            printerJob.setPrinterIMEI(printerAddress);

            //Tries to see if it is one of the allowed devices and then adds it to the PrinterJob details
            String printerCode =  PrinterAdapter.getPrinterDepCode(printerAddress,context);
            if(printerCode!=null){
                printerJob.setPrinterCode(printerCode);
            }else{
                printerJob.setPrinterCode("Unknown Printer");
            }

            System.out.println("About to Start Print: "+printerJob.toString());

            new GlobalPrint(context,onPrintResponse,printerJob).execute();


        }
    };



}
