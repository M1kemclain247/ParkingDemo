package com.example.m1kes.parkingdemo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.callbacks.UpdateResourceResponse;
import com.example.m1kes.parkingdemo.settings.manager.ApiManager;
import com.example.m1kes.parkingdemo.tasks.UpdateTask;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.m1kes.parkingdemo.settings.developer.DeveloperSettings.setDeveloperModeEnabled;

public class SplashScreen extends AppCompatActivity {

    private ProgressBar bar;
    private TextView txtUpdateDetails;
    private LinearLayout rootView;
    private Context context;
    private Button btnRetryUpdate,btnWorkOffline;
    private Snackbar mSnackbar;
    private UpdateTask updateTask;
    private String DAILY_UPDATE_CHECK = "DailyCheck";
    private Spinner spinnerServerType;
    final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    final String TAG = "Permissions";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        context = this.getApplicationContext();

        //ShiftDataManager.setDefaultTerminalID(context);
        btnRetryUpdate = (Button)findViewById(R.id.btnRetryUpdate);
        spinnerServerType = (Spinner)findViewById(R.id.spinnerServerType);
        spinnerServerType.setVisibility(View.GONE);

        btnRetryUpdate.setVisibility(View.GONE);
        bar = (ProgressBar)findViewById(R.id.progressBar);
        rootView = (LinearLayout)findViewById(R.id.rootView);
        txtUpdateDetails = (TextView)findViewById(R.id.txtUpdateDetails);
        btnWorkOffline = (Button)findViewById(R.id.btnWorkOffline);
        btnWorkOffline.setVisibility(View.GONE);
        setOnClicks();

        //TODO Check if this works
        //Sets Developer Mode enabled
        setDeveloperModeEnabled(false,context);

        if(checkAndRequestPermissions()){
            //get the Ip Address depending on what is selected and save it to the ApiManager Pref to be read throught app
            ApiManager.setServerIp(getCurrentSelectedOption(),context);
            bar.setVisibility(View.VISIBLE);
            txtUpdateDetails.setText("Checking for new Content....");
            updateTask  = new UpdateTask(context,resourceResponse);
            updateTask.execute(getCurrentSelectedOption());
        }

        setRetry();


    }

    private String getCurrentSelectedOption(){
        return (String)spinnerServerType.getSelectedItem();
    }


    private void setOnClicks(){

        btnWorkOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,LoginScreen.class));

            }
        });

    }


    UpdateResourceResponse resourceResponse = new UpdateResourceResponse() {
        @Override
        public void updateComplete() {
            bar.setVisibility(View.GONE);
            startActivity(new Intent(context, LoginScreen.class));
            Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void updateFailed() {
            bar.setVisibility(View.GONE);
            txtUpdateDetails.setText("Server Error while Updating Resources, Please Try Again!");
            btnRetryUpdate.setVisibility(View.VISIBLE);
            spinnerServerType.setVisibility(View.VISIBLE);
            btnWorkOffline.setVisibility(View.VISIBLE);
            showSnackbarError(rootView,"Try Connnecting to Wifi or Mobile Data!");


        }

        @Override
        public void onNoConnection() {
            bar.setVisibility(View.GONE);
            txtUpdateDetails.setText("Failed to Update Resources Please Try Again, No Internet Connection!");
            btnRetryUpdate.setVisibility(View.VISIBLE);
            spinnerServerType.setVisibility(View.VISIBLE);
            btnWorkOffline.setVisibility(View.VISIBLE);
            showSnackbarError(rootView,"Try Connnecting to Wifi or Mobile Data!");
            //Check if there is currently an active shift running if so then allow them to skip

        }

        @Override
        public void onProgressUpdated(String message) {
            txtUpdateDetails.setText(message);
        }
    };

    private void showSnackbarError(LinearLayout rootView,String message){

        mSnackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE);
        mSnackbar.setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       // new UpdateTask(context,bar,txtUpdateDetails,resourceResponse).execute();
                    }
                });
        mSnackbar.setActionTextColor(getResources().getColor(android.R.color.holo_red_light ));
        mSnackbar.show();
    }

    private void setRetry(){


        btnRetryUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnRetryUpdate.setVisibility(View.GONE);
                spinnerServerType.setVisibility(View.GONE);
                btnWorkOffline.setVisibility(View.GONE);
                if(mSnackbar!=null)
                mSnackbar.dismiss();

                if(checkAndRequestPermissions()){
                    //get the Ip Address depending on what is selected and save it to the ApiManager Pref to be read throught app
                    ApiManager.setServerIp(getCurrentSelectedOption(),context);
                    bar.setVisibility(View.VISIBLE);

                    txtUpdateDetails.setText("Checking for new Content....");

                    updateTask  = new UpdateTask(context,resourceResponse);
                    updateTask.execute(getCurrentSelectedOption());
                }

            }
        });


    }

    private String checkDateForUpdate(){
        String format = "yyyy-MM-dd";
        SharedPreferences prefs = getSharedPreferences(DAILY_UPDATE_CHECK, MODE_PRIVATE);
        String datecheck = prefs.getString("date", null);
        if (datecheck != null) {
            return datecheck;
        }else{
            return null;
        }
    }




    private  boolean checkAndRequestPermissions() {
        int storageReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int storageWritePermission = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
        int coarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int fineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storageReadPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (storageWritePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (coarseLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (fineLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }



        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                   {
                        Log.d(TAG,  "Permissions granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                       Log.i(TAG, "Permission has been granted by user");
                       bar.setVisibility(View.VISIBLE);
                       txtUpdateDetails.setText("Checking for new Content....");
                       updateTask  = new UpdateTask(context,resourceResponse);
                       updateTask.execute(getCurrentSelectedOption());

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                ){
                            showDialogOK("There are permissions required for this App to work properly",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    Toast.makeText(context,"Denied PERMISSION!",Toast.LENGTH_SHORT).show();
                                                    btnRetryUpdate.setVisibility(View.VISIBLE);
                                                    txtUpdateDetails.setText("Requires Permissions to Continue!");
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }




    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

}
