package com.example.m1kes.parkingdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import android.view.WindowManager;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.adapters.spinners.PaymentModesSpinnerAdapter;
import com.example.m1kes.parkingdemo.models.PaymentMode;
import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.models.TransactionType;
import com.example.m1kes.parkingdemo.models.User;
import com.example.m1kes.parkingdemo.modules.supervisor.util.TransactionDumper;
import com.example.m1kes.parkingdemo.printer.PrinterDevices;
import com.example.m1kes.parkingdemo.printer.models.MakePaymentReceipt;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.printer.models.MakeFineReceipt;
import com.example.m1kes.parkingdemo.sqlite.adapters.PaymentModeAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrecinctAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrintJobAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.transactions.MakeFineTransaction;
import com.example.m1kes.parkingdemo.transactions.MakePaymentTransaction;
import com.example.m1kes.parkingdemo.util.GeneralUtils;
import com.example.m1kes.parkingdemo.util.ObjectParser;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;
import com.example.m1kes.parkingdemo.util.StartupUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter.updateShiftPrepaidAmountCollected;
import static com.example.m1kes.parkingdemo.util.DBUtils.getDateParts;
import static com.example.m1kes.parkingdemo.util.GeneralUtils.cleanRawVehicleReg;



public class VehicleLogin extends AppCompatActivity implements View.OnClickListener {


    EditText Login_editAmountDue,Login_editTender,Login_editChange,Login_editBayNumber,Login_editDuration;
    Button btnLoginVehicle,Login_btnCancel;
    private TextView txtLoginCurrentUserBalance,Login_editEntryTime,Login_editRegNumber,Login_lastLogged,Login_txtCurrentUserBalanceStatus;
    private Context context;
    private Spinner Login_spinnerPaymentMethods;
    private List<PaymentMode> paymentModes;
    private double currentUserBalance = 198262;
    private String userVehicleReg = null;
    private String lastLogged = null;
    private boolean isOffline = false;
    private DecimalFormat df = new DecimalFormat("0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_vehicle);
        StartupUtils.setupActionBar("Login Vehicle",this);
        context = VehicleLogin.this;
        paymentModes = new ArrayList<>();
        initGui();
        setOnItemClicks();
        theWatcher();// checks if the edittext is changed then it calculates the change
        setupSpinners();

        //Default amount Due
        setupDefaults();

        if (getUserBalanceInfo()){

            if(userVehicleReg!=null){
                Login_editRegNumber.setText(userVehicleReg);
            }

            if(lastLogged!=null){
                Login_lastLogged.setText(""+lastLogged);
            }else{
                Login_lastLogged.setTextColor(getResources().getColor(R.color.red));
                Login_lastLogged.setText("Offline Unavailable");
                isOffline = true;
            }

            if(currentUserBalance!=198262){

                if(currentUserBalance>0){
                    txtLoginCurrentUserBalance.setText("$"+df.format(currentUserBalance));
                    txtLoginCurrentUserBalance.setTextColor(getResources().getColor(R.color.red));
                }else if(currentUserBalance == 0){
                    Login_txtCurrentUserBalanceStatus.setText("Balance");
                    txtLoginCurrentUserBalance.setText("$"+df.format(Math.abs(currentUserBalance)));
                    txtLoginCurrentUserBalance.setTextColor(getResources().getColor(R.color.green));
                }else{
                    Login_txtCurrentUserBalanceStatus.setText("Prepaid Balance");
                    txtLoginCurrentUserBalance.setText("$"+df.format(Math.abs(currentUserBalance)));
                    txtLoginCurrentUserBalance.setTextColor(getResources().getColor(R.color.green));
                }

            }else{

                if(!isNetworkAvailable(context)){
                    //If there is no network
                    String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                    double localBalance = TransactionAdapter.getLocalUserTransactionBalance(shiftID,userVehicleReg,context);
                    currentUserBalance = localBalance;
                }else{
                    currentUserBalance =0;
                }

                if(currentUserBalance<0){
                    txtLoginCurrentUserBalance.setTextColor(getResources().getColor(R.color.green));
                    txtLoginCurrentUserBalance.setText("Offline Balance: $"+ df.format(Math.abs(currentUserBalance)));
                }else if(currentUserBalance == 0){
                    txtLoginCurrentUserBalance.setText("Offline Balance: $"+df.format(Math.abs(currentUserBalance)));
                    txtLoginCurrentUserBalance.setTextColor(getResources().getColor(R.color.red));
                }else{
                    txtLoginCurrentUserBalance.setTextColor(getResources().getColor(R.color.red));
                    txtLoginCurrentUserBalance.setText("Offline Balance: $"+ df.format(currentUserBalance));
                }


            }


        }


    }


    private boolean getUserBalanceInfo(){
        boolean isNotNull = false;
        Intent i = getIntent();
        if(i!=null) {
            Bundle b = i.getExtras();
            if (b!=null) {
                String balance = null;
                String vehicleReg = null;
                String lastLogged = null;
                try {
                    balance =  b.getString("Balance");
                    vehicleReg = b.getString("VehicleReg");
                    lastLogged = b.getString("LastLogged");
                    if(balance!=null){
                        this.currentUserBalance = Double.parseDouble(balance);
                    }
                    if(vehicleReg!=null){
                        this.userVehicleReg = vehicleReg;
                    }
                    if(lastLogged!=null){

                        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmmss");
                        String formattedDate = null;
                        Date lastDate  = null;
                        try {
                            lastDate = df2.parse(lastLogged);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat pf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        formattedDate = pf.format(lastDate);
                        this.lastLogged = formattedDate;
                    }
                    isNotNull = true;
                }catch (Exception e){
                    isNotNull = false;
                }

            }
        }
        return isNotNull;
    }


    private void initGui(){

        //Edit texts
        Login_editRegNumber = (TextView) findViewById(R.id.Login_editRegNumber);
        Login_txtCurrentUserBalanceStatus = (TextView)findViewById(R.id.Login_txtCurrentUserBalanceStatus);

        Login_editAmountDue = (EditText) findViewById(R.id.Login_editAmountDue);
        Login_editAmountDue.setEnabled(false);
        Login_editTender = (EditText) findViewById(R.id.Login_editTender);
        Login_editChange = (EditText) findViewById(R.id.Login_editChange);
        Login_editBayNumber = (EditText)findViewById(R.id.Login_editBayNumber);
        Login_editDuration = (EditText)findViewById(R.id.Login_editDuration);
        //buttons
        btnLoginVehicle = (Button) findViewById(R.id.btnLoginVehicle);
        Login_btnCancel = (Button) findViewById(R.id.Login_btnCancel);
        //Spinners
        Login_spinnerPaymentMethods = (Spinner)findViewById(R.id.Login_spinnerPaymentMethods);
        //Textviews
        txtLoginCurrentUserBalance = (TextView)findViewById(R.id.txtLoginCurrentUserBalance);
        Login_editEntryTime = (TextView) findViewById(R.id.Login_editEntryTime);
        Login_lastLogged = (TextView)findViewById(R.id.Login_lastLogged);

        String tymStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(Calendar.getInstance().getTime());
        Login_editEntryTime.setText(tymStamp);


        Login_editChange.setEnabled(false);

    }

    private void setOnItemClicks(){
        btnLoginVehicle.setOnClickListener(this);
        Login_btnCancel.setOnClickListener(this);

    }



    /***
     * Responsible for Setting the default amount due = calculated by duration * tariff
     * **/
    private void setupDefaults(){

        int defaultDuration = 1;
        //By default lets set the duration to 1
        Login_editDuration.setText(""+defaultDuration);
        double tarrif = getDefaultTarrifForPrecinct();
        double amountDue = (double)defaultDuration * tarrif ;
        //this happens on onCreate it generates the default amount due
        //by multiplying the default duration of 1 hour and the tarrif to work out amount due for that precinct
        Login_editAmountDue.setText(""+amountDue);
        Login_editChange.setText(""+0);

    }



    private double getDefaultTarrifForPrecinct(){
        String username = UserAdapter.getLoggedInUser(context);
        String precinctID = String.valueOf(ShiftsAdapter.getPrecinctIDForUser(username,context));
        return PrecinctAdapter.getPrecinctTarrif(precinctID,context);
    }

    private void setupSpinners(){
        paymentModes = PaymentModeAdapter.getAllPaymentTypes(context);
        PaymentModesSpinnerAdapter adapter  = new PaymentModesSpinnerAdapter(context,paymentModes);
        Login_spinnerPaymentMethods.setAdapter(adapter);

    }


    private String getCurrentSelectedOption(){
        PaymentMode paymentMode =  (PaymentMode)Login_spinnerPaymentMethods.getSelectedItem();
        return  paymentMode.getName();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            AlertDialog alertDialog = new AlertDialog.Builder(context).create();
            alertDialog.setTitle("Cancel Transaction");
            alertDialog.setMessage("Are you sure you want to cancel this transaction?");
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
                            if(BluetoothAdapter.getDefaultAdapter()!=null&&BluetoothAdapter.getDefaultAdapter().isEnabled()){
                                BluetoothAdapter.getDefaultAdapter().disable();
                            }
                            startActivity(new Intent(context,MainActivity.class));
                        }
                    });
            alertDialog.show();
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {

        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Cancel Transaction");
        alertDialog.setMessage("Are you sure you want to cancel this transaction?");
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
                        if(BluetoothAdapter.getDefaultAdapter()!=null&&BluetoothAdapter.getDefaultAdapter().isEnabled()){
                            BluetoothAdapter.getDefaultAdapter().disable();
                        }
                        startActivity(new Intent(context,MainActivity.class));
                    }
                });
        alertDialog.show();

    }


    /**Responsible for Handling All Gui Clicks**/
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            /**Buttons start:**/
            case R.id.Login_btnCancel:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.btnLoginVehicle:

                if(validate(new EditText[]{Login_editChange, Login_editAmountDue})) {


                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Confirm");
                    alertDialog.setMessage("Are you sure these details are correct?");
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
                                    //Choose what transactions will be created and how ect..
                                    mainpulateApiCalls(Login_editAmountDue,Login_editTender);

                                }
                            });
                    alertDialog.show();
                }

                break;
            /**Buttons end:**/
            default:
        }


    }



    private double calculateAmountDue(double tarrif ,double duration){
        return duration * tarrif;
    }

    private void theWatcher() {

        Login_editTender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(Login_editTender.getText().toString().length()==1&&Login_editTender.getText().toString().contains(".")){
                    Login_editTender.setError("Invalid Amount");
                }else{
                    Login_editChange.setText(calculateChange(Login_editAmountDue, Login_editTender));
                    if (count > 0) {
                        Login_editChange.setError(null);
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //Set amount due on duration changed
        Login_editDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkValidDuration();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private boolean checkValidDuration(){

        boolean isValid = false;

        if (!Login_editDuration.getText().toString().equalsIgnoreCase("")
                ) {
            if(Double.parseDouble(Login_editDuration.getText().toString())<9&&
                    Login_editDuration.getText().toString().length()>0&&
                    Double.parseDouble(Login_editDuration.getText().toString())!=0) {

                if(!Login_editDuration.getText().toString().contains(".")){

                    double duration = Double.parseDouble(Login_editDuration.getText().toString());
                    double tarrif = getDefaultTarrifForPrecinct();
                    double amountDue =  calculateAmountDue(tarrif, duration);

                    String strAmountDue = String.valueOf(amountDue);
                    Login_editAmountDue.setText(strAmountDue);

                    Login_editChange.setText(calculateChange(Login_editAmountDue, Login_editTender));
                    isValid = true;
                }else{
                    Login_editDuration.setError("Invalid Duration");
                }
            }else{
                Login_editDuration.setError("Max 1 - 8");
            }
        } else {
            Login_editDuration.setError("Invalid Duration");
        }
        return isValid;
    }


    private String calculateChange(EditText editAmountDue, EditText editTender) {
        double amountDue;
        double change;
        if (editAmountDue.getText().toString() != "" && editAmountDue.getText().length() > 0) {
            amountDue = Double.parseDouble(editAmountDue.getText().toString());
        } else {
            amountDue = 0;
        }
        if (editTender.getText().toString() != "" && editTender.getText().length() > 0) {
            change = Double.parseDouble(editTender.getText().toString());
        } else {
            change = 0;
        }
        return Double.toString(change - amountDue);
    }

    private double calculatePaidAmount(EditText editTenderAmount,EditText editChangeAmount){

        double tenderAmount;
        double change;

        if (editTenderAmount.getText().toString() != "" && editTenderAmount.getText().length() > 0) {
            tenderAmount = Double.parseDouble(editTenderAmount.getText().toString());
        } else {
            tenderAmount = 0;
        }
        if (editChangeAmount.getText().toString() != "" && editChangeAmount.getText().length() > 0) {
            change = Double.parseDouble(editChangeAmount.getText().toString());
        } else {
            change = 0;
        }

        return (tenderAmount - change);
    }




    private boolean validate(EditText[] fields){
        for(int i=0; i<fields.length; i++){
            EditText currentField=fields[i];
            if(currentField.getText().toString().length()<=0){
                currentField.setError("Required Field");
                return false;
            }
        }

        if(Login_editTender.getText().toString().length()==1&&Login_editTender.getText().toString().contains(".")){
            Login_editTender.setError("Invalid Amount");
            return false;
        }

        if(!checkValidDuration()){
            return false;
        }

        try {
            double tenderAmount = Double.parseDouble(Login_editTender.getText().toString().equalsIgnoreCase("")?"0":Login_editTender.getText().toString());
            double amountDue = Double.parseDouble(Login_editAmountDue.getText().toString().equalsIgnoreCase("")?"0":Login_editAmountDue.getText().toString());
            int duration = Integer.parseInt(Login_editDuration.getText().toString().equalsIgnoreCase("")?"0":Login_editDuration.getText().toString());

            if (tenderAmount < amountDue)
            {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Invalid Amount");
                alertDialog.setMessage("Motorist hasn't paid enough Money\nto park for " + duration + " Hours");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                throw new Exception();
            }




        }catch(Exception e){
            return false;
        }





//        if(invalidTenderDurationCheck()){
//            return false;
//        }




        return true;
    }


    private boolean invalidTenderDurationCheck(){

        double tenderAmount;
        double duration;

        //making Payment
        if (Login_editTender.getText().toString() != "" && Login_editTender.getText().length() > 0) {
            tenderAmount = Double.parseDouble(Login_editTender.getText().toString());
        } else {
            tenderAmount = 0;
        }

        //making Payment
        if (Login_editDuration.getText().toString() != "" && Login_editDuration.getText().length() > 0) {
            duration = Double.parseDouble(Login_editDuration.getText().toString());
        } else {
            duration = 0;
        }

        String username = UserAdapter.getLoggedInUser(context);
        String precinctID = String.valueOf(ShiftsAdapter.getPrecinctIDForUser(username,context));
        double tarriff = PrecinctAdapter.getPrecinctTarrif(precinctID,context);




        //Check if making payment and if duration is longer than the amount tendered
        if(tenderAmount>0&&(duration*tarriff)>tenderAmount){
            Login_editDuration.setError("Cannot Park for longer than tendered");
            return true;
        }else{
            return false;
        }


    }

    @Override
    protected void onDestroy() {
        if(BluetoothAdapter.getDefaultAdapter()!=null&&BluetoothAdapter.getDefaultAdapter().isEnabled()){
            BluetoothAdapter.getDefaultAdapter().disable();
        }
        super.onDestroy();
    }


    private void mainpulateApiCalls(EditText login_editAmountDue,EditText login_editTender){

        double amountDue;
        double tenderAmount;

        //Making Fine
        if (login_editAmountDue.getText().toString() != "" && login_editAmountDue.getText().length() > 0) {
            amountDue = Double.parseDouble(login_editAmountDue.getText().toString());
        } else {
            amountDue = 0;
        }

        //making Payment
        if (login_editTender.getText().toString() != "" && login_editTender.getText().length() > 0) {
            tenderAmount = Double.parseDouble(login_editTender.getText().toString());
        } else {
            tenderAmount = 0;
        }




        boolean isFine = false;
        boolean isPayment = false;

        Map<String,Transaction> transactionMap = new HashMap<>();

        double tempAmountPaid = 0;


        if(tenderAmount>0){
            //Initiate Transaction for FinePayment
            Transaction transaction = makePaymentTransaction();
            tempAmountPaid = transaction.getAmountPaid();

            transactionMap.put("MakePayment",transaction);


            isPayment = true;

        }

        if(amountDue>0){
            //Initiate Transaction for MakeFine
            Transaction transaction = makeFineTransaction();
            if(isPayment){
                transaction.setAmountPaid(tempAmountPaid);
                transaction.setUserBalance(currentUserBalance);
            }
            isFine = true;
            transactionMap.put("MakeFine",transaction);

        }

        //Link the payment to the fine

        String paymentRefNo =  transactionMap.get("MakePayment").getRecieptNumber();
        String fineRefNo = transactionMap.get("MakeFine").getRecieptNumber();



        transactionMap.get("MakeFine").setIs_Paid(true);
        transactionMap.get("MakeFine").setPaymentRefNo(paymentRefNo);
        transactionMap.get("MakePayment").setFineRefNo(fineRefNo);



        MakeFineTransaction makeFineTransaction = new MakeFineTransaction(context);
        makeFineTransaction.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,transactionMap.get("MakeFine"));


        MakePaymentTransaction makePaymentTransaction = new MakePaymentTransaction(context);
        makePaymentTransaction.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,transactionMap.get("MakePayment"));



            //Update the fine transaction with the paid amount if they paid
            TransactionAdapter.updateTransactionDetails(transactionMap.get(TransactionType.TYPE_FINE),context);
          //Update the payment transaction in the database
            TransactionAdapter.updateTransactionDetails(transactionMap.get(TransactionType.TYPE_PAYMENT),context);

            //IF the user pays for his/her fine PRINT
            startPaymentPrint(transactionMap.get("MakePayment"));


    }





    private void startFinePrint(Transaction transaction){

        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        PrinterJob printerJob = new PrinterJob();
        Receipt receipt = getFineReceipt(transaction);
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

    private MakeFineReceipt getFineReceipt(Transaction transaction){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String strInTime = ""+transaction.getIn_datetime();

        Calendar cal = Calendar.getInstance();
        String[] parts= getDateParts(strInTime);

        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));

        Date date = cal.getTime();

        String formattedDate =  formatter.format(date);

        String username = UserAdapter.getLoggedInUser(context);
        String precinctID = String.valueOf(ShiftsAdapter.getPrecinctIDForUser(username,context));
        Precinct precinct =  PrecinctAdapter.getPrecinctWithID(precinctID,context);
        String precinctName = precinct.getName();


        MakeFineReceipt receipt = new MakeFineReceipt();
        receipt.setReg_number(transaction.getVehicleregNumber());
        receipt.setAmountDue(transaction.getAmountDue());
        receipt.setTender(transaction.getTenderAmount());
        receipt.setEntryTime(formattedDate);
        receipt.setExpiryTime(getExpiryTimeAsString(transaction));
        receipt.setChange(transaction.getChange());
        receipt.setZone(ShiftsAdapter.getZoneForUser(username,context));
        //set precinct
        receipt.setPrecinctName(precinctName);
        receipt.setRecieptNumber(transaction.getRecieptNumber());
        receipt.setBayNumber(transaction.getBayNumber());
        receipt.setCurrentUser(transaction.getUsername());
        receipt.setOffline(isOffline);

        if(currentUserBalance==198262){
            currentUserBalance = 0.00;
        }
        receipt.setCurrentBalance(currentUserBalance);

        return receipt;
    }

    private String getExpiryTimeAsString(Transaction transaction){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String strInTime = ""+transaction.getExpiry_datetime();

        Calendar cal = Calendar.getInstance();
        String[] parts= getDateParts(strInTime);

        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));

        Date date = cal.getTime();

        return formatter.format(date);

    }

    private void startPaymentPrint(Transaction transaction){

        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        PrinterJob printerJob = new PrinterJob();
        Receipt receipt = getPaymentReceipt(transaction);
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

    private MakePaymentReceipt getPaymentReceipt(Transaction transaction){



        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String strInTime = ""+transaction.getIn_datetime();

        Calendar cal = Calendar.getInstance();
        String[] parts= getDateParts(strInTime);

        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));

        Date date = cal.getTime();

        String formattedDate =  formatter.format(date);
        String username = UserAdapter.getLoggedInUser(context);
        String precinctID = String.valueOf(ShiftsAdapter.getPrecinctIDForUser(username,context));
        Precinct precinct =  PrecinctAdapter.getPrecinctWithID(precinctID,context);
        String precinctName = precinct.getName();


        MakePaymentReceipt receipt = new MakePaymentReceipt();
        receipt.setReg_number(transaction.getVehicleregNumber());
        receipt.setAmountDue(transaction.getAmountDue());
        receipt.setAmountPaid(transaction.getAmountPaid());
        receipt.setTender(transaction.getTenderAmount());
        receipt.setEntryTime(formattedDate);
        receipt.setExpiryTime(getExpiryTimeAsString(transaction));

        receipt.setChange(transaction.getChange());
        receipt.setZone(ShiftsAdapter.getZoneForUser(username,context));
        //set precinct
        receipt.setPrecinctName(precinctName);
        receipt.setRecieptNumber(transaction.getRecieptNumber());
        receipt.setBayNumber(transaction.getBayNumber());
        receipt.setCurrentUser(transaction.getUsername());
        if(currentUserBalance==198262){
            currentUserBalance = 0.00;
        }
        receipt.setOffline(isOffline);
        receipt.setCurrentBalance(currentUserBalance);
        receipt.setTender(transaction.getTenderAmount());
        receipt.setChange(transaction.getChange());

        return receipt;
    }


    private Transaction makeFineTransaction(){


        String username = UserAdapter.getLoggedInUser(context);
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        System.out.println("Shift ID: "+shiftID+"v");

        String precinctID = String.valueOf(ShiftsAdapter.getPrecinctIDForUser(username,context));
        String in_datetime = getCurrentDateTime();
        double duration = Double.valueOf(Login_editDuration.getText().toString()); //Amount the user actually gave u
        //Calculated using tarrif and amount paid

        String rawVehicleReg = Login_editRegNumber.getText().toString();
        String vehicleReg = cleanRawVehicleReg(rawVehicleReg);

        //get the tarrif for that precinct
        double tarriff = PrecinctAdapter.getPrecinctTarrif(precinctID,context);
        double amountDue = tarriff * duration;

        String terminalID =  String.valueOf(ShiftDataManager.getDefaultTerminalID(context));
        String requestDateTime = getCurrentDateTime();

        int userID =  UserAdapter.getCurrentLoggedUserID(username,context);
        int bayNumber = 0;

        if(Login_editBayNumber.getText().toString().equalsIgnoreCase("")){
            bayNumber = 0;
        }else{
            bayNumber = Integer.valueOf(Login_editBayNumber.getText().toString());
        }

        boolean is_Prepayment = false;
        System.out.println("Checking if it is a PrePayment Balance: "+currentUserBalance);

        if(currentUserBalance<0){
            System.out.println("This transaction will be treated as a Prepayment!");
            is_Prepayment = true;
            //TODO CHECK IF UPDATING THE SHIFT PREPAID AMOUNT
            updateShiftPrepaidAmountCollected(shiftID,amountDue,context);
            //TODO See if deducting from the current prepaid balance works
        }


        Transaction transaction = new Transaction();

        transaction.setShiftId(shiftID);
        transaction.setUsername(username);
        transaction.setTerminalId(Integer.valueOf(terminalID));
        //May cuz null cuz no reciept no
        transaction.setBayNumber(bayNumber);
        transaction.setPrecinctID(precinctID);
        transaction.setRequestDateTime(Long.parseLong(requestDateTime));
        transaction.setVehicleregNumber(vehicleReg);
        transaction.setDuration((int)duration);
        transaction.setIn_datetime(Long.parseLong(in_datetime));
        transaction.setExpiry_datetime(calculateExpiryTime(Long.valueOf(in_datetime),(int)duration));
        transaction.setIs_synced(false);
        transaction.setAmountDue(amountDue);
        transaction.setTransactionType(TransactionType.TYPE_FINE);
        transaction.setIs_Prepayment(is_Prepayment);

        //Save trans to DB
        TransactionAdapter.addTransaction(transaction,context);
        //We get the same transaction ID Since its ++
        int transactionID = TransactionAdapter.getTransactionID(in_datetime,context);



        String receiptNumber = GeneralUtils.GenerateRecieptNumber(precinctID,String.valueOf(userID),String.valueOf(transactionID));
        //Update the database
        TransactionAdapter.updateReceiptNumber(transactionID,receiptNumber,context);
        transaction.setRecieptNumber(receiptNumber);

        TransactionDumper dumper = new TransactionDumper(transaction,context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dumper.writeTransactionsDaily();
        }

        TransactionAdapter.deductLocalUserPrepaidTransactionBalance(shiftID,vehicleReg,amountDue,context);

        return transaction;

    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    private Transaction makePaymentTransaction(){

        String in_datetime = getCurrentDateTime();
        //TODO CHECK IF THIS WORKS
        String username = UserAdapter.getLoggedInUser(context);

        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);

        System.out.println("Shift ID: "+shiftID+"v");

        String precinctID = String.valueOf(ShiftsAdapter.getPrecinctIDForUser(username,context));

        long transactionDateTime = Long.valueOf(getCurrentDateTime());
        String rawVehicleReg = Login_editRegNumber.getText().toString();
        String vehicleReg = cleanRawVehicleReg(rawVehicleReg);
        //get tender amount and change amount

        int bayNumber = 0;

        if(Login_editBayNumber.getText().toString().equalsIgnoreCase("")){
            bayNumber = 0;
        }else{
            bayNumber = Integer.valueOf(Login_editBayNumber.getText().toString());
        }


        double tenderAmount;

        if(Login_editTender.getText().toString().equalsIgnoreCase("")){
            tenderAmount = 0;
        }else{
            tenderAmount = Double.valueOf(Login_editTender.getText().toString());
        }

        double changeAmount;

        if(Login_editChange.getText().toString().equalsIgnoreCase("")){
            changeAmount = 0;
        }else{
            changeAmount = Double.valueOf(Login_editChange.getText().toString());
        }



        double duration = Double.valueOf(Login_editDuration.getText().toString());

        double tarriff = PrecinctAdapter.getPrecinctTarrif(precinctID,context);
        double amountDue = tarriff * duration;


        double amountPaid =  amountDue; //calculatePaidAmount(Login_editTender,Login_editChange);



        System.out.println("Amount Paid =="+amountPaid);



        String terminalID =  String.valueOf(ShiftDataManager.getDefaultTerminalID(context));

        String requestDateTime = getCurrentDateTime();
        int userID =  UserAdapter.getCurrentLoggedUserID(username,context);


        String selectedPaymentMethod =  getCurrentSelectedOption();
        int paymentModeId = PaymentModeAdapter.getPaymentModeID(selectedPaymentMethod,context);

        double piaAmount =  currentUserBalance;
        if(piaAmount<0){
            //They can use their prepayment as method of payment

        }


        Transaction transaction = new Transaction();

        transaction.setShiftId(shiftID);
        transaction.setUsername(username);
        transaction.setTerminalId(Integer.valueOf(terminalID));
        transaction.setPrecinctID(precinctID);
        transaction.setRequestDateTime(Long.parseLong(requestDateTime));
        transaction.setVehicleregNumber(vehicleReg);
        transaction.setBayNumber(bayNumber);
        transaction.setTransactionDateTime(transactionDateTime);
        transaction.setIs_synced(false);
        transaction.setAmountPaid(amountPaid);
        transaction.setAmountDue(amountDue);
        transaction.setTransactionType(TransactionType.TYPE_PAYMENT);
        transaction.setPayment_mode_id(paymentModeId);
        transaction.setTenderAmount(tenderAmount);
        transaction.setChange(changeAmount);
        transaction.setIn_datetime(transactionDateTime);
        transaction.setExpiry_datetime(calculateExpiryTime(Long.valueOf(in_datetime),(int)duration));

        //Save trans to DB
        TransactionAdapter.addTransaction(transaction,context);
        //We get the same transaction ID Since its ++
        //We use the transaction time to get the transaction instead of the in_date_time
        int transactionID = TransactionAdapter.getCurrentTransactionID(String.valueOf(transactionDateTime),context);
        String receiptNumber = GeneralUtils.GenerateRecieptNumber(precinctID,String.valueOf(userID),String.valueOf(transactionID));
        //Update the database
        TransactionAdapter.updateReceiptNumber(transactionID,receiptNumber,context);
        transaction.setRecieptNumber(receiptNumber);

        //UPDATE CURRENT SHIFT TOTAL
        String tempShiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        if(tempShiftID!=null){
            System.out.println("Found active shift so updating transaction amount now");

            //TODO DETERMINE WETHER CASH OR E-PAYMENT
            if(selectedPaymentMethod.equalsIgnoreCase("Cash")){
                ShiftsAdapter.updateShiftCashCollected(tempShiftID,amountPaid,context);
            }else if(selectedPaymentMethod.equalsIgnoreCase("E-Payment")){
                ShiftsAdapter.updateShiftEpaymentsCollected(tempShiftID,amountPaid,context);
            }

            //ShiftsAdapter.updateShiftAmountCollected(tempShiftID,amountPaid,context);
        }else{
            System.out.println("No active shift to update amount for!");
        }


        TransactionDumper dumper = new TransactionDumper(transaction,context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dumper.writeTransactionsDaily();
        }

        return transaction;

    }



    private String getCurrentDateTime(){

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        return  sf.format(calendar.getTime());

    }


    //TODO FIX ERROR WITH CALCULATING EXPIRY TIME
    private long calculateExpiryTime(long in_date_time,int duration){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String strInTime = ""+in_date_time;

        System.out.println("In Date Time :"+in_date_time +" Duration: "+duration);


        Calendar cal = Calendar.getInstance();
        String[] parts= getDateParts(strInTime);

        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
        cal.add(Calendar.HOUR_OF_DAY, duration);
        System.out.println("Calculating expiry time of this vehicle : "+formatter.format(cal.getTime()));

        return Long.valueOf(formatter.format(cal.getTime()));
    }



}
