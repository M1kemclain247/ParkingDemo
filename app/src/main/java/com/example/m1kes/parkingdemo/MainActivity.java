package com.example.m1kes.parkingdemo;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.adapters.MenuAdapter;
import com.example.m1kes.parkingdemo.callbacks.UpdateResourceResponse;
import com.example.m1kes.parkingdemo.callbacks.VehicleFoundResponse;
import com.example.m1kes.parkingdemo.fragments.dialogs.AuthorizeUser;
import com.example.m1kes.parkingdemo.fragments.dialogs.PopUpCashUp;
import com.example.m1kes.parkingdemo.fragments.dialogs.VehicleRegDialog;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.models.User;
import com.example.m1kes.parkingdemo.modules.supervisor.AdvancedMainActivity;
import com.example.m1kes.parkingdemo.modules.supervisor.util.SupervisorDataManager;
import com.example.m1kes.parkingdemo.printer.PrinterDevices;
import com.example.m1kes.parkingdemo.printer.models.Arrear;
import com.example.m1kes.parkingdemo.printer.models.CashupReceipt;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.settings.activities.DeviceSettingsManager;
import com.example.m1kes.parkingdemo.settings.manager.ApiManager;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrecinctAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrintJobAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.tasks.BalanceEnquiryTask;
import com.example.m1kes.parkingdemo.tasks.SyncCashup;
import com.example.m1kes.parkingdemo.tasks.DumpAllUnsyncedTransactions;
import com.example.m1kes.parkingdemo.tasks.LogoutTask;
import com.example.m1kes.parkingdemo.tasks.UpdateTask;
import com.example.m1kes.parkingdemo.transactions.models.CashupTransaction;
import com.example.m1kes.parkingdemo.util.GeneralUtils;
import com.example.m1kes.parkingdemo.util.ObjectParser;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static com.example.m1kes.parkingdemo.util.DBUtils.getDateParts;
import static com.example.m1kes.parkingdemo.util.GeneralUtils.cleanRawVehicleReg;

public class MainActivity extends AppCompatActivity implements AuthorizeUser.AuthorizeUserDialogListener,PopUpCashUp.PopUpCashUpDailogListener,VehicleRegDialog.VehicleRegDialogListener {

    Integer imageIds[] = {R.drawable.ic_car_white_48dp,R.drawable.ic_car_white_48dp,R.drawable.ic_coin_white_48dp,R.drawable.ic_magnify_white_48dp,
            R.drawable.ic_alert_circle_outline_white_48dp,R.drawable.ic_logout_white_48dp};
    GridView gridView;
        String []keyWords ={"Arrear Login","Cash Login","Arrear Payment","Search Car","Cash Up","Logout"};
    private Context context = this;
    ProgressDialog progressDialog;
    private RelativeLayout rootView;
    private CashupTransaction cashupTransaction;
    private int cashupPrintCount = 0;
    TextView txtVersionNoMain,mCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        setupActionBar("City Marshall");



        txtVersionNoMain = (TextView)findViewById(R.id.txtVersionNoMain);

        String versionName = BuildConfig.VERSION_NAME;
        txtVersionNoMain.setText("Version: "+versionName);

        rootView = (RelativeLayout)findViewById(R.id.activity_main_root);
        gridView = (GridView)findViewById(R.id.gridview);
        gridView.setAdapter(new MenuAdapter(MainActivity.this,imageIds,keyWords));
        gridView.setNumColumns(2);
        gridView.setPadding(0,0,0,0);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                switch (position){

                    case 0://Arrear Login
                        showEnterVehicleRegDialog(position);
                        break;
                    case 1://Cash Login
                        showEnterVehicleRegDialog(position);
                        break;
                    case 2: //Make Payment
                        showEnterVehicleRegDialog(position);
                        break;
                    case 3: //start Search Car
                        startActivity(new Intent(context,SearchVehicle.class));
                        break;
                    case 4://Start logout
//                        startActivity(new Intent(context,MySchedule.class));
                         showSecureDialog(AuthorizeUser.MODE_CASHUP);
                        break;
                    case 5: //Start Fine Payment
                        showConfirmLogoutAction();
                        break;
                    case 6: //Start My Schedule

                        break;
                    default:

                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.action_print);
        MenuItemCompat.setActionView(item, R.layout.print_badge_layout);
        RelativeLayout notifCount = (RelativeLayout) MenuItemCompat.getActionView(item);
        ImageView imgbtnPrinter  = (ImageView)notifCount.findViewById(R.id.imgbtnPrinter);
        imgbtnPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,ViewPrinterJobs.class));
            }
        });
        mCounter = (TextView) notifCount.findViewById(R.id.counter);
        List<PrinterJob> printerJobs = PrintJobAdapter.getAllPrintJobs(PrinterJob.STATUS_PENDING,context);

        if(!printerJobs.isEmpty()){
            mCounter.setText("+"+Integer.toString(printerJobs.size()));
        }else{
            mCounter.setVisibility(View.GONE);
        }

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            return true;
        }else if(id == R.id.action_spot_check){
            showSecureDialog(AuthorizeUser.MODE_SPOT_CHECK);
            //startSyncTask();
        }else if(id == R.id.action_sync){
            startActivity(new Intent(this,SyncTransactions.class));
            //startSyncTask();
        }else if(id == R.id.action_print){
            startActivity(new Intent(this,ViewPrinterJobs.class));
            return true;
        }else if(id == R.id.action_loggedin_vehicles){
            startActivity(new Intent(this,CheckVehiclesLoggedIn.class));
            return true;
        }else if(id == R.id.action_about_us){
            showAboutUsDialog();
            return true;
        }else if(id == R.id.action_register_printer_temp){

            String username = UserAdapter.getLoggedInUser(context);
            String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
            Intent i = new Intent(context,RegisterAPrinter.class);
            if(shiftID!=null){
                //Continue active shift
                i.putExtra("ActiveShift",true);
            }else{
                i.putExtra("ActiveShift",false);
            }
            startActivity(i);
            return true;
        }else if(id == R.id.action_device_settings){
            startActivity(new Intent(context, DeviceSettingsManager.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    UpdateResourceResponse updateResourceResponse = new UpdateResourceResponse() {
        @Override
        public void updateComplete() {
            showSnackbarSuccess(rootView,"Successfully Synced",context);
        }

        @Override
        public void updateFailed() {
            showSnackbarError(rootView,"Failed to Sync",context);
        }

        @Override
        public void onNoConnection() {
            showSnackbarError(rootView,"No Internet Connection Try Again later",context);
        }

        @Override
        public void onProgressUpdated(String message) {

        }
    };

    public void showSnackbarError(RelativeLayout rootView, String message, final Context context){

        Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new UpdateTask(context,updateResourceResponse).execute(ApiManager.getServerType(context));
                    }
                })
                .setActionTextColor(context.getResources().getColor(android.R.color.holo_red_light ))
                .show();
    }

    public void showSnackbarSuccess(RelativeLayout rootView, String message, final Context context){

        Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                .setActionTextColor(context.getResources().getColor(android.R.color.holo_green_light ))
                .show();
    }


    private void showAboutUsDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("About Us");

        String versionName = BuildConfig.VERSION_NAME;
        alertDialog.setMessage("City Marshall Application. \n\n"+
                "Version: "+versionName+" \n"+
                "Release Date: 2016-12-19 \n"+
                "Checksum: HA2882SJSH22H1 \n"+
                " \n \nCreated by : SOJ (School of Java)");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }

    private void showConfirmLogoutAction() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Confirm Logout");
        alertDialog.setMessage("Are you sure you want to logout?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Logout",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        new LogoutTask(MainActivity.this).execute();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();

    }


    public void showSecureDialog(String MODE) {
        if(UserAdapter.getLoggedInUser(context)!=null){
            DialogFragment dialog = new AuthorizeUser();
            Bundle args = new Bundle();
            args.putString("mode",MODE);
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "AuthorizeUserFragment");
        }else{
            Toast.makeText(context,"Not Logged In Cannot Cashup!",Toast.LENGTH_SHORT).show();
        }
    }

    public void showCashupDialog() {
        PopUpCashUp dialog = new PopUpCashUp();
        dialog.show(getFragmentManager(), "PopUpCashup");
    }



    public void showEnterVehicleRegDialog(int position) {
        VehicleRegDialog dialog = new VehicleRegDialog();
        Bundle b = new Bundle();
        b.putInt("position",position);
        dialog.setArguments(b);
        dialog.show(getFragmentManager(), "PopUpCashup");
    }




    @Override
    public void onDialogPositiveClick(DialogFragment dialog, EditText editPassword,EditText editUsername,String MODE) {
        //Authorize User
        String password =  editPassword.getText().toString().trim();
        String username = UserAdapter.getLoggedInUser(context);
        String supervisorUsername =  editUsername.getText().toString();

        if(MODE.equals(AuthorizeUser.MODE_CASHUP)) {

            boolean authorized = UserAdapter.loginUser(new User(username, password, false), context);
            if (authorized) {
                dialog.dismiss();
                showCashupDialog();
            } else {
                editPassword.setError("Invalid Password!");
            }
        }else if(MODE.equals(AuthorizeUser.MODE_SPOT_CHECK)){
            //TODO ADD REAL CODE FOR PERMS CHECK

            if(username.equalsIgnoreCase("michael")&&password.equals("Password")) {

                SupervisorDataManager.setActiveSupervisor(supervisorUsername,context);
                startActivity(new Intent(context, AdvancedMainActivity.class));

            }else if(UserAdapter.loginSupervisor(new User(editUsername.getText().toString(),password,false),context)) {

                //Set Active Supervisor
                SupervisorDataManager.setActiveSupervisor(supervisorUsername,context);
                startActivity(new Intent(context, AdvancedMainActivity.class));

            }else{
                Toast.makeText(context,"You are not Authorized to do this!",Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Cancel dialog
        dialog.dismiss();
    }



    private void setupActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle(title);
        }
    }




    @Override
    public void onBackPressed() {
        Toast.makeText(this,"You must logout First!",Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onCashupClick(DialogFragment dialog, EditText editCashupAmount,EditText editCashupAmountConfirm) {

        String username = UserAdapter.getLoggedInUser(context);
        String countedAmount =  editCashupAmount.getText().toString();
        String countedAmountConfirm =  editCashupAmountConfirm.getText().toString();
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        System.out.println("Current Active Shift ID:"+shiftID+"v");


        long cashupTime = Long.valueOf(GeneralUtils.getCurrentDateTime());

        if(shiftID!=null) {
            System.out.println("Trying to get the current user ShiftID of : " + shiftID);
        }else{
            System.out.println("Shift ID is null for the current user");
        }



        if(countedAmount.length()>=1&&countedAmountConfirm.length()>=1){

            if(countedAmount.equalsIgnoreCase(countedAmountConfirm)) {

                        //Local Cashup
                        Shift shift = ShiftsAdapter.getShiftForUser(shiftID,context);
                        //Set details for cashup
                        double declaredAmount = Double.valueOf(countedAmount);
                        shift.setTotal_declaredAmount(declaredAmount);
                        shift.setEnd_time(cashupTime);
                        ShiftsAdapter.endShift(shift,context);


                        //generate A Cashup Transaction
                        CashupTransaction transaction = generateCashupTransaction(username, context, countedAmount);
                        //Upload all unsynced transactions
                        List<Transaction> unsyncedTransactions = TransactionAdapter.getAllUnSyncedTransactionsForShift(shiftID, context);
                        if (!unsyncedTransactions.isEmpty()) {
                            DumpAllUnsyncedTransactions task = new DumpAllUnsyncedTransactions(context);
                            task.execute(unsyncedTransactions);
                        }else{
                            //Sync Cash up if the transactions are synced
                            new SyncCashup(context).execute(shift);
                        }

                        //TODO UPDATE RECORD TO SYNCED ON RESPONSE
                        Toast.makeText(context,"Cashed Up!",Toast.LENGTH_SHORT).show();
                        new LogoutTask(context).execute();

                        cashupTransaction = transaction;
                        cashupTransaction.setShiftID(shiftID);
                        ShiftDataManager.setCurrentActiveShiftID(null,context);
                        startCashupPrint(cashupTransaction);

            }else{
                editCashupAmount.setError("Match Fields!");
                editCashupAmountConfirm.setError("Match Fields!");
            }
        }else{
            editCashupAmount.setError("Fields Should both be entered!");

        }

    }

    private CashupTransaction generateCashupTransaction(String username,Context context,String countedAmount) {

        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        Shift shift = ShiftsAdapter.getShiftForUser(shiftID,context);

        CashupTransaction cashupTransaction = new CashupTransaction();


        //Start Setting the values
        User user =  UserAdapter.findUserWithName(username,context);
        String fn =  user.getFirstname().substring(0,2);
        String marshalDetails = ""+user.getEmpNo()+" : "+user.getSurname() +" "+fn;
        String supervisor = "None";

        String shiftStartTime  = String.valueOf(shift.getStart_time());
        //Format the start Time
        SimpleDateFormat pf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyyMMddHHmmss");
        String formattedDate = null;
        Date startDate  = null;
        try {
            startDate = df2.parse(shiftStartTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        formattedDate = pf.format(startDate);
        String site = "City Parking (PVT)";
        String zone = shift.getPrecinctZoneName();


        int precinctID = shift.getPrecinctID();

        String dateNos = shiftStartTime.substring(2,4) +""+shiftStartTime.substring(4,8);
        String receciptNo = ShiftDataManager.getDefaultTerminalID(context)+""+dateNos+precinctID;
        String start_time = formattedDate;
        String end_time = pf.format(new Date());


        //TODO Calculate Duration

        long diff = startDate.getTime() - new Date().getTime();//as given

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);

        String duration = parseDuration(diffHours,diffMinutes,diffSeconds);


        cashupTransaction.setShiftID(shiftID);
        cashupTransaction.setSiteName(site);
        cashupTransaction.setZoneName(zone);
        cashupTransaction.setRecieptNumber(receciptNo);
        cashupTransaction.setMarshalDetails(marshalDetails);
        cashupTransaction.setSupervisor(supervisor);
        cashupTransaction.setShiftStartTime(start_time);
        cashupTransaction.setShiftEndTime(end_time);
        cashupTransaction.setShiftDuration(duration);
        cashupTransaction.setExpectedAmount(shift.getCash_collected());
        cashupTransaction.setPrepaidAmount(shift.getTotal_prepaid());
        cashupTransaction.setCountedAmount(Double.valueOf(countedAmount));
        cashupTransaction.setShortAmount(shift.getTotal_collected(),Double.valueOf(countedAmount));
        cashupTransaction.setEpaymentAmount(shift.getePayments());
        cashupTransaction.setTotalAmount(shift.getTotal_collected());


        return cashupTransaction;

    }

    @Override
    public void onCancelCashup(DialogFragment dialog) {

        dialog.dismiss();
        Toast.makeText(context,"Cancelled Cashup!",Toast.LENGTH_SHORT).show();
    }

    private String parseDuration(long diffHours,long diffMinutes, long diffSeconds){

        String duration ="";

        if(diffHours<0){
            duration += Math.abs(diffHours);
        }else{
            duration += diffHours;
        }
        duration += " hrs ";

        if(diffMinutes<0){
            duration += Math.abs(diffMinutes);
        }else {
            duration += diffMinutes;
        }

        duration += " m ";

        if(diffSeconds<0){
            duration += Math.abs(diffSeconds);
        }else {
            duration += diffSeconds;
        }
        duration += " s ";

        return duration;
    }


    private void startCashupPrint(CashupTransaction transaction){

        BluetoothAdapter.getDefaultAdapter().enable();
        PrinterJob printerJob = new PrinterJob();
        Receipt receipt = new CashupReceipt(transaction);
        printerJob.setPrintAttemptTime(new Date());//id
        printerJob.setShiftId(transaction.getShiftID());
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


    @Override
    public void vehicleRegPositiveClick(final DialogFragment dialog, EditText editText, final int position) {




        String rawVehicleReg = editText.getText().toString();
        final String vehicleReg = cleanRawVehicleReg(rawVehicleReg);

        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Searching for vehicle "+vehicleReg+" ....");
        progressDialog.show();

        BalanceEnquiryTask task = new BalanceEnquiryTask(context, new VehicleFoundResponse() {
            @Override
            public void vehicleFound(String response) {






                 if(response.contains("lastlogged")){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String balance = jsonObject.getString("Balance");
                        String lastlogged = jsonObject.getString("lastlogged");
                        System.out.println("Recieved Vehicle reg: "+vehicleReg+" Bal: $"+balance);
                        Toast.makeText(context,"Found Vehicle: "+vehicleReg,Toast.LENGTH_LONG).show();

                        //TODO Add validation of Local Date Time for No Relogging of vehicles

                        Calendar cal = Calendar.getInstance();
                        String[] parts= getDateParts(lastlogged);
                        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                        Date lastLoggedDate = cal.getTime();


                        //Dismiss Dialog
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                            progressDialog= null;
                        }

                        //TODO UPDATE LOCAL USERBALANCE
                        System.out.println("Setting LocalUser Balance: "+balance);
                        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                        TransactionAdapter.setLocalUserBalance(shiftID,vehicleReg,Double.parseDouble(balance),context);

                        System.out.println("***************************************************************");
                        System.out.println("Last Logged: "+lastLoggedDate +" Current Date: "+new Date());

                        System.out.println("Last Logged: "+lastLoggedDate.getTime() +" Current Date: "+new Date().getTime());
                        System.out.println("***************************************************************");

                        if(position==1) {

                            if (new Date().before(lastLoggedDate)) {
                                long diff = lastLoggedDate.getTime() - new Date().getTime();//as given

                                long diffSeconds = diff / 1000 % 60;
                                long diffMinutes = diff / (60 * 1000) % 60;
                                long diffHours = diff / (60 * 60 * 1000);

                                String duration = parseDuration(diffHours, diffMinutes, diffSeconds);

                                double arrearsAmount = Double.parseDouble(balance);

                                if (arrearsAmount > 0) {
                                    showVehicleAlreadyLogggedIn(duration, arrearsAmount, vehicleReg, lastlogged);
                                } else {
                                    showVehicleAlreadyLogggedInWithoutPayment(duration);
                                }


                            }else{
                                //Everything is ok
                                Intent loginVehicle = new Intent(context, VehicleLogin.class);
                                loginVehicle.putExtra("Balance", balance);
                                loginVehicle.putExtra("VehicleReg", vehicleReg);
                                loginVehicle.putExtra("LastLogged", lastlogged);
                                startActivity(loginVehicle);
                            }

                        }else if(position ==0){

                            if (new Date().before(lastLoggedDate)) {
                                long diff = lastLoggedDate.getTime() - new Date().getTime();//as given

                                long diffSeconds = diff / 1000 % 60;
                                long diffMinutes = diff / (60 * 1000) % 60;
                                long diffHours = diff / (60 * 60 * 1000);

                                String duration = parseDuration(diffHours, diffMinutes, diffSeconds);

                                double arrearsAmount = Double.parseDouble(balance);

                                if (arrearsAmount > 0) {
                                    showVehicleAlreadyLogggedIn(duration, arrearsAmount, vehicleReg, lastlogged);
                                } else {
                                    showVehicleAlreadyLogggedInWithoutPayment(duration);
                                }




                            }else{
                                //Everything is ok
                                Intent loginVehicle = new Intent(context, ArrearLogin.class);
                                loginVehicle.putExtra("Balance", balance);
                                loginVehicle.putExtra("VehicleReg", vehicleReg);
                                loginVehicle.putExtra("LastLogged", lastlogged);
                                startActivity(loginVehicle);
                            }


                        }else{

                            Intent fine = new Intent(context, ArrearsPayment.class);
                            fine.putExtra("Balance", balance);
                            fine.putExtra("VehicleReg", vehicleReg);
                            fine.putExtra("LastLogged", lastlogged);
                            startActivity(fine);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else{

                    progressDialog.dismiss();
                    Toast.makeText(context,""+response,Toast.LENGTH_SHORT).show();
                }





            }

            @Override
            public void notFound(String response) {

                /**
                 * Start without any extra's in those activities
                 * This is when there is no internet or response is not valid
                 * Basically no balance details willl be shown in the activities
                 * */

                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                    progressDialog= null;
                }

                switch (position) {



                    case 0:
                        //Check locallly if there is a vehicle with the same vehicle reg already logged in
                        long expiryTime1 = TransactionAdapter.isVehicleLoggedIn(vehicleReg, context);
                        if (expiryTime1 != 0) {

                            String strExpiry = "" + expiryTime1;
                            Calendar cal = Calendar.getInstance();
                            String[] parts = getDateParts(strExpiry);
                            cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                            Date lastLoggedDate = cal.getTime();

                            if (new Date().before(lastLoggedDate)) {
                                long diff = lastLoggedDate.getTime() - new Date().getTime();//as given

                                long diffSeconds = diff / 1000 % 60;
                                long diffMinutes = diff / (60 * 1000) % 60;
                                long diffHours = diff / (60 * 60 * 1000);

                                String duration = parseDuration(diffHours, diffMinutes, diffSeconds);

                                String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                                double amount =  TransactionAdapter.getLocalUserTransactionBalance(shiftID,vehicleReg,context);
                                showVehicleAlreadyLogggedIn(duration,amount,vehicleReg);
                            }

                        } else {
                            Intent loginVehicle = new Intent(context, ArrearLogin.class);
                            loginVehicle.putExtra("VehicleReg", vehicleReg);
                            startActivity(loginVehicle);
                        }

                        break;

                    case 1:

                        //Check locallly if there is a vehicle with the same vehicle reg already logged in
                        long expiryTime = TransactionAdapter.isVehicleLoggedIn(vehicleReg, context);
                        if (expiryTime != 0) {

                            String strExpiry = "" + expiryTime;
                            Calendar cal = Calendar.getInstance();
                            String[] parts = getDateParts(strExpiry);
                            cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                            Date lastLoggedDate = cal.getTime();

                            if (new Date().before(lastLoggedDate)) {
                                long diff = lastLoggedDate.getTime() - new Date().getTime();//as given

                                long diffSeconds = diff / 1000 % 60;
                                long diffMinutes = diff / (60 * 1000) % 60;
                                long diffHours = diff / (60 * 60 * 1000);

                                String duration = parseDuration(diffHours, diffMinutes, diffSeconds);

                                String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                                double amount =  TransactionAdapter.getLocalUserTransactionBalance(shiftID,vehicleReg,context);
                                showVehicleAlreadyLogggedIn(duration,amount,vehicleReg);
                            }

                        } else {
                            Intent loginVehicle = new Intent(context, VehicleLogin.class);
                            loginVehicle.putExtra("VehicleReg", vehicleReg);
                            startActivity(loginVehicle);
                        }


                        break;


                    case 2:
                        Intent fine = new Intent(context, ArrearsPayment.class);
                        fine.putExtra("VehicleReg", vehicleReg);
                        startActivity(fine);
                        break;
                    default:
                }




            }
        });

        task.execute(vehicleReg);
        dialog.dismiss();
    }

    @Override
    public void vehicleRegNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }



    private void showVehicleAlreadyLogggedIn(String timeRemaining, final double arrearsAmount,final String regnumber) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Vehicle Already Logged In");
        alertDialog.setMessage("This vehicle is already logged In!\n\nTime Remaining: "+timeRemaining);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Pay Arrears", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(context,ArrearsPayment.class);
                i.putExtra("Balance",String.valueOf(arrearsAmount));
                i.putExtra("VehicleReg",regnumber);
                startActivity(i);
            }
        });
        alertDialog.show();
    }

    private void showVehicleAlreadyLogggedInWithoutPayment(String timeRemaining) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Vehicle Already Logged In");
        alertDialog.setMessage("This vehicle is already logged In!\n\nTime Remaining: "+timeRemaining);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


    private void showVehicleAlreadyLogggedIn(String timeRemaining, final double arrearsAmount,final String vehicleReg,final String lastlogged) {

        DecimalFormat df = new DecimalFormat("0.00");

        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Vehicle Already Logged In");
        alertDialog.setMessage("This vehicle is already logged In!\n\nArrears : $"+df.format(arrearsAmount)+" \n\nTime Remaining: "+timeRemaining);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Pay Arrears", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(context,ArrearsPayment.class);
                i.putExtra("Balance",String.valueOf(arrearsAmount));
                i.putExtra("VehicleReg",vehicleReg);
                i.putExtra("LastLogged",lastlogged);
                startActivity(i);
            }
        });
        alertDialog.show();
    }



}
