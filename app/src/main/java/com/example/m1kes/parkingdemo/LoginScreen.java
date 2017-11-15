package com.example.m1kes.parkingdemo;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.callbacks.LoginUserResponse;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.ShiftUserData;
import com.example.m1kes.parkingdemo.printer.PrinterDevices;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.printer.models.ReprintReceipt;
import com.example.m1kes.parkingdemo.settings.activities.DeviceSettingsManager;
import com.example.m1kes.parkingdemo.settings.manager.SecureAccountManager;
import com.example.m1kes.parkingdemo.settings.models.Account;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrintJobAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrinterAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserShiftDataAdapter;
import com.example.m1kes.parkingdemo.tasks.LoginUserTask;
import com.example.m1kes.parkingdemo.util.GeneralUtils;
import com.example.m1kes.parkingdemo.util.ObjectParser;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;
import com.example.m1kes.parkingdemo.util.ShiftValidator;
import com.example.m1kes.parkingdemo.util.StartupUtils;
import com.example.m1kes.parkingdemo.util.ToFile;
import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.pass.Spass;
import com.samsung.android.sdk.pass.SpassFingerprint;
import com.example.m1kes.parkingdemo.services.MyWebService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



public class LoginScreen extends AppCompatActivity {


    private static final String LOG_TAG = "AppUpgrade";
    private MyWebReceiver receiver;



    private DownloadManager downloadManager;
    private long downloadReference;

    final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    final String TAG = "Permissions";



    EditText input_username,input_password;
    TextView txtVersionNo,mCounter;
    AppCompatButton btn_login;
    ImageView logoIcon,imgFingerPrint;
    private Context context;
    private ProgressDialog progressDialog;
    private int versionCode = 0;
    String appURI = "";
    Spass mSpass = null;
    private boolean isFeatureEnabled_fingerprint = false;
    private SpassFingerprint mSpassFingerprint;
    private static final int LOGIN_USER = 101;
    private static final int LINK_NEW_ACCOUNT = 103;
    private int ATTEMPT_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_screen);

        context = LoginScreen.this;

        initGUI();
        StartupUtils.setupActionBar("Login",this);
        setupVersionSigning();
        addByPassForDevs();
        setupFingerPrintSupport();


        setupAppUpdates();


    }

    private void setupAppUpdates(){

        //Broadcast receiver for our Web Request
        IntentFilter filter = new IntentFilter(MyWebReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new MyWebReceiver();
        registerReceiver(receiver, filter);

        //Broadcast receiver for the download manager
        filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

        System.out.println("Checking network availability....");
        //check of internet is available before making a web service request
        if (isNetworkAvailable(this)) {
            System.out.println("Starting Service!");
            Intent msgIntent = new Intent(this, MyWebService.class);
            startService(msgIntent);
        }

    }

    private void setupFingerPrintSupport(){


        boolean supported = false;

        try {
                mSpass = new Spass();
                mSpass.initialize(context);
            isFeatureEnabled_fingerprint = mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT);

            if (isFeatureEnabled_fingerprint) {
                mSpassFingerprint = new SpassFingerprint(context);
                supported = true;
                log("Fingerprint Service is supported in the device.");
                log("SDK version : " + mSpass.getVersionName());
                SecureAccountManager.setFingerPrintSupported(context);
                if(SecureAccountManager.getAccountLinked(context)!=null) {
                    //If there is an account linked to fingerprint then attempt to log them in
                }else{
                    System.out.println("Fingerprint is supported but not setup yet Login for first time");
                }
            } else {
                log("Fingerprint Service is not supported in the device.");
                return;
            }
            } catch (SsdkUnsupportedException e) {
                log("Exception: " + e);
            } catch (UnsupportedOperationException e) {
                log("Fingerprint Service is not supported in the device");
            }

        if(supported){
            imgFingerPrint.setVisibility(View.VISIBLE);
        }else{
            imgFingerPrint.setVisibility(View.GONE);
        }


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

    private void initGUI(){

        input_username = (EditText)findViewById(R.id.input_username);
        input_password = (EditText)findViewById(R.id.input_password);
        btn_login = (AppCompatButton)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate(new EditText[]{input_password,input_username})){
                    String username = input_username.getText().toString();
                    String password = input_password.getText().toString();

                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Loggging in...");
                    progressDialog.show();

                    new LoginUserTask(context,response).execute(username,password);


                }
            }
        });
        logoIcon = (ImageView)findViewById(R.id.logoIcon);
        imgFingerPrint = (ImageView)findViewById(R.id.imgFingerPrint);

        if(SecureAccountManager.getFingerPrintSupported(context)&&SecureAccountManager.getAccountLinked(context)!=null){
            imgFingerPrint.setVisibility(View.VISIBLE);


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
                    imgFingerPrint.setAnimation(mAnimation);
                    mAnimation.start();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            imgFingerPrint.setAnimation(mAnimation);

            imgFingerPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startIdentifyDialog(true,LOGIN_USER);
                }
            });
        }else{

            imgFingerPrint.setVisibility(View.GONE);
        }


        startAnimation(logoIcon);


        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //get the app version Code for checking
        if(pInfo!=null){
            versionCode = pInfo.versionCode;
        }



    }


    private void addByPassForDevs(){

        //If there is already logged in then bypass the login screen
         String username = UserAdapter.getLoggedInUser(context);
//         System.out.println(username);
         if(username!=null)
         System.out.println("Username: "+username);
         if(username!=null&&username.equalsIgnoreCase("michael")){
             System.out.println("Bypassing login for michael since already logged in!");
             startActivity(new Intent(context,MainActivity.class));
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


    private void startAnimation(final ImageView l){

        final ScaleAnimation growAnim = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        final ScaleAnimation shrinkAnim = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        growAnim.setDuration(3000);
        shrinkAnim.setDuration(3000);

        l.setAnimation(growAnim);
        growAnim.start();

        growAnim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation){}

            @Override
            public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationEnd(Animation animation)
            {
                l.setAnimation(shrinkAnim);
                shrinkAnim.start();
            }
        });
        shrinkAnim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation){}

            @Override
            public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationEnd(Animation animation)
            {
                l.setAnimation(growAnim);
                growAnim.start();
            }
        });

    }


    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Login First!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);

        MenuItem item = menu.findItem(R.id.action_printers);
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
        if(id== android.R.id.home){
            this.finish();
            startActivity(new Intent(this,SplashScreen.class));
            return true;
        }else if(id == R.id.action_device_settings){
            startActivity(new Intent(context, DeviceSettingsManager.class));
            return true;
        } else if(id == R.id.action_printers){
            startActivity(new Intent(this,ViewPrinterJobs.class));
            return true;
        } else if(id == R.id.action_sync_shifts){
            startActivity(new Intent(this,SyncShifts.class));
            return true;
        }else if(id == R.id.action_reprint_cashup){

            String receiptData = ToFile.readFromFile(context);
            if(receiptData!=null&&receiptData.length()>40) {
                ReprintReceipt reprintReceipt = new ReprintReceipt(receiptData);
                PrinterJob printerJob = new PrinterJob();

                Receipt receipt = (Receipt) reprintReceipt;
                printerJob.setPrintAttemptTime(new Date());//id
                printerJob.setPrintData(receipt.getPrintString());
                printerJob.setPrintType(receipt.getPrintTypeAll());



                Intent i = new Intent(this, PrinterDevices.class);
                Bundle b = new Bundle();
                try {
                    b.putByteArray("printerJob", ObjectParser.object2Bytes(printerJob));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i.putExtras(b);
                startActivity(i);
            }else{
               Toast.makeText(context,"Error with cashup Reciept!",Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return false;
    }







    LoginUserResponse response = new LoginUserResponse() {
        @Override
        public void onLoginSuccess(final String username) {

            if(progressDialog!=null)
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog=null;

            }

            System.out.println("Setting user: "+username+" LOGGED In");
            UserAdapter.setUserLoggedIn(username,context);


            if(SecureAccountManager.getAccountLinked(context)==null&&SecureAccountManager.getFingerPrintSupported(context)) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Setup Fingerprint Login");
                alertDialog.setMessage("Would u like to link your account with your fingerprint?\n\n" +
                        "-Faster Login's\n" +
                        "-More Secure");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Need to see if this works
                                dialog.dismiss();
                                startIdentifyDialog(true, LINK_NEW_ACCOUNT);
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //Getting the shiftID for user 0 if no shift exists
                        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                        Shift shift = ShiftsAdapter.getShiftForUser(shiftID,context);

                        List<Printer> pairedPrinters = PrinterAdapter.getPairedPrinters(context);
                        if(pairedPrinters!=null&&pairedPrinters.size()>0) {
                            //Already has a paired printer so no need to register

                            if(shiftID!=null){
                                //There is an active shift
                                if(shift!=null&&shift.getUsername().equalsIgnoreCase(username)){
                                    //There is an active shift for this username
                                    Toast.makeText(context,"Continuing active Shift",Toast.LENGTH_LONG).show();
                                    context.startActivity(new Intent(context, MainActivity.class));
                                }else{
                                    //Active shift is not for this user
                                    Toast.makeText(context,"Logged In Successfully",Toast.LENGTH_LONG).show();
                                    context.startActivity(new Intent(context, ShiftManager.class));
                                }

                            }else{
                                //There is no active shift
                                //Check the date and see if there is already a shift that existed for this same user on the same day
                                //IF NOT THEN CONTINUE TO CREATE A NEW SHIFT
                                //ELSE WE NEED TO SHOW ANOTHER SCREEN TO TELL THEM THAT THEY ALREADY CREATED A SHIFT TODAY AND CANNOT CREATE ANOTHER SHIFT
                                //ADD A BYPASS FOR THE DEVELOPERS AND THE IT STAFF FOR TESTING PURPOSES TO CREATE MULTILE SHIFTS WITHING THE

                                 if(ShiftValidator.checkifShiftIsAlreadyCreated(context,username)){
                                     //Show the Already created Shift Activity
                                     Toast.makeText(context,"Logged In Successfully",Toast.LENGTH_LONG).show();
                                     context.startActivity(new Intent(context, ShiftChecker.class));

                                 }else{
                                     //Continue to Shift Manager

                                     Toast.makeText(context,"Logged In Successfully",Toast.LENGTH_LONG).show();
                                     context.startActivity(new Intent(context, ShiftManager.class));
                                 }





                            }
                        }else{
                            //Doesnt have a paired Printer so register the first one

                            Intent i = new Intent(context,RegisterAPrinter.class);
                            if(shiftID!=null){
                                //Continue active shift
                                i.putExtra("ActiveShift",true);
                            }else{
                                i.putExtra("ActiveShift",false);
                            }
                            i.putExtra("LoggedIn",username);
                            startActivity(i);
                        }

                    }
                });
                alertDialog.show();
            }else{
                //There is a linked account finger support and logged in successfully

                //Getting the shiftID for user 0 if no shift exists
                String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                Shift shift = ShiftsAdapter.getShiftForUser(shiftID,context);

                List<Printer> pairedPrinters = PrinterAdapter.getPairedPrinters(context);
                if(pairedPrinters!=null&&pairedPrinters.size()>0) {
                    //Already has a paired printer so no need to register

                    if(shiftID!=null){
                        //There is an active shift
                        if(shift!=null&&shift.getUsername().equalsIgnoreCase(username)){
                            //There is an active shift for this username
                            Toast.makeText(context,"Continuing active Shift",Toast.LENGTH_LONG).show();
                            context.startActivity(new Intent(context, MainActivity.class));
                        }else{
                            //Active shift is not for this user
                            Toast.makeText(context,"Logged In Successfully",Toast.LENGTH_LONG).show();
                            context.startActivity(new Intent(context, ShiftManager.class));
                        }

                    }else{
                        //There is no active shift
                        Toast.makeText(context,"Logged In Successfully",Toast.LENGTH_LONG).show();
                        context.startActivity(new Intent(context, ShiftManager.class));
                    }
                }else{
                    //Doesnt have a paired Printer so register the first one

                    Intent i = new Intent(context,RegisterAPrinter.class);
                    if(shiftID!=null){
                        //Continue active shift
                        i.putExtra("ActiveShift",true);
                    }else{
                        i.putExtra("ActiveShift",false);
                    }
                    i.putExtra("LoggedIn",username);
                    startActivity(i);
                }
            }






        }

        @Override
        public void onLoginFailed() {

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog=null;
            }

            Toast.makeText(context,"Login Failed!",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onLoginSameDay() {

            if(progressDialog.isShowing()){
                progressDialog.dismiss();
                progressDialog=null;
            }

            Toast.makeText(context,"You already Created A shift today!",Toast.LENGTH_LONG).show();

        }
    };


    private static String getEventStatusName(int eventStatus) {
        switch (eventStatus) {
            case SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS:
                return "STATUS_AUTHENTIFICATION_SUCCESS";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS:
                return "STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS";
            case SpassFingerprint.STATUS_TIMEOUT_FAILED:
                return "STATUS_TIMEOUT";
            case SpassFingerprint.STATUS_SENSOR_FAILED:
                return "STATUS_SENSOR_ERROR";
            case SpassFingerprint.STATUS_USER_CANCELLED:
                return "STATUS_USER_CANCELLED";
            case SpassFingerprint.STATUS_QUALITY_FAILED:
                return "STATUS_QUALITY_FAILED";
            case SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE:
                return "STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE";
            case SpassFingerprint.STATUS_BUTTON_PRESSED:
                return "STATUS_BUTTON_PRESSED";
            case SpassFingerprint.STATUS_OPERATION_DENIED:
                return "STATUS_OPERATION_DENIED";
            case SpassFingerprint.STATUS_AUTHENTIFICATION_FAILED:
            default:
                return "STATUS_AUTHENTIFICATION_FAILED";
        }
    }

    private void startIdentifyDialog(boolean backup,int ATTEMPT_CODE) {
        try {
            if (mSpassFingerprint != null) {
                this.ATTEMPT_CODE = ATTEMPT_CODE;
                mSpassFingerprint.startIdentifyWithDialog(context, mIdentifyListenerDialog, backup);
            }
        } catch (IllegalStateException e) {
            log("Exception: " + e);
        }
    }

    private void log(String text) {
        System.out.println("LOGGING: "+text);
    }



    private SpassFingerprint.IdentifyListener mIdentifyListenerDialog = new SpassFingerprint.IdentifyListener() {
        @Override
        public void onFinished(int eventStatus) {
            log("identify finished : reason =" + getEventStatusName(eventStatus));
            int FingerprintIndex = 0;
            boolean isFailedIdentify = false;

            try {
                FingerprintIndex = mSpassFingerprint.getIdentifiedFingerprintIndex();
            } catch (IllegalStateException ise) {
                log(ise.getMessage());
            }
            if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_SUCCESS) {
                log("onFinished() : Identify authentification Success with FingerprintIndex : " + FingerprintIndex);
                //Login user
                String username = input_username.getText().toString();
                String password = input_password.getText().toString();
                if(ATTEMPT_CODE==LOGIN_USER){
                    Account account = SecureAccountManager.getAccountLinked(context);
                    assert account != null;

                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Loggging in...");
                    progressDialog.show();
                    new LoginUserTask(context,response).execute(account.getUsername(),account.getPassword());

                }else if(ATTEMPT_CODE==LINK_NEW_ACCOUNT){
                    SecureAccountManager.setAccountLinked(new Account(username,password),context);
                    Toast.makeText(context,"Account Linked to FingerPrint",Toast.LENGTH_SHORT).show();
                    //Getting the shiftID for user 0 if no shift exists
                    String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                    Shift shift = ShiftsAdapter.getShiftForUser(shiftID,context);

                    List<Printer> pairedPrinters = PrinterAdapter.getPairedPrinters(context);
                    if(pairedPrinters!=null&&pairedPrinters.size()>0) {
                        //Already has a paired printer so no need to register

                        if(shiftID!=null){
                            //There is an active shift
                            if(shift!=null&&shift.getUsername().equalsIgnoreCase(username)){
                                //There is an active shift for this username
                                Toast.makeText(context,"Continuing active Shift",Toast.LENGTH_LONG).show();
                                context.startActivity(new Intent(context, MainActivity.class));
                            }else{
                                //Active shift is not for this user
                                Toast.makeText(context,"Logged In Successfully",Toast.LENGTH_LONG).show();
                                context.startActivity(new Intent(context, ShiftManager.class));
                            }

                        }else{
                            //There is no active shift
                            Toast.makeText(context,"Logged In Successfully",Toast.LENGTH_LONG).show();
                            context.startActivity(new Intent(context, ShiftManager.class));
                        }
                    }else{
                        //Doesnt have a paired Printer so register the first one

                        Intent i = new Intent(context,RegisterAPrinter.class);
                        if(shiftID!=null){
                            //Continue active shift
                            i.putExtra("ActiveShift",true);
                        }else{
                            i.putExtra("ActiveShift",false);
                        }
                        i.putExtra("LoggedIn",username);
                        startActivity(i);
                    }
                }
            } else if (eventStatus == SpassFingerprint.STATUS_AUTHENTIFICATION_PASSWORD_SUCCESS) {
                log("onFinished() : Password authentification Success");
            } else if (eventStatus == SpassFingerprint.STATUS_USER_CANCELLED
                    || eventStatus == SpassFingerprint.STATUS_USER_CANCELLED_BY_TOUCH_OUTSIDE) {
                log("onFinished() : User cancel this identify.");
            } else if (eventStatus == SpassFingerprint.STATUS_TIMEOUT_FAILED) {
                log("onFinished() : The time for identify is finished.");
            } else if (!mSpass.isFeatureEnabled(Spass.DEVICE_FINGERPRINT_AVAILABLE_PASSWORD)) {
                if (eventStatus == SpassFingerprint.STATUS_BUTTON_PRESSED) {
                    log("onFinished() : User pressed the own button");
                    Toast.makeText(context, "Please connect own Backup Menu", Toast.LENGTH_SHORT).show();
                }
            } else {
                log("onFinished() : Authentification Fail for identify");
                isFailedIdentify = true;
            }

        }

        @Override
        public void onReady() {
            log("identify state is ready");
        }

        @Override
        public void onStarted() {
            log("User touched fingerprint sensor");
        }

        @Override
        public void onCompleted() {
            log("the identify is completed");
        }
    };


    //broadcast receiver to get notification when the web request finishes
    public class MyWebReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE = "com.example.m1kes.appupdater.intent.action.PROCESS_RESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {

            String reponseMessage = intent.getStringExtra(MyWebService.RESPONSE_MESSAGE);
            Log.v(LOG_TAG, reponseMessage);

            System.out.println("Getting Response from Server: "+reponseMessage);

            //parse the JSON response
            JSONObject responseObj;
            try {
                responseObj = new JSONObject(reponseMessage);
                boolean success = responseObj.getBoolean("success");
                //if the reponse was successful check further
                if(success){
                    //get the latest version from the JSON string
                    int latestVersion = responseObj.getInt("latestVersion");
                    //get the lastest application URI from the JSON string
                    appURI = responseObj.getString("appURI");
                    //check if we need to upgrade?
                    if(latestVersion > versionCode){
                        //oh yeah we do need an upgrade, let the user know send an alert message
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginScreen.this);
                        builder.setMessage("There is newer version of this application available, click OK to upgrade now?")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    //if the user agrees to upgrade
                                    public void onClick(DialogInterface dialog, int id) {
                                        //start downloading the file using the download manager
                                        downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
                                        Uri Download_Uri = Uri.parse(appURI);
                                        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
                                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                                        request.setAllowedOverRoaming(false);
                                        request.setTitle("Downloading Update");
                                        request.setDestinationInExternalFilesDir(LoginScreen.this, Environment.DIRECTORY_DOWNLOADS,"Release.apk");
                                        downloadReference = downloadManager.enqueue(request);
                                    }
                                })
                                .setNegativeButton("Remind Later", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                    }
                                });
                        //show the alert message
                        builder.create().show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    //broadcast receiver to get notification about ongoing downloads
    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadReference == referenceId) {

                Log.v(LOG_TAG, "Downloading of the new app version complete");
                //start the installation of the latest version
                Intent installIntent = new Intent(Intent.ACTION_VIEW);

                Uri uri = downloadManager.getUriForDownloadedFile(downloadReference);

                //FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider",);
                File file = new File(getPath(uri));
                System.out.println("File Downloaded: "+file.getAbsolutePath());
                installIntent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(installIntent);

            }
        }
    };

    public String getPath(Uri uri)
    {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =             cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    @Override
    public void onDestroy() {
        //unregister your receivers
        this.unregisterReceiver(receiver);
        this.unregisterReceiver(downloadReceiver);
        super.onDestroy();
    }

    //check for internet connection
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    Log.v(LOG_TAG,String.valueOf(i));
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        Log.v(LOG_TAG, "connected!");
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
