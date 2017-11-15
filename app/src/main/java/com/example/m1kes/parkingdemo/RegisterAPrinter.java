package com.example.m1kes.parkingdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.callbacks.OnPrintResponse;
import com.example.m1kes.parkingdemo.callbacks.OnPrinterConnected;
import com.example.m1kes.parkingdemo.callbacks.RegisterPrinterResponse;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.printer.Activity_DeviceList;
import com.example.m1kes.parkingdemo.printer.PrinterDevices;
import com.example.m1kes.parkingdemo.printer.ScanDeviceList;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.printer.models.RegisterPrintout;
import com.example.m1kes.parkingdemo.printer.tasks.ConnectBluetoothPrinter;
import com.example.m1kes.parkingdemo.printer.tasks.GlobalPrint;
import com.example.m1kes.parkingdemo.printer.models.PrinterStringMaker;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrintJobAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrinterAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.tasks.LogoutTask;
import com.example.m1kes.parkingdemo.tasks.PrinterRegisterTask;
import com.example.m1kes.parkingdemo.util.GeneralUtils;
import com.example.m1kes.parkingdemo.util.ObjectParser;
import com.example.m1kes.parkingdemo.util.PrintPrinterReg;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;
import com.example.m1kes.parkingdemo.util.StartupUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import HPRTAndroidSDK.HPRTPrinterHelper;

public class RegisterAPrinter extends AppCompatActivity implements View.OnClickListener {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Button connectPrinter;
    String strBTAddress = "";
    int id = 1;
    String printerAdd,printerModel;
    RegisterPrinterResponse registerPrinterResponse;
    private Handler mHandler;
    ProgressDialog progressDialog;
    private Context context;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private boolean hasActiveShift= false;
    private boolean isLoggedIn = false;
    private TextView txtVersionNo;
    private ImageView imgBluetoothIcon;
    private RadioButton radRego,radZebra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_aprinter);
        StartupUtils.setupActionBar("Setup First Printer",this);
        context = RegisterAPrinter.this;
        BluetoothAdapter.getDefaultAdapter().enable();
        checkIfHasActiveShift();

        this.radRego = (RadioButton) findViewById(R.id.radRego);
        this.radZebra = (RadioButton) findViewById(R.id.radZebra);

        connectPrinter = (Button) findViewById(R.id.btnConnectToPrinters);
        connectPrinter.setOnClickListener(this);

        setupVersionSigning();
        setupAnimations();

    }

    private void setupAnimations() {

        imgBluetoothIcon = (ImageView)findViewById(R.id.imgBluetoothIcon);

        imgBluetoothIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(context,ShiftManager.class));
                return true;
            }
        });

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(3000);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(1000);
        fadeOut.setDuration(3000);

        final AnimationSet mAnimation = new AnimationSet(false); //change to false
        mAnimation.addAnimation(fadeIn);
        mAnimation.addAnimation(fadeOut);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgBluetoothIcon.setAnimation(mAnimation);
                mAnimation.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imgBluetoothIcon.setAnimation(mAnimation);

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

    private void checkIfHasActiveShift(){
            Intent i = getIntent();
            if(i!=null) {
                Bundle b = i.getExtras();
                if (b!=null) {
                    boolean hasShift = false;
                    String username = null;
                    try {
                        hasShift =  b.getBoolean("ActiveShift");
                        username = b.getString("LoggedIn",null);
                        if(hasShift){
                            this.hasActiveShift = hasShift;
                            System.out.println("Has a active shift so will continue later");
                        }else{
                            System.out.println("Doesn't have an active shift");
                        }
                        if(username!=null){
                            isLoggedIn = true;
                        }
                    }catch (Exception e){
                        System.out.println("Exception: Doesn't have an active shift");
                    }
                }
            }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnConnectToPrinters:{
                String printerType = "";
                Intent scanIntent;
                if (this.radZebra.isChecked()) {
                    scanIntent = new Intent(this.context, ScanDeviceList.class);
                    scanIntent.putExtra("Printer-Type", "MZ220");
                    startActivityForResult(scanIntent, 3);
                    return;
                } else if (this.radRego.isChecked()) {
                    scanIntent = new Intent(this.context, ScanDeviceList.class);
                    scanIntent.putExtra("Printer-Type", "MPT-II");
                    startActivityForResult(scanIntent, 3);
                    return;
                } else {
                    Toast.makeText(this.context, "Select the Printer Type!", 0).show();
                    return;
                }
            }
        }
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
                        System.out.println("Got printer Details: Model: "+strModel + " IMEI:"+strtempBTAddress);
                        if (strtempBTAddress == null || !strtempBTAddress.contains(":") || strtempBTAddress.length() != 17)
                            return;
                        System.out.println("Connecting to printer : " + strtempBTAddress);
                        Printer printer = new Printer();
                        printer.setPrinterModel(strModel);
                        printer.setPrinterIMEI(strtempBTAddress);
                        this.printerModel = printer.getPrinterModel();
                        this.strBTAddress = printer.getPrinterIMEI();
                        pintMe(printer.getPrinterIMEI());
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
                        System.out.println("Got printer Details: Model: "+strTempModel + " IMEI:"+strBTAddress);
                        if (strBTAddress == null || !strBTAddress.contains(":") || strBTAddress.length() != 17)
                            return;
                        System.out.println("Connecting to printer : "+strBTAddress);
                        Printer printer = new Printer();
                        printer.setPrinterModel(strTempModel);
                        printer.setPrinterIMEI(strBTAddress);
                        this.printerModel = printer.getPrinterModel();
                        this.strBTAddress = printer.getPrinterIMEI();
                        pintMe(printer.getPrinterIMEI());
                    }

                    break;
                default:
            }

        } catch (Exception e) {
            Log.e("HPRTSDKSample", (new StringBuilder("Activity_Main --> onActivityResult ")).append(e.getMessage()).toString());
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        showConfirmExitAction();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id== android.R.id.home) {
            showConfirmExitAction();
            return true;
        }
        return false;
    }
    private void showConfirmExitAction() {


        if(isLoggedIn){
            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
            alertDialog.setTitle("Confirm Logout");
            alertDialog.setMessage("Are you sure you want to logout?");
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Logout",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new LogoutTask(context).execute();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }else{
            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
            alertDialog.setTitle("Confirm");
            alertDialog.setMessage("Are you sure you want to cancel Printer Setup?");
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(context,LoginScreen.class));
                        }
                    });
            alertDialog.show();
        }



    }



    //---------------------------------------------------------Printing Code Below--------------------------------------------------



    public  void pintMe(String printerMac){
        System.out.println("PrinterRegTask");
        progressDialog = ProgressDialog.show(this, "","Registering Please Wait ...", true);
        PrinterRegisterTask registerTask = new PrinterRegisterTask(this,printerMac,registerPrinterResponse);
        registerTask.execute();
        System.out.println("PrinterRegTask End");
    }


    //--------------------------------------------Technical Printing Code----------------------------------------------------


    {

        registerPrinterResponse = new RegisterPrinterResponse() {
            @Override
            public void onRegisterSuccess(String successMsg) {
                progressDialog.dismiss();

                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString("msgOfSuccess", successMsg);
                editor.commit();
                //set as new paired printer
                Printer p = new Printer("CP 11",printerModel,strBTAddress,true);
                PrinterAdapter.deleteAll(context);
                PrinterAdapter.addPrinter(p,RegisterAPrinter.this);

                PrintPrinterReg printPrinterReg = new PrintPrinterReg(RegisterAPrinter.this);
                printPrinterReg.printIfSuccessful(p);

                if(UserAdapter.getLoggedInUser(context)!=null){
                    if(hasActiveShift){
                        //continue with current shift
                        startActivity(new Intent(context,MainActivity.class));
                    }else{
                        startActivity(new Intent(context, ShiftManager.class));
                    }
                }else{
                    startActivity(new Intent(context,LoginScreen.class));
                }



            }

            @Override
            public void onRegisterFail(final String successMsg) {
                progressDialog.dismiss();


                android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
                alertDialog.setTitle("Failed To Register Printer");
                alertDialog.setMessage("Failed to Register this printer with the server, Try again or you may continue and the registration request will be sent later");
                alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "Retry",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent serverIntent = new Intent(context, ScanDeviceList.class);
                                startActivityForResult(serverIntent, HPRTPrinterHelper.ACTIVITY_CONNECT_BT);
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Continue",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                editor.putString("msgOfSuccess", successMsg);
                                editor.commit();


                                PrinterAdapter.deleteAll(context);
                                Printer p = new Printer("CP 11",printerModel,strBTAddress,true);
                                PrinterAdapter.addPrinter(p,RegisterAPrinter.this);

                                PrintPrinterReg printPrinterReg = new PrintPrinterReg(RegisterAPrinter.this);
                                printPrinterReg.printIfSuccessful(p);
                                dialog.dismiss();

                                if(UserAdapter.getLoggedInUser(context)!=null){
                                    if(hasActiveShift){
                                        //continue with current shift
                                        startActivity(new Intent(context,MainActivity.class));
                                    }else{
                                        startActivity(new Intent(context, ShiftManager.class));
                                    }
                                }else{
                                    startActivity(new Intent(context,LoginScreen.class));
                                }


                            }
                        });
                alertDialog.show();
            }
        };

    }


}


