package com.example.m1kes.parkingdemo.printer.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;

import com.example.m1kes.parkingdemo.MainActivity;
import com.example.m1kes.parkingdemo.callbacks.BluetoothEnabledResponse;
import com.example.m1kes.parkingdemo.callbacks.OnPrinterConnected;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.printer.PublicAction;
import com.example.m1kes.parkingdemo.util.DialogUtils;

import HPRTAndroidSDK.HPRTPrinterHelper;
import HPRTAndroidSDK.PublicFunction;


public class ConnectBluetoothPrinter extends AsyncTask<Void,Void,Boolean> {

    private Context context;
    private Handler mHandler;
    private ProgressDialog dialog;
    //Printer HELPER items
    private static HPRTPrinterHelper HPRTPrinter = new HPRTPrinterHelper();
    private PublicFunction PFun=null;
    private PublicAction PAct=null;
    private Printer printer;
    //Call Back
    private OnPrinterConnected response;


    public ConnectBluetoothPrinter(Context context, Printer printer, OnPrinterConnected response){
        this.context = context;
        mHandler = new Handler();
        PFun=new PublicFunction(context);
        PAct=new PublicAction(context);
        this.printer = printer;
        this.response = response;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Connecting to Printer....");
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        boolean isConnected = false;
        boolean isZebraPrinter = false;



        if(hasBluetooth()){
            if(enableBluetooth()){


                if(printer.getPrinterModel().contains("MZ220")){
                    System.out.println("Trying to connect to Zebra Printer...");
                    isConnected = true;
                    isZebraPrinter = true;
                }else {

                    ///Close any open ports
                    HPRTPrinter = new HPRTPrinterHelper(context, printer.getPrinterModel());
                    HPRTPrinterHelper.PortClose();

                    System.out.println("Trying to connect to MPT-II Printer...");

                    System.out.println("Printer Details :  MODEL: "+printer.getPrinterModel() + " IMEI: "+printer.getPrinterIMEI());
                    if (HPRTPrinterHelper.PortOpen("Bluetooth," + printer.getPrinterIMEI()) != 0) {
                        //Failed Connect


                    } else {
                        //Successfully connected!
                        isConnected = true;
                    }

                }
            }
        }else{
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    DialogUtils.showDismissDialogMessage("Bluetooth Error","Your Device doesnt support Bluetooth",context);
                }
            });
        }

        if(isConnected&&isZebraPrinter){

        }



        return isConnected;
    }


    @Override
    protected void onPostExecute(Boolean connected) {

        if(connected){
            dialog.dismiss();
            response.onConnected(printer.getPrinterModel(),printer.getPrinterIMEI());
        }else {
            dialog.dismiss();
            response.onFailedConnect();
        }

    }

    private boolean hasBluetooth(){

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if(adapter!=null)
            return true;
        else
            return false;
    }

    private Boolean enableBluetooth(){

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        Boolean isEnabled = false;
        if(!adapter.isEnabled()){
            try {
                //Enable Bluetooth
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setMessage("Connecting to Printer....");
                    }
                });
                adapter.enable();
                isEnabled = true;
            }catch (Exception e){
                e.printStackTrace();
                isEnabled = false;
            }
        }else{
            isEnabled = true;
        }
        return isEnabled;
    }


}
