package com.example.m1kes.parkingdemo.modules.supervisor.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.models.User;
import com.example.m1kes.parkingdemo.modules.supervisor.AdvancedMainActivity;
import com.example.m1kes.parkingdemo.modules.supervisor.models.JsonExports;
import com.example.m1kes.parkingdemo.modules.supervisor.printer.models.SpotCheckReceipt;
import com.example.m1kes.parkingdemo.modules.supervisor.util.SupervisorDataManager;
import com.example.m1kes.parkingdemo.printer.PrinterDevices;
import com.example.m1kes.parkingdemo.printer.models.CashupReceipt;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.printer.models.Vehicle;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrintJobAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.transactions.models.CashupTransaction;
import com.example.m1kes.parkingdemo.util.ObjectParser;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;
import com.example.m1kes.parkingdemo.util.StartupUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SpotCheck extends AppCompatActivity {

    private Context context;
    private Button btnCheckTransactions,btnCheckSearchHistory,btnCheckTerminalStatus;
    private double expectedAmount = 0;
    private double prepaidAmount = 0;
    private double declaredAmount = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_spot_check);
        this.context = SpotCheck.this;
        StartupUtils.setupActionBar("Spot Check",this);
        initGui();
        setupOnClicks();
        getTransactionDetails();


    }


    private void initGui(){
        btnCheckTransactions = (Button)findViewById(R.id.btnCheckTransactions);
        btnCheckSearchHistory = (Button)findViewById(R.id.btnCheckSearchHistory);
        btnCheckTerminalStatus = (Button)findViewById(R.id.btnCheckTerminalStatus);


    }

    private void setupOnClicks(){
        btnCheckTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSpotCheckTransactionDetails();
            }
        });
        btnCheckSearchHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnCheckTerminalStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



    }

    private void showSpotCheckTransactionDetails(){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        // LayoutInflater inflater = context.getLayoutInflater();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_spot_check_transactions, null);
        TextView txtSpotTotalAmount = (TextView)view.findViewById(R.id.txtSpotTotalAmount);
        TextView txtSpotPrepaidAmount = (TextView)view.findViewById(R.id.txtSpotPrepaidAmount);
        TextView txtSpotVehiclesLogged = (TextView)view.findViewById(R.id.txtSpotVehiclesLogged);
        Button btnPrintSpotCheck = (Button)view.findViewById(R.id.btnPrintSpotCheck);


        btnPrintSpotCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //First show the declaration of the spotcheck amount
                showDeclarationAmount();
            }
        });

         DecimalFormat df = new DecimalFormat("0.00");
        List<Vehicle> loggedVehicleTransactions = TransactionAdapter.getAllLoginPrintoutTransactions(context);
        int numLoggedVehicles  = loggedVehicleTransactions.size();

        //Logged Vehicles
        txtSpotVehiclesLogged.setText(String.valueOf(numLoggedVehicles));

        String strExpected = "$"+df.format(expectedAmount);
        String strPrepaid = "$"+df.format(prepaidAmount);

        txtSpotTotalAmount.setText(strExpected);
        txtSpotPrepaidAmount.setText(strPrepaid);


        builder.setView(view);
        //Create the Alert Dialog and Return it
        builder.setCancelable(true);
        final android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme; //style id
        dialog.show();

    }

    private void showDeclarationAmount(){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        // LayoutInflater inflater = context.getLayoutInflater();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_declare_spot_check_amount, null);
        final EditText editMarshalDeclareAmount = (EditText)view.findViewById(R.id.editMarshalDeclareAmount);
        final EditText editMarshalDeclareAmountConfirm  =(EditText)view.findViewById(R.id.editMarshalDeclareAmountConfirm);
        Button btnDeclareAmount =(Button)view.findViewById(R.id.btnDeclareAmount);
        btnDeclareAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Check if the amounts are valid
                if(isAmountValid(editMarshalDeclareAmount,editMarshalDeclareAmountConfirm)){
                    //if declared amount is valid then print receipt
                    startPrint();
                }

            }
        });

        builder.setView(view);
        //Create the Alert Dialog and Return it
        builder.setCancelable(true);
        final android.app.AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme; //style id
        dialog.show();

    }

    private boolean isAmountValid(EditText editMarshalDeclareAmount,EditText editMarshalDeclareAmountConfirm){

        boolean isValid = false;

        double firstAmount = 0;
        double secondAmt = 0;

        if(editMarshalDeclareAmount.getText().toString().length()==0||
                editMarshalDeclareAmount.getText().toString().equals("")){
            editMarshalDeclareAmount.setError("Invalid Amount!");
        }else if(editMarshalDeclareAmountConfirm.getText().toString().length()==0||
                editMarshalDeclareAmountConfirm.getText().toString().equals("")){
            editMarshalDeclareAmountConfirm.setError("Invalid Amount!");
        }else{
            //First get the first amount declared
            if(editMarshalDeclareAmount.getText().toString().length()<0){
                firstAmount = 0;
            }else{
                String strAmt =  editMarshalDeclareAmount.getText().toString();
                firstAmount = Double.valueOf(strAmt);
            }

            //Then get the confirm amount to be declared
            if(editMarshalDeclareAmountConfirm.getText().toString().length()<0){
                secondAmt = 0;
            }else{
                String strAmt =  editMarshalDeclareAmountConfirm.getText().toString();
                secondAmt = Double.valueOf(strAmt);
            }

            if(firstAmount==secondAmt){
                declaredAmount = firstAmount;
                isValid = true;
            }else{
                editMarshalDeclareAmountConfirm.setError("Not the same!");
            }
        }

        return isValid;

    }

    private void startPrint(){

        String shiftID =  ShiftDataManager.getCurrentActiveShiftID(context);
        BluetoothAdapter.getDefaultAdapter().enable();
        PrinterJob printerJob = new PrinterJob();
        Receipt receipt = getReciept();
        printerJob.setPrintAttemptTime(new Date());//id
        printerJob.setShiftId(shiftID);
        printerJob.setPrintData(receipt.getPrintString());
        printerJob.setPrintType(receipt.getPrintTypeAll());
        printerJob.setPrintStatus(PrinterJob.STATUS_PENDING);
        PrintJobAdapter.addPrintJob(printerJob,context);

        Intent i = new Intent(this,PrinterDevices.class);
        Bundle b = new Bundle();
        try {
            b.putByteArray("printerJob", ObjectParser.object2Bytes(printerJob));
        } catch (IOException e) {
            e.printStackTrace();
        }
        i.putExtras(b);
        startActivity(i);

    }

    private SpotCheckReceipt getReciept(){
        
        //TODO GET SUPERVISOR LOGGED IN
        //SET SHORT AMOUNT
        //SET COUNTED AMOUNT

        String username = UserAdapter.getLoggedInUser(context);
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        Shift shift = ShiftsAdapter.getShiftForUser(shiftID,context);

        User user =  UserAdapter.findUserWithName(username,context);
        String fn =  user.getFirstname().substring(0,2);
        String marshalDetails = ""+user.getEmpNo()+" : "+user.getSurname() +" "+fn;
        //TODO CHECK IF THIS WORKS
        String supervisor = SupervisorDataManager.getActiveSupervisor(context);
        String site = "City Parking (PVT)";
        String zone = shift.getPrecinctZoneName();

        int precinctID = shift.getPrecinctID();


        String date = new SimpleDateFormat("yyMMdd").format(new Date());
        String receiptNo = date+ShiftDataManager.getDefaultTerminalID(context)+""+precinctID;
        SpotCheckReceipt receipt = new SpotCheckReceipt();

        receipt.setSiteName(site);
        receipt.setZoneName(zone);
        receipt.setRecieptNumber(receiptNo);
        receipt.setMarshalDetails(marshalDetails);
        receipt.setSupervisor(supervisor);
        receipt.setExpectedAmount(expectedAmount);
        receipt.setPrepaidAmount(prepaidAmount);
        receipt.setCountedAmount(declaredAmount);
        double amtShort = expectedAmount - declaredAmount;
        receipt.setShortAmount(amtShort);

        return receipt;
    }


    private void getTransactionDetails(){

        String username = UserAdapter.getLoggedInUser(context);
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);


        Shift shift = ShiftsAdapter.getShiftForUser(shiftID,context);

        if(shift!=null) {
            this.expectedAmount = shift.getTotal_collected();
            this.prepaidAmount = shift.getTotal_prepaid();
        }else{
            Toast.makeText(context,"Invalid Shift",Toast.LENGTH_SHORT).show();
        }

        System.out.println("Total Amount Calculated from all of the local Transactions : "+expectedAmount);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(), AdvancedMainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
