package com.example.m1kes.parkingdemo.settings.activities;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.BuildConfig;
import com.example.m1kes.parkingdemo.LoginScreen;
import com.example.m1kes.parkingdemo.MainActivity;
import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.SplashScreen;
import com.example.m1kes.parkingdemo.SyncShifts;
import com.example.m1kes.parkingdemo.ViewPrinterJobs;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.printer.Activity_DeviceList;
import com.example.m1kes.parkingdemo.printer.PrinterDevices;
import com.example.m1kes.parkingdemo.printer.ScanDeviceList;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.settings.manager.AppSettingsManager;
import com.example.m1kes.parkingdemo.settings.manager.SecureAccountManager;
import com.example.m1kes.parkingdemo.settings.models.Account;
import com.example.m1kes.parkingdemo.settings.models.DeviceSettings;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.util.ObjectParser;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;
import com.example.m1kes.parkingdemo.util.StartupUtils;

import java.io.IOException;
import java.util.List;

import HPRTAndroidSDK.HPRTPrinterHelper;

public class DeviceSettingsManager extends AppCompatActivity {

    private Context context;
    private TextView txtDeviceModel,txtDeviceIMEI,txtDeviceTerminalID,txtDeviceActivationStatus,txtVersionNoSettings;
    private TextView txtUnsyncedShiftsSettings,txtTransUnsyncSettings,txtUnsyncedTransSettings;
    private TextView txtFingerPrintSupported,txtLinkedAccount;
    private Button btnEditLinkedAcc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_device_settings_manager);
        context = DeviceSettingsManager.this;
        StartupUtils.setupActionBar("Settings",this);
        initGui();
        getSettings();
        checkFingerPrintStatus();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_device_settings, menu);
        return true;
    }


    private void checkFingerPrintStatus(){

       if(SecureAccountManager.getFingerPrintSupported(context)){
           //Supported
           txtFingerPrintSupported.setText("true");
       }else{
           //Not Supported
           txtFingerPrintSupported.setText("false");
       }

        Account account = SecureAccountManager.getAccountLinked(context);
       if(account!=null){
           txtLinkedAccount.setText(account.getUsername());
           btnEditLinkedAcc.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   showOptions();
               }
           });
       }else{
           txtLinkedAccount.setText("None");
           btnEditLinkedAcc.setVisibility(View.GONE);
       }



    }

    public void showOptions(){

        final CharSequence[] options = { "Remove Account","Update Account" };
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
       // builder.setIcon(R.drawable.ic_printer_black_24dp);
        builder.setTitle("Edit Linked Account");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Remove Account")) {
                    //TODO TEST IF THIS CLEARS
                    SecureAccountManager.setAccountLinked(new Account(),context);
                    Toast.makeText(context,"Account Unlinked!",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    txtLinkedAccount.setText("None");
                    btnEditLinkedAcc.setVisibility(View.GONE);
                }else if(options[item].equals("Update Account")){
                    Toast.makeText(context,"WIP",Toast.LENGTH_SHORT).show();
                    //TODO Show dialog or fragment to change account details
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void getSettings(){

        DeviceSettings settings = AppSettingsManager.getDeviceSettings(context);

        txtDeviceModel.setText(settings.getDeviceModel());
        txtDeviceIMEI.setText(settings.getDeviceIMEI());
        txtDeviceTerminalID.setText(""+ ShiftDataManager.getDefaultTerminalID(context));




        if(settings.isActivated()){
            txtDeviceActivationStatus.setText("Yes");
        }else{
            txtDeviceActivationStatus.setText("No");
        }

        String versionName = BuildConfig.VERSION_NAME;
        txtVersionNoSettings.setText(versionName);


        //Get all unsynced Shifts
        List<Shift> shifts = ShiftsAdapter.getAllLocalShifts(context);
        //Set number of unsynced Shifts
        txtUnsyncedShiftsSettings.setText(""+shifts.size());

        //Get Transactions for shift
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        //There is an active shift
        if(shiftID!=null) {
            List<Transaction> transactions = TransactionAdapter.getAllUnSyncedTransactionsForShift(shiftID, context);
            txtUnsyncedTransSettings.setText(""+transactions.size());
        }else{
            txtUnsyncedTransSettings.setVisibility(View.GONE);
            txtTransUnsyncSettings.setVisibility(View.GONE);
        }



    }

    private void initGui(){
        txtDeviceModel = (TextView)findViewById(R.id.txtDeviceModel);
        txtDeviceIMEI = (TextView)findViewById(R.id.txtDeviceIMEI);
        txtDeviceTerminalID = (TextView)findViewById(R.id.txtDeviceTerminalID);
        txtDeviceActivationStatus = (TextView)findViewById(R.id.txtDeviceActivationStatus);
        txtVersionNoSettings= (TextView)findViewById(R.id.txtVersionNoSettings);

        txtUnsyncedShiftsSettings = (TextView)findViewById(R.id.txtUnsyncedShiftsSettings);
        txtUnsyncedTransSettings = (TextView)findViewById(R.id.txtUnsyncedTransSettings);
        txtTransUnsyncSettings = (TextView)findViewById(R.id.txtTransUnsyncSettings);

        txtFingerPrintSupported = (TextView)findViewById(R.id.txtFingerPrintSupported);
        txtLinkedAccount = (TextView)findViewById(R.id.txtLinkedAccount);

        btnEditLinkedAcc = (Button)findViewById(R.id.btnEditLinkedAcc);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            boolean activeShift = false;
            String username = UserAdapter.getLoggedInUser(context);
            if(username!=null){
                String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                if(shiftID!=null){
                    activeShift = true;
                }
            }
            if(activeShift){
                startActivity(new Intent(context,MainActivity.class));
            }else{
                startActivity(new Intent(context,LoginScreen.class));
            }


            return true;
        }else if(id == R.id.action_update_app){
            Toast.makeText(context,"Checking For Updates....",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }



}
