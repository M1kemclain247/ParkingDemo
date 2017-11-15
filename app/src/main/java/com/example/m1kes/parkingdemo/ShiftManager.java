package com.example.m1kes.parkingdemo;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.adapters.spinners.PrecinctSpinnerAdapter;
import com.example.m1kes.parkingdemo.adapters.spinners.ZoneSpinnerAdapter;
import com.example.m1kes.parkingdemo.fragments.dialogs.AuthorizeUser;
import com.example.m1kes.parkingdemo.fragments.dialogs.PopUpCashUp;
import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.User;
import com.example.m1kes.parkingdemo.models.Zone;
import com.example.m1kes.parkingdemo.printer.models.Vehicle;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrecinctAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ZonesAdapter;
import com.example.m1kes.parkingdemo.tasks.GetSchedules;
import com.example.m1kes.parkingdemo.tasks.LogoutTask;
import com.example.m1kes.parkingdemo.tasks.SyncShiftTask;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;
import com.example.m1kes.parkingdemo.util.StartupUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ShiftManager extends AppCompatActivity implements AuthorizeUser.AuthorizeUserDialogListener {

    private Context context;
    private ProgressDialog progressDialog;
    private Spinner spinnerZones,spinnerPrecincts;
    private List<Zone> zones;
    private Button btnStartShift,btnViewSchedule,btnCloseExistingShift;
    private TextView txtCurrentUser,txtActiveShifts,txtNB,txtShiftFootNote,txtActiveShiftNote;
    private LinearLayout rootSpinnerShift,rootInfoSpinnerShift;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_shift_manager);
        context = ShiftManager.this;
        zones = new ArrayList<>();

        StartupUtils.setupActionBar("Shift Manager",this);
        setupSpinners();
        setupButtons();
        setupTextViews();




    }

    private void setupTextViews(){

        txtCurrentUser = (TextView)findViewById(R.id.txtCurrentlyloggedUserShift);
        txtActiveShifts = (TextView)findViewById(R.id.txtActiveShifts);
        rootSpinnerShift = (LinearLayout)findViewById(R.id.rootSpinnerShift);
        rootInfoSpinnerShift = (LinearLayout)findViewById(R.id.rootInfoSpinnerShift);
        btnCloseExistingShift = (Button)findViewById(R.id.btnCloseExistingShift);
        txtShiftFootNote = (TextView)findViewById(R.id.txtShiftFootNote);
        txtNB = (TextView)findViewById(R.id.txtNB);
        txtActiveShiftNote = (TextView)findViewById(R.id.txtActiveShiftNote);

        String shiftID =  ShiftDataManager.getCurrentActiveShiftID(context);

        if(shiftID!=null){
            //There is an active shift show username
            Shift shift = ShiftsAdapter.getShiftForUser(shiftID,context);
            txtActiveShifts.setText(shift.getUsername());
            //Hide the ability to create a shift
            rootSpinnerShift.setVisibility(View.GONE);
            rootInfoSpinnerShift.setVisibility(View.GONE);
            btnStartShift.setVisibility(View.GONE);

            btnCloseExistingShift.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSpotCheckTransactionDetails();
                }
            });
        }else{
            //There is no active shift so allow them to start a new shift
            txtActiveShifts.setVisibility(View.GONE);
            btnCloseExistingShift.setVisibility(View.GONE);
            txtShiftFootNote.setVisibility(View.GONE);
            txtNB.setVisibility(View.GONE);
            txtActiveShiftNote.setVisibility(View.GONE);

        }



        String username =  UserAdapter.getLoggedInUser(context);
        if(username!=null){
            txtCurrentUser.setText(username);
        }




    }

    private void showSpotCheckTransactionDetails(){

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        // LayoutInflater inflater = context.getLayoutInflater();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_login_marshal_active_shift, null);
        TextView txtCurrentActiveShiftUser =  (TextView)view.findViewById(R.id.txtCurrentActiveShiftUser);
        final EditText editPasswordActiveShift = (EditText)view.findViewById(R.id.editPasswordActiveShift);
        Button btnLoginActiveShiftUser = (Button)view.findViewById(R.id.btnLoginActiveShiftUser);

        //TODO CHECK THAT THIS WILL NEVER BE NULL SINCE THE OPTION TO MAKE THIS POSSIBLE IS MAKING THE SAME CHECK
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        Shift shift = ShiftsAdapter.getShiftForUser(shiftID,context);
        final String activeUsername = shift.getUsername();
        txtCurrentActiveShiftUser.setText(activeUsername);

        btnLoginActiveShiftUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validate User
                String password =  editPasswordActiveShift.getText().toString();
                boolean authenticated = UserAdapter.loginUser(new User(activeUsername,password),context);

                if(authenticated){
                    //IF authenticated send them to their active shift
                    //logout the user who just tried to login and perform actions
                    String username = UserAdapter.getLoggedInUser(context);
                    boolean worked = UserAdapter.logoutUser(username,context);
                    //if logout was successful

                    if(worked){
                        //Relogin the active Shift user
                        UserAdapter.setUserLoggedIn(activeUsername,context);
                        //Send them to their shift
                        startActivity(new Intent(context,MainActivity.class));
                    }


                }else{
                    //if login failed display so
                    editPasswordActiveShift.setError("Incorrect Password");
                    Toast.makeText(context,"Login Failed",Toast.LENGTH_LONG).show();
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


    private void setupButtons(){

        btnViewSchedule = (Button)findViewById(R.id.btnViewSchedule);
        btnStartShift = (Button)findViewById(R.id.btnStartShift);


        btnViewSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new GetSchedules(context).execute();
            }
        });


        btnStartShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmStartShiftDialog();
            }
        });

    }


    private void confirmStartShiftDialog() {

        Precinct precinct = (Precinct)spinnerPrecincts.getSelectedItem();
        final Zone zone = (Zone)spinnerZones.getSelectedItem();

        StringBuilder sb = new StringBuilder();
        sb.append("Zone  :   ").append(zone.getName()).append("\n");
        sb.append("Precinct:  "+precinct.getName());

        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
        alertDialog.setTitle("Confirm Shift Details");
        alertDialog.setMessage("You are about to start a shift with the following details: \n\n"+sb.toString()+"\n\n      Is this Correct?");
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        String username = UserAdapter.getLoggedInUser(context);
                        Precinct precinct = (Precinct)spinnerPrecincts.getSelectedItem();
                        String shift_start_time =  new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                        String terminalID =  String.valueOf(ShiftDataManager.getDefaultTerminalID(context));

                        Shift shift = new Shift();
                        shift.setUsername(username);
                        shift.setPrecinctID(precinct.getId());
                        shift.setPrecinctName(precinct.getName());
                        shift.setPrecinctZoneName(zone.getName());
                        shift.setTerminalID(Integer.parseInt(terminalID));
                        shift.setStart_time(Long.valueOf(shift_start_time));
                        //Create shift locally
                        System.out.println("Trying to create a shift for the following user");
                        ShiftsAdapter.createShift(shift,context);
                        //Need to get the shiftID from the created Shift

                        int id =  ShiftsAdapter.getShiftIDForNewShift(username,Long.valueOf(shift_start_time),context);

                        System.out.println("Shift Created with localID of "+id);
                        //Generate the unique ShiftID Code

                        String generatedShiftID = ShiftsAdapter.makePaddedShiftID(shift,id,context);


                        shift.setShiftId(generatedShiftID);


                        System.out.println("Creating the unique padded shift id of "+generatedShiftID);
                        //Set the new coded ShiftID


                        ShiftsAdapter.setCodedShiftID(id,generatedShiftID,context);


                        //Set this coded shift id to use throughout the application
                        System.out.println("Setting the current user ShiftID to "+generatedShiftID);


                        ShiftDataManager.setCurrentActiveShiftID(generatedShiftID,context);


                        //Try sync shift with server
                        new SyncShiftTask(context).execute(shift);
                        Toast.makeText(context,"Shift Started!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(context,MainActivity.class));
                        dialog.dismiss();

                    }
                });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });
        alertDialog.show();
    }



    private void setupSpinners(){

        spinnerZones = (Spinner)findViewById(R.id.spinnerZones);
        spinnerPrecincts = (Spinner)findViewById(R.id.spinnerPrecincts);
        zones = ZonesAdapter.getAllZones(context);
        ZoneSpinnerAdapter adapter = new ZoneSpinnerAdapter(context,zones);
        spinnerZones.setAdapter(adapter);
        spinnerZones.setOnItemSelectedListener(new OnZoneSelectedSpinner());

    }


    public void showCashupDialog() {
        PopUpCashUp dialog = new PopUpCashUp();
        dialog.show(getFragmentManager(), "PopUpCashup");
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog, EditText editText,EditText editUsername,String MODE) {
        String password =  editText.getText().toString().trim();
        String username = UserAdapter.getLoggedInUser(context);

        if(MODE.equals(AuthorizeUser.MODE_CASHUP)) {
            boolean authorized = UserAdapter.loginUser(new User(username, password, false), context);
            if (authorized) {
                dialog.dismiss();
                showCashupDialog();
            } else {
                editText.setError("Invalid Password!");
            }
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }



    @Override
    public void onBackPressed() {
        showConfirmLogoutAction();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id== android.R.id.home) {
            showConfirmLogoutAction();
            return true;
        }
        return false;
    }




    private void showConfirmLogoutAction() {
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

    }


    public class OnZoneSelectedSpinner implements AdapterView.OnItemSelectedListener {

        public OnZoneSelectedSpinner(){
        }

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            refreshList(zones.get(pos),spinnerPrecincts,context);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
        }

    }

    private void refreshList(Zone zone,Spinner spinner,Context context){

        List<Precinct> precincts =  PrecinctAdapter.getPrecinctsFromZone(zone,context);
        PrecinctSpinnerAdapter spinnerAdapter = new PrecinctSpinnerAdapter(context,precincts);
        spinner.setAdapter(spinnerAdapter);
    }
}
