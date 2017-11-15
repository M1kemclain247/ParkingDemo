package com.example.m1kes.parkingdemo.printer;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.LoginScreen;
import com.example.m1kes.parkingdemo.MainActivity;
import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.ShiftManager;
import com.example.m1kes.parkingdemo.ViewPrinterJobs;
import com.example.m1kes.parkingdemo.adapters.listviews.PrinterListAdapter;
import com.example.m1kes.parkingdemo.callbacks.OnPrintResponse;
import com.example.m1kes.parkingdemo.callbacks.OnPrinterClicked;
import com.example.m1kes.parkingdemo.callbacks.OnPrinterConnected;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.modules.supervisor.AdvancedMainActivity;
import com.example.m1kes.parkingdemo.modules.supervisor.util.SupervisorDataManager;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.printer.tasks.ConnectBluetoothPrinter;
import com.example.m1kes.parkingdemo.printer.tasks.GlobalPrint;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrinterAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.transactions.models.CashupTransaction;
import com.example.m1kes.parkingdemo.util.ToFile;

import java.io.IOException;
import java.util.List;

import HPRTAndroidSDK.HPRTPrinterHelper;

import static com.example.m1kes.parkingdemo.util.ObjectParser.*;

public class PrinterDevices extends AppCompatActivity {

    private Context context;
    private ListView availablePrintersListView;
    private  List<Printer> printers = null;
    private Button btnConnectOthers,btnCancelConnect;

    public static final int BLUETOOTH_CONNECTED = 500;
    public static final int BLUETOOTH_FAILED_CONNECT = 404;
    private  PrinterJob printerJob;
    private int printFailCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.activity_printer_devices);
        setResult(Activity.RESULT_CANCELED);
        context = PrinterDevices.this;
        this.setFinishOnTouchOutside(false);
        getPrintJobInfo();




        availablePrintersListView = (ListView)findViewById(R.id.availablePrintersListView);
        btnConnectOthers = (Button)findViewById(R.id.btnConnectOthers);
        btnCancelConnect = (Button)findViewById(R.id.btnCancelConnect);

        btnCancelConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelDialog();
            }
        });

        btnConnectOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWarningDialog();
            }
        });

        printers = PrinterAdapter.getAllPrinters(context);
        PrinterListAdapter adapter = new PrinterListAdapter(this,R.layout.printer_row,printers,onPrinterClicked);
        availablePrintersListView.setAdapter(adapter);
        if(BluetoothAdapter.getDefaultAdapter()!=null&&!BluetoothAdapter.getDefaultAdapter().isEnabled()){
            BluetoothAdapter.getDefaultAdapter().enable();
        }

        List<Printer> pairedPrinters = PrinterAdapter.getPairedPrinters(context);
        if(pairedPrinters!=null&&pairedPrinters.size()>0){
            //get the first paired printer and attempt to print to it
            Printer printer =  pairedPrinters.get(0);

            System.out.println("Going to Print with Printer: "+printer);

            printerJob.setPrinterModel(printer.getPrinterModel());
            printerJob.setPrinterIMEI(printer.getPrinterIMEI());
            //Tries to see if it is one of the allowed devices and then adds it to the PrinterJob details
            String printerCode =  PrinterAdapter.getPrinterDepCode(printer.getPrinterIMEI(),context);

            if(printerCode!=null){
                printerJob.setPrinterCode(printerCode);
            }else{
                printerJob.setPrinterCode("Unknown Printer");
            }

            System.out.println("About to Start Print: "+printerJob.toString());

            connectPrinter(printer);


        }

    }


    private void showCancelDialog(){

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Continue");
        alertDialog.setMessage("Are you sure u want to cancel?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(printerJob.getPrintType().equalsIgnoreCase(Receipt.TYPE_CASHUP)){
                            Toast.makeText(context,"Cashup Printout!",Toast.LENGTH_SHORT).show();
                            //delete all local transactions
                            TransactionAdapter.deleteAll(context);
                            context.startActivity(new Intent(context, ShiftManager.class));
                            Toast.makeText(context,"You have successfully cashed up Print Receipt Later!",Toast.LENGTH_SHORT).show();
                            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 500 milliseconds
                            BluetoothAdapter.getDefaultAdapter().disable();
                            v.vibrate(200);
                            finish();
                        }else if(printerJob.getPrintType().equalsIgnoreCase(Receipt.TYPE_REPRINT)){

                            Toast.makeText(context,"Cancelled!",Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            BluetoothAdapter.getDefaultAdapter().disable();
                            startActivity(new Intent(context,MainActivity.class));
                        }
                    }
                });
        alertDialog.show();


    }




    private void showWarningDialog(){

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("Only Use this option if there is an error with your Allowed Devices");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Continue",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        /*Intent serverIntent = new Intent(context, Activity_DeviceList.class);
                        startActivityForResult(serverIntent, HPRTPrinterHelper.ACTIVITY_CONNECT_BT);*/
                        Intent scanIntent = new Intent(context, ScanDeviceList.class);
                        startActivityForResult(scanIntent, HPRTPrinterHelper.ACTIVITY_CONNECT_BT);
                    }
                });
        alertDialog.show();


    }

    private boolean getPrintJobInfo(){
        boolean isNotNull = false;
        Intent i = getIntent();
        if(i!=null) {
            Bundle b = i.getExtras();
            if (b!=null) {
                PrinterJob p = null;
                try {
                    p =  (PrinterJob)bytes2Object(b.getByteArray("printerJob"));
                    if(p!=null){
                        this.printerJob = p;
                        isNotNull = true;
                        System.out.println("Successfully got the printerJob into Bluetooth Devices");
                    }else{
                        System.out.println("Failed to get the printerJob into Bluetooth Devices");
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                    isNotNull = false;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    isNotNull = false;
                }catch (Exception e){
                    isNotNull = false;
                }

            }
        }
        return isNotNull;
    }


    OnPrinterConnected onPrinterConnected = new OnPrinterConnected() {
        @Override
        public void onFailedConnect() {
            printFailCount++;
            if(printFailCount==2){
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Failed to Connect to printer");
                alertDialog.setMessage("You may continue with your shift and reprint the ticket when a printer is available");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Continue",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(context,MainActivity.class));
                                Toast.makeText(context,"Print Failed Try again later",Toast.LENGTH_SHORT).show();
                            }
                        });
                alertDialog.show();
            }else{
                Toast.makeText(context,"Failed to Connect to Printer",Toast.LENGTH_SHORT).show();
            }



        }

        @Override
        public void onConnected(String printerName, String printerAddress) {


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

    OnPrintResponse onPrintResponse = new OnPrintResponse() {
        @Override
        public void onPrintComplete() {

            if(printerJob.getPrintType().equalsIgnoreCase(Receipt.TYPE_CASHUP)){
                Toast.makeText(context,"Cashup Printout!",Toast.LENGTH_SHORT).show();
                //delete all local transactions
                ToFile.writeToFile(printerJob.getPrintData(),context);

                context.startActivity(new Intent(context, LoginScreen.class));
                Toast.makeText(context,"You have successfully cashed up!",Toast.LENGTH_SHORT).show();
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                BluetoothAdapter.getDefaultAdapter().disable();
                v.vibrate(200);
                finish();

            }else if(printerJob.getPrintType().equalsIgnoreCase(Receipt.TYPE_SPOT_CHECK)){
                Toast.makeText(context,"SpotCheck Printed!",Toast.LENGTH_SHORT).show();
                //delete all local transactions
                context.startActivity(new Intent(context, AdvancedMainActivity.class));
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                BluetoothAdapter.getDefaultAdapter().disable();
                v.vibrate(200);
                finish();
            }else{
                Toast.makeText(context,"Successfully Printed Receipt",Toast.LENGTH_SHORT).show();
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                v.vibrate(500);

                BluetoothAdapter.getDefaultAdapter().disable();
                String username = UserAdapter.getLoggedInUser(context);

                if(username!=null){
                    Intent i = new Intent(context,MainActivity.class);
                    startActivity(i);
                }else{
                    Intent i = new Intent(context,LoginScreen.class);
                    startActivity(i);
                }



            }

        }

        @Override
        public void onPrintFailed() {
            Toast.makeText(context,"Failed to Print Receipt",Toast.LENGTH_SHORT).show();
            if(printerJob.getPrintType().equalsIgnoreCase(Receipt.TYPE_CASHUP)){
                //delete all local transactions

                String username = UserAdapter.getLoggedInUser(context);
                UserAdapter.logoutUser(username,context);
                context.startActivity(new Intent(context, LoginScreen.class));
                Toast.makeText(context,"You have successfully cashed up can print later!",Toast.LENGTH_SHORT).show();
                Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                BluetoothAdapter.getDefaultAdapter().disable();
                v.vibrate(200);
                finish();
            }
        }
    };

    OnPrinterClicked onPrinterClicked = new OnPrinterClicked() {
        @Override
        public void onClicked(int position) {
            //This is where we will attempt to connect to the printe
            Printer printer =  printers.get(position);
            connectPrinter(printer);
        }
    };


    private void connectPrinter(Printer printer){
        new ConnectBluetoothPrinter(context,printer,onPrinterConnected).execute();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            String strIsConnected;
            System.out.println("In Activity Result now");
            switch (resultCode) {
                case Activity_DeviceList.ZEBRA_CONNECT_RESULT:
                    System.out.println("inside Zebra activity result");
                    String strtempBTAddress = "";
                    String strModel = "";
                    strIsConnected = data.getExtras().getString("is_connected");
                    if (strIsConnected.equals("NO")) {
                        return;
                    } else {

                        strtempBTAddress = data.getExtras().getString("BTAddress");
                        strModel = data.getExtras().getString("PrinterModel");
                        System.out.println("Got printer Details: Model: "+strModel + "IMEI:"+strtempBTAddress);
                        if (strtempBTAddress == null || !strtempBTAddress.contains(":") || strtempBTAddress.length() != 17)
                            return;
                        System.out.println("Connecting to printer : " + strtempBTAddress);
                        Printer printer = new Printer();
                        printer.setPrinterModel(strModel);
                        printer.setPrinterIMEI(strtempBTAddress);
                        connectPrinter(printer);
                    }
                    break;

                case HPRTPrinterHelper.ACTIVITY_CONNECT_BT:
                    String strBTAddress = "";
                    String strTempModel = "";
                    strIsConnected = data.getExtras().getString("is_connected");
                    if (strIsConnected.equals("NO")) {
                        return;
                    } else {
                        strBTAddress = data.getExtras().getString("BTAddress");
                        strTempModel = data.getExtras().getString("PrinterModel");
                        if (strBTAddress == null || !strBTAddress.contains(":") || strBTAddress.length() != 17)
                            return;
                        System.out.println("Connecting to printer : "+strBTAddress);
                        Printer printer = new Printer();
                        printer.setPrinterModel(strTempModel);
                        printer.setPrinterIMEI(strBTAddress);
                        connectPrinter(printer);
                        }
                    }

        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onActivityResult ")).append(e.getMessage()).toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}
