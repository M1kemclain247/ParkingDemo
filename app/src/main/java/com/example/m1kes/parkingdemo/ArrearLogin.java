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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.models.TransactionType;
import com.example.m1kes.parkingdemo.modules.supervisor.util.TransactionDumper;
import com.example.m1kes.parkingdemo.printer.PrinterDevices;
import com.example.m1kes.parkingdemo.printer.models.MakeFineReceipt;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
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

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter.updateShiftPrepaidAmountCollected;
import static com.example.m1kes.parkingdemo.util.DBUtils.getDateParts;
import static com.example.m1kes.parkingdemo.util.GeneralUtils.cleanRawVehicleReg;


public class ArrearLogin extends AppCompatActivity implements View.OnClickListener{


    EditText Login_editAmountDue,Login_editBayNumber,Login_editDuration;
    Button btnLoginVehicle,Login_btnCancel;
    private TextView txtLoginCurrentUserBalance,Login_editEntryTime,Login_editRegNumber,Login_lastLogged,Login_txtCurrentUserBalanceStatus;
    private Context context;

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
        setContentView(R.layout.activity_arrear_login);
        StartupUtils.setupActionBar("Login Vehicle",this);
        context = ArrearLogin.this;



        initGui();


        if (getUserBalanceInfo()) {

            if (userVehicleReg != null) {
                Login_editRegNumber.setText(userVehicleReg);
            }

            if (lastLogged != null) {
                Login_lastLogged.setText("" + lastLogged);
            } else {
                Login_lastLogged.setTextColor(getResources().getColor(R.color.red));
                Login_lastLogged.setText("Offline Unavailable");
                isOffline = true;
            }

            if (currentUserBalance != 198262) {

                if (currentUserBalance > 0) {
                    txtLoginCurrentUserBalance.setText("$" + df.format(currentUserBalance));
                    txtLoginCurrentUserBalance.setTextColor(getResources().getColor(R.color.red));
                } else if (currentUserBalance == 0) {
                    Login_txtCurrentUserBalanceStatus.setText("Balance");
                    txtLoginCurrentUserBalance.setText("$" + df.format(Math.abs(currentUserBalance)));
                    txtLoginCurrentUserBalance.setTextColor(getResources().getColor(R.color.green));
                } else {
                    Login_txtCurrentUserBalanceStatus.setText("Prepaid Balance");
                    txtLoginCurrentUserBalance.setText("$" + df.format(Math.abs(currentUserBalance)));
                    txtLoginCurrentUserBalance.setTextColor(getResources().getColor(R.color.green));
                }

            } else {

                if (!isNetworkAvailable(context)) {
                    //If there is no network
                    String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                    double localBalance = TransactionAdapter.getLocalUserTransactionBalance(shiftID, userVehicleReg, context);
                    currentUserBalance = localBalance;
                } else {
                    currentUserBalance = 0;
                }

                if (currentUserBalance < 0) {
                    txtLoginCurrentUserBalance.setTextColor(getResources().getColor(R.color.green));
                    txtLoginCurrentUserBalance.setText("Offline Balance: $" + df.format(Math.abs(currentUserBalance)));
                } else if (currentUserBalance == 0) {
                    txtLoginCurrentUserBalance.setText("Offline Balance: $" + df.format(Math.abs(currentUserBalance)));
                    txtLoginCurrentUserBalance.setTextColor(getResources().getColor(R.color.red));
                } else {
                    txtLoginCurrentUserBalance.setTextColor(getResources().getColor(R.color.red));
                    txtLoginCurrentUserBalance.setText("Offline Balance: $" + df.format(currentUserBalance));
                }


            }
        }

    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void initGui(){

        //Edit texts
        Login_editRegNumber = (TextView) findViewById(R.id.Login_editRegNumber);
        Login_txtCurrentUserBalanceStatus = (TextView)findViewById(R.id.Login_txtCurrentUserBalanceStatus);

        Login_editAmountDue = (EditText) findViewById(R.id.Login_editAmountDue);
        Login_editAmountDue.setEnabled(false);
        Login_editBayNumber = (EditText)findViewById(R.id.Login_editBayNumber);
        Login_editDuration = (EditText)findViewById(R.id.Login_editDuration);
        //buttons
        btnLoginVehicle = (Button) findViewById(R.id.btnLoginVehicleArrear);
        Login_btnCancel = (Button) findViewById(R.id.btnCancelArrear);

        //Textviews
        txtLoginCurrentUserBalance = (TextView)findViewById(R.id.txtLoginCurrentUserBalance);
        Login_editEntryTime = (TextView) findViewById(R.id.Login_editEntryTime);
        Login_lastLogged = (TextView)findViewById(R.id.Login_lastLogged);

        String tymStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(Calendar.getInstance().getTime());
        Login_editEntryTime.setText(tymStamp);

        Login_editDuration.setEnabled(false);


        String username = UserAdapter.getLoggedInUser(context);
        String precinctID = String.valueOf(ShiftsAdapter.getPrecinctIDForUser(username,context));
        double tarriff = PrecinctAdapter.getPrecinctTarrif(precinctID,context);
        double amountDue = tarriff * (double)1;

        Login_editDuration.setText("1");
        Login_editAmountDue.setText(String.valueOf(amountDue));

        btnLoginVehicle.setOnClickListener(this);
        Login_btnCancel.setOnClickListener(this);

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
            case R.id.btnCancelArrear:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.btnLoginVehicleArrear:

                if(validate(new EditText[]{Login_editDuration, Login_editAmountDue})) {

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
                                    mainpulateApiCalls(Login_editAmountDue);

                                }
                            });
                    alertDialog.show();
                }

                break;
            /**Buttons end:**/
            default:
        }


    }

    private boolean validate(EditText[] fields){
        for(int i=0; i<fields.length; i++){
            EditText currentField=fields[i];
            if(currentField.getText().toString().length()<=0){
                currentField.setError("Required Field");
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        if(BluetoothAdapter.getDefaultAdapter()!=null&&BluetoothAdapter.getDefaultAdapter().isEnabled()){
            BluetoothAdapter.getDefaultAdapter().disable();
        }
        super.onDestroy();
    }

    private void mainpulateApiCalls(EditText login_editAmountDue){

        double amountDue;


        //Making Fine
        if (login_editAmountDue.getText().toString() != "" && login_editAmountDue.getText().length() > 0) {
            amountDue = Double.parseDouble(login_editAmountDue.getText().toString());
        } else {
            amountDue = 0;
        }


        boolean isFine = false;

        Map<String,Transaction> transactionMap = new HashMap<>();


        if(amountDue>0){
            //Initiate Transaction for MakeFine
            Transaction transaction = makeFineTransaction();
            isFine = true;
            transactionMap.put("MakeFine",transaction);

        }


       if(isFine){
            //if no payment was made. PRINT

            transactionMap.get(TransactionType.TYPE_FINE).setIs_Paid(false);
            transactionMap.get(TransactionType.TYPE_FINE).setPaymentRefNo("0");

            //This means that they have enough balance to Set it as a prepayment because they didnt pay anything
            System.out.println("Checking if has enough to charge as Prepayment Current User Balance: "+currentUserBalance);
            if(currentUserBalance<0){
                Transaction transaction = transactionMap.get(TransactionType.TYPE_FINE);
                transaction.setIs_Prepayment(true);
                System.out.println("Set is prepayment = TRUE");
                TransactionAdapter.updateTransactionDetails(transaction,context);
                System.out.println("This transaction was set to Prepayment : "+transaction);
            }

               MakeFineTransaction makeFineTransaction = new MakeFineTransaction(context);
               makeFineTransaction.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,transactionMap.get("MakeFine"));

            startFinePrint(transactionMap.get("MakeFine"));
        }

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
        transaction.setIs_Paid(false);

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
