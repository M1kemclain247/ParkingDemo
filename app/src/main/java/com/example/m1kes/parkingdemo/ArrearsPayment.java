package com.example.m1kes.parkingdemo;

import android.app.AlertDialog;
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
import com.example.m1kes.parkingdemo.modules.supervisor.util.TransactionDumper;
import com.example.m1kes.parkingdemo.printer.PrinterDevices;
import com.example.m1kes.parkingdemo.printer.models.PayArrearsReceipt;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.sqlite.adapters.PaymentModeAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrecinctAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrintJobAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.transactions.PayArrearsTransaction;
import com.example.m1kes.parkingdemo.util.GeneralUtils;
import com.example.m1kes.parkingdemo.util.ObjectParser;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;
import com.example.m1kes.parkingdemo.util.StartupUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.example.m1kes.parkingdemo.util.DBUtils.getDateParts;
import static com.example.m1kes.parkingdemo.util.GeneralUtils.cleanRawVehicleReg;

public class ArrearsPayment extends AppCompatActivity{

    private EditText editFinePayment,editTender,editChange;
    private Button btnCancelPayment,btnPayFine;
    private TextView Arrears_RegNumber,txtArrearsCurrentUserBalance,Arrears_lastLogged,txtCurrentUserBalanceStatus;
    private Spinner Arrears_spinnerPaymentMethods;
    private Context context;
    private double currentUserBalance = 198262;
    private String userVehicleReg = null;
    private String lastLogged = null;
    private List<PaymentMode> paymentModes;
    private boolean isOffline = false;
    private DecimalFormat df = new DecimalFormat("0.00");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pay_arrears);
        context = ArrearsPayment.this;
        StartupUtils.setupActionBar("Make Payment",this);
        initGui();
        setupOnClicks();
        setCharacterLimits();
        setupSpinners();
        setupDefaultsfromPrevious();

    }

    private void setupSpinners(){
        paymentModes = PaymentModeAdapter.getAllPaymentTypes(context);
        PaymentModesSpinnerAdapter adapter  = new PaymentModesSpinnerAdapter(context,paymentModes);
        Arrears_spinnerPaymentMethods.setAdapter(adapter);

    }



    private void setupDefaultsfromPrevious(){


        if (getUserBalanceInfo()){

            if(userVehicleReg!=null){
                Arrears_RegNumber.setText(userVehicleReg);
            }

            if(lastLogged!=null){
                Arrears_lastLogged.setText(""+lastLogged);
            }else{
                Arrears_lastLogged.setTextColor(getResources().getColor(R.color.red));
                Arrears_lastLogged.setText("Offline Unavailable");
                isOffline = true;
            }

            if(currentUserBalance!=198262){

                if(currentUserBalance>0){
                    txtArrearsCurrentUserBalance.setText("$"+df.format(Math.abs(currentUserBalance)));
                    txtArrearsCurrentUserBalance.setTextColor(getResources().getColor(R.color.red));
                }else if(currentUserBalance ==0){
                    txtCurrentUserBalanceStatus.setText("Balance");
                    txtArrearsCurrentUserBalance.setText("$"+df.format(Math.abs(currentUserBalance)));
                    txtArrearsCurrentUserBalance.setTextColor(getResources().getColor(R.color.green));
                }else{
                    txtCurrentUserBalanceStatus.setText("Prepaid Balance");
                    txtArrearsCurrentUserBalance.setText("$"+df.format(Math.abs(currentUserBalance)));
                    txtArrearsCurrentUserBalance.setTextColor(getResources().getColor(R.color.green));
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
                    txtCurrentUserBalanceStatus.setText("Prepaid Balance");
                    txtArrearsCurrentUserBalance.setTextColor(getResources().getColor(R.color.green));
                    txtArrearsCurrentUserBalance.setText("Offline Balance: $"+ df.format(Math.abs(currentUserBalance)));
                }else if(currentUserBalance == 0){
                    txtArrearsCurrentUserBalance.setText("Offline Balance: $"+df.format(Math.abs(currentUserBalance)));
                    txtArrearsCurrentUserBalance.setTextColor(getResources().getColor(R.color.red));
                }else{
                    txtArrearsCurrentUserBalance.setTextColor(getResources().getColor(R.color.red));
                    txtArrearsCurrentUserBalance.setText("Offline Balance: $"+ df.format(currentUserBalance));
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
                        isNotNull = true;
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

        editFinePayment = (EditText)findViewById(R.id.editFinePayment);
        editTender = (EditText)findViewById(R.id.editTender);
        editChange = (EditText)findViewById(R.id.editChange);

        editChange.setEnabled(false);

        Arrears_RegNumber = (TextView)findViewById(R.id.Arrears_RegNumber);
        txtArrearsCurrentUserBalance = (TextView)findViewById(R.id.txtArrearsCurrentUserBalance);
        Arrears_lastLogged = (TextView)findViewById(R.id.Arrears_lastLogged);
        txtCurrentUserBalanceStatus = (TextView)findViewById(R.id.txtCurrentUserBalanceStatus);

        Arrears_spinnerPaymentMethods = (Spinner)findViewById(R.id.Arrears_spinnerPaymentMethods);


        btnCancelPayment = (Button)findViewById(R.id.btnCancelPayment);
        btnPayFine = (Button)findViewById(R.id.btnPayFine);
    }
    private void setCharacterLimits() {

        addTextWatchers();

    }

    private void setupOnClicks() {

        btnCancelPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,MainActivity.class));
            }
        });
        btnPayFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validate(new EditText[]{editTender,editFinePayment})){
                    //startArrearsPrint();
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
                                    manipulatePrintApiCall();

                                }
                            });
                    alertDialog.show();


                }

            }
        });

    }

    private void manipulatePrintApiCall(){

        //Start upload Transaction
        Transaction transaction =  makeArrearsTransaction(editFinePayment,editTender);
        PayArrearsTransaction payArrearsTransaction = new PayArrearsTransaction(context);
        payArrearsTransaction.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,transaction);
        //Start Print
        startArrearsPrint(transaction);



    }


    private void startArrearsPrint(Transaction transaction){

        PrinterJob printerJob = new PrinterJob();
        Receipt receipt = getPayArrearsReceipt(transaction);
        printerJob.setPrintAttemptTime(new Date());//id
        printerJob.setShiftId(ShiftDataManager.getCurrentActiveShiftID(context));
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

    private PayArrearsReceipt getPayArrearsReceipt(Transaction transaction){


        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String strInTime = ""+transaction.getTransactionDateTime();

        Calendar cal = Calendar.getInstance();
        String[] parts= getDateParts(strInTime);

        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));

        Date date = cal.getTime();

        String formattedDate =  formatter.format(date);
        String username = UserAdapter.getLoggedInUser(context);
        String precinctID = String.valueOf(ShiftsAdapter.getPrecinctIDForUser(username,context));
        Precinct precinct =  PrecinctAdapter.getPrecinctWithID(precinctID,context);
        String precinctName = precinct.getName();


        PayArrearsReceipt receipt = new PayArrearsReceipt();
        receipt.setReg_number(transaction.getVehicleregNumber());
        receipt.setAmountDue(transaction.getAmountPaid());
        receipt.setTender(transaction.getTenderAmount());
        receipt.setEntryTime(formattedDate);
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

        return receipt;
    }

    private Transaction makeArrearsTransaction(EditText editFinePayment,EditText editTender){


        String username = UserAdapter.getLoggedInUser(context);
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        String precinctID = String.valueOf(ShiftsAdapter.getPrecinctIDForUser(username,context));
        String rawVehicleReg = Arrears_RegNumber.getText().toString();
        String vehicleReg = cleanRawVehicleReg(rawVehicleReg);
        String transactionDateTime = getCurrentDateTime();
        //TODO CHANGE THIS ONCE REGISTRATION IS DONE
        String terminalID = String.valueOf(ShiftDataManager.getDefaultTerminalID(context));
        String requestDateTime = getCurrentDateTime();
        int userID =  UserAdapter.getCurrentLoggedUserID(username,context);
        String transactionType = null;

        if(currentUserBalance<=0){
            //Its a prepayment
           transactionType = TransactionType.TYPE_PREPAYMENT;
        }else{
            //Arrears Payment
            transactionType = TransactionType.TYPE_PAY_ARREARS;
        }




        double paymentAmount;
        if(editFinePayment.getText().toString().equalsIgnoreCase("")){
            paymentAmount = 0;
        }else{
            paymentAmount = Double.valueOf(editFinePayment.getText().toString());
        }
        double tenderAmount;
        if(editTender.getText().toString().equalsIgnoreCase("")){
            tenderAmount = 0;
        }else{
            tenderAmount = Double.valueOf(editTender.getText().toString());
        }

        double changeAmount = tenderAmount - paymentAmount;

        String selectedPaymentMethod =  getCurrentSelectedOption();
        int paymentModeId = PaymentModeAdapter.getPaymentModeID(selectedPaymentMethod,context);


        Transaction transaction = new Transaction();


        transaction.setShiftId(String.valueOf(shiftID));
        transaction.setUsername(username);
        transaction.setTerminalId(Integer.valueOf(terminalID));
        transaction.setPrecinctID(precinctID);
        transaction.setRequestDateTime(Long.parseLong(requestDateTime));
        transaction.setVehicleregNumber(vehicleReg);
        transaction.setIs_synced(false);
        transaction.setTransactionType(transactionType);
        transaction.setTransactionDateTime(Long.valueOf(transactionDateTime));
        transaction.setTenderAmount(tenderAmount);
        transaction.setChange(changeAmount);
        transaction.setAmountPaid(paymentAmount);
        transaction.setPayment_mode_id(paymentModeId);


        //Save trans to DB
        TransactionAdapter.addTransaction(transaction,context);
        //We get the same transaction ID Since its ++
        int transactionID = TransactionAdapter.getCurrentTransactionID(transactionDateTime,context);
        String receiptNumber = GeneralUtils.GenerateRecieptNumber(precinctID,String.valueOf(userID),String.valueOf(transactionID));
        //Update the database
        TransactionAdapter.updateReceiptNumber(transactionID,receiptNumber,context);
        transaction.setRecieptNumber(receiptNumber);

        //UPDATE CURRENT SHIFT TOTAL
        String tempShiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        if(tempShiftID!=null){
            System.out.println("Found active shift so updating transaction amount now");
            //Need to add method to determine cash or e-payments
           // ShiftsAdapter.updateShiftAmountCollected(tempShiftID,paymentAmount,context);
            if(selectedPaymentMethod.equalsIgnoreCase("Cash")){
                ShiftsAdapter.updateShiftCashCollected(tempShiftID,paymentAmount,context);
            }else if(selectedPaymentMethod.equalsIgnoreCase("E-Payment")){
                ShiftsAdapter.updateShiftEpaymentsCollected(tempShiftID,paymentAmount,context);
            }
        }else{
            System.out.println("No active shift to update amount for!");
        }


        List<Transaction> loggedvehicles =  TransactionAdapter.getAllLoginTransactions(context);
        if(!loggedvehicles.isEmpty()){
            for(Transaction t: loggedvehicles){
                if(t.getVehicleregNumber().equalsIgnoreCase(transaction.getVehicleregNumber())){
                    //Update previously logged vehicle payment
                    TransactionAdapter.setDueFinePayed(paymentAmount,t.getRecieptNumber(),context);
                }
            }
        }


        TransactionDumper dumper = new TransactionDumper(transaction,context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dumper.writeTransactionsDaily();
        }

        System.out.println("Current User Balance: "+currentUserBalance);


        double tempBal = currentUserBalance;
        double newBal = 0;

        if(tempBal<0){
            //- prepaid
            newBal = tempBal + paymentAmount;
        }else{
            //+ arrears
            newBal = tempBal - paymentAmount;
        }

        if(currentUserBalance<=0){
            //Its a prepayment
            //-2 - prepaid
            //-
            TransactionAdapter.updateLocalUserPrepaidTransactionBalance(shiftID,vehicleReg,newBal,context);

        }else{
            //Arrears payment
            //+
            TransactionAdapter.deductLocalUserPrepaidTransactionBalance(shiftID,vehicleReg,newBal,context);

        }

        return transaction;

    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private String getCurrentSelectedOption(){
        PaymentMode paymentMode =  (PaymentMode)Arrears_spinnerPaymentMethods.getSelectedItem();
        return  paymentMode.getName();
    }


    private void addTextWatchers(){

        editFinePayment.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if(editFinePayment.getText().toString().length()==1&&editFinePayment.getText().toString().contains(".")||
                        editTender.getText().toString().length()==0){
                    editFinePayment.setError("Invalid Amount");
                }else if(editTender.getText().toString().length()==1&&editTender.getText().toString().contains(".")||
                        editTender.getText().toString().length()==0){
                    editTender.setError("Invalid Amount");
                }else{
                    editChange.setText(calculateChange(editFinePayment, editTender));
                    if (count > 0) {
                        editChange.setError(null);
                        editFinePayment.setError(null);
                    }
                    validAmountsCheck();
                }
            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

        editTender.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(editTender.getText().toString().length()==1&&editTender.getText().toString().contains(".")||
                        editTender.getText().toString().length()==0){
                    editTender.setError("Invalid Amount");
                }else  if(editFinePayment.getText().toString().length()==1&&editFinePayment.getText().toString().contains(".")||
                        editTender.getText().toString().length()==0){
                    editFinePayment.setError("Invalid Amount");
                }else {
                    editChange.setText(calculateChange(editFinePayment, editTender));
                    if (count > 0) {
                        editChange.setError(null);
                        editTender.setError(null);
                    }
                    validAmountsCheck();

                }
            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });

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
    protected void onDestroy() {
        if(BluetoothAdapter.getDefaultAdapter()!=null&&BluetoothAdapter.getDefaultAdapter().isEnabled()){
            BluetoothAdapter.getDefaultAdapter().disable();
        }
        super.onDestroy();
    }



    private boolean validate(EditText[] fields){
        for(int i=0; i<fields.length; i++){
            EditText currentField=fields[i];
            if(currentField.getText().toString().length()<=0){
                currentField.setError("Required Field");
                return false;
            }

            if(currentField.getText().toString().equals("0")){
                currentField.setError("Invalid Field");
                return false;
            }

            if(currentField.getText().toString().length()==1&&currentField.getText().toString().contains(".")){
                currentField.setError("Invalid Amount");
                return false;
            }


        }



        if(validAmountsCheck()){
            return false;
        }

        return true;
    }


    private boolean validAmountsCheck(){

        double paymentAmount;
        double tenderAmount;

        //Making Fine
        if (!editFinePayment.getText().toString().equals("") && editFinePayment.getText().length() > 0) {
            paymentAmount = Double.parseDouble(editFinePayment.getText().toString());
        } else {
            paymentAmount = 0;
        }

        //making Payment
        if (!editTender.getText().toString().equals("") && editTender.getText().length() > 0) {
            tenderAmount = Double.parseDouble(editTender.getText().toString());
        } else {
            tenderAmount = 0;
        }

        if(paymentAmount>tenderAmount){
            editFinePayment.setError("Invalid Amount");
            return true;
        }else{
            editFinePayment.setError(null);
            return false;
        }
    }

    private String calculateChange(EditText editFinePayment,EditText editTender) {
        double number1;
        double number2;
        if(!editFinePayment.getText().toString().equals("")&&editFinePayment.getText().length() > 0) {
            number1 = Double.parseDouble(editFinePayment.getText().toString());
        } else {
            number1 = 0;
        }
        if(!editTender.getText().toString().equals("")&& editTender.getText().length() > 0) {
            number2 = Double.parseDouble(editTender.getText().toString());
        } else {
            number2 = 0;
        }

        return Double.toString(number2 - number1);
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


    private String getCurrentDateTime(){

        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        return  sf.format(calendar.getTime());

    }

}
