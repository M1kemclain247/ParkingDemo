package com.example.m1kes.parkingdemo;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.adapters.recyclerviews.AllTransactionsAdapter;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.SearchHistoryRecylerAdapter;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.decoration.DividerItemDecoration;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnHistoryItemSelected;
import com.example.m1kes.parkingdemo.callbacks.ArrearsResponse;
import com.example.m1kes.parkingdemo.callbacks.VehicleFoundResponse;
import com.example.m1kes.parkingdemo.managers.recievers.NetworkChangeReceiver;
import com.example.m1kes.parkingdemo.models.SearchHistory;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.networking.parsers.BalanceEnquiryParser;
import com.example.m1kes.parkingdemo.printer.PrinterDevices;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.printer.models.BalanceEnquiry;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrintJobAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.SearchHistoryAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.tasks.ArrearsReportTask;
import com.example.m1kes.parkingdemo.tasks.BalanceEnquiryTask;
import com.example.m1kes.parkingdemo.util.GeneralUtils;
import com.example.m1kes.parkingdemo.util.ObjectParser;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;
import com.example.m1kes.parkingdemo.util.StartupUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static com.example.m1kes.parkingdemo.util.GeneralUtils.getCurrentDateTime;

public class SearchVehicle extends AppCompatActivity {

    private Button btnSearchVehicle;
    private EditText Search_editRegNumber;
    private Context context;
    private BalanceEnquiry balanceEnquiry;
    private ProgressDialog progressDialog;
    private TextView txtSearchVehicleCurrentUser,txtTerminalStatusSearch,txtTerminalLastLoggedSearch,txtSearchVersion;
    private RadioGroup radioSearchSelection;
    private RadioButton radioBtnBalEnquiry,radioBtnArrReportSummary;
    private RecyclerView searchHistoryRecycler;
    private RecyclerView.LayoutManager layoutManager;
    private SearchHistoryRecylerAdapter adapter;
    private List<SearchHistory> searchHistories;
    private NetworkChangeReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search_vehicle);
        context = SearchVehicle.this;
        StartupUtils.setupActionBar("Search Vehicle",this);
        initGui();
        setupOnClicks();
        setupConnectivityManager();


    }


    private void initGui(){

        Search_editRegNumber = (EditText)findViewById(R.id.Search_editRegNumber);
        btnSearchVehicle = (Button)findViewById(R.id.btnSearchVehicle);
        txtSearchVehicleCurrentUser = (TextView)findViewById(R.id.txtSearchVehicleCurrentUser);
        txtTerminalStatusSearch = (TextView)findViewById(R.id.txtTerminalStatusSearch);
        txtTerminalLastLoggedSearch = (TextView)findViewById(R.id.txtTerminalLastLoggedSearch);

        radioSearchSelection = (RadioGroup)findViewById(R.id.radioSearchSelection);
        radioBtnBalEnquiry = (RadioButton)findViewById(R.id.radioBtnBalEnquiry);
        radioBtnArrReportSummary = (RadioButton)findViewById(R.id.radioBtnArrReportSummary);

        //Setup recyclerview for search History
        searchHistoryRecycler =(RecyclerView)findViewById(R.id.searchHistoryRecycler);

        setupRecyclerView(SearchHistoryAdapter.getAllSearchHistory(context));

        String currentUser = UserAdapter.getLoggedInUser(context);

        if(currentUser!=null)
        txtSearchVehicleCurrentUser.setText(currentUser);
        else
            txtSearchVehicleCurrentUser.setText("Not Logged In");


        setupVersionSigning();

    }

    private void setupVersionSigning(){

        txtSearchVersion = (TextView)findViewById(R.id.txtSearchVersion);

        String version =  GeneralUtils.getVersionNumber(context);
        if(version!=null){
            txtSearchVersion.setText("Version "+version);
        }else{
            txtSearchVersion.setText("Unknown Version");
        }
    }

    private void setupRecyclerView(List<SearchHistory> searchHistories){

        this.searchHistories = searchHistories;
        adapter = new SearchHistoryRecylerAdapter(context,searchHistories,onHistoryItemSelected);
        layoutManager = new LinearLayoutManager(context);
        searchHistoryRecycler.setLayoutManager(layoutManager);
        searchHistoryRecycler.setHasFixedSize(true);
        searchHistoryRecycler.setAdapter(adapter);
        searchHistoryRecycler.addItemDecoration(new DividerItemDecoration(context));
        adapter.notifyDataSetChanged();
    }

    OnHistoryItemSelected onHistoryItemSelected = new OnHistoryItemSelected() {
        @Override
        public void onClick(SearchHistory searchHistory) {
        }
    };


    private void setupConnectivityManager(){


        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver(txtTerminalStatusSearch,txtTerminalLastLoggedSearch);
        registerReceiver(receiver, filter);

    }

    @Override
    protected void onPause() {
        if(receiver!=null)
        unregisterReceiver(receiver);
        super.onPause();
    }

    private void setupOnClicks(){


        btnSearchVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String rawregNumber = Search_editRegNumber.getText().toString();
                String regNumber = GeneralUtils.cleanRawVehicleReg(rawregNumber);
                if(!regNumber.trim().equalsIgnoreCase("")&&
                        regNumber.length()!=0){
                    int selectedId=radioSearchSelection.getCheckedRadioButtonId();
                    if(selectedId == radioBtnBalEnquiry.getId()){

                        if(isNetworkAvailable()) {

                            progressDialog = new ProgressDialog(context);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Searching For Vehicle " + regNumber + " ....");
                            progressDialog.show();

                            new BalanceEnquiryTask(context, response).execute(regNumber);
                            //add the search as search history
                            SearchHistoryAdapter.addNewSearchHistory(regNumber,getCurrentDateTime(),context);
                        }else{
                            Toast.makeText(context,"No Internet Access!",Toast.LENGTH_SHORT).show();
                        }
                    }else if(selectedId == radioBtnArrReportSummary.getId()){

                        if(isNetworkAvailable()) {

                            progressDialog = new ProgressDialog(context);
                            progressDialog.setCancelable(false);
                            progressDialog.setMessage("Getting Arrears List for vehicle:  " + regNumber + " ....");
                            progressDialog.show();

                            ArrearsReportTask task = new ArrearsReportTask(context, arrearsResponse);
                            task.execute(regNumber, "10");
                            //add the search as search history
                            SearchHistoryAdapter.addNewSearchHistory(regNumber,getCurrentDateTime(),context);

                        }else{
                            Toast.makeText(context,"No Internet Access!",Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(context,"Select An Operation!",Toast.LENGTH_SHORT).show();
                    }


                }else{
                    Search_editRegNumber.setError("Invalid Reg Number!");
                }
            }
        });

    }




    @Override
    public void onBackPressed() {

        if(BluetoothAdapter.getDefaultAdapter()!=null&&BluetoothAdapter.getDefaultAdapter().isEnabled()){
            BluetoothAdapter.getDefaultAdapter().disable();
        }
        startActivity(new Intent(context,MainActivity.class));
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id== android.R.id.home){
            this.finish();
            if(BluetoothAdapter.getDefaultAdapter()!=null&&BluetoothAdapter.getDefaultAdapter().isEnabled()){
                BluetoothAdapter.getDefaultAdapter().disable();
            }
            startActivity(new Intent(this,MainActivity.class));
            return true;
        }

        return false;

    }



    VehicleFoundResponse response = new VehicleFoundResponse() {
        @Override
        public void vehicleFound(String response) {

            //Parse the Response

            Pattern pattern = Pattern.compile("errormsg|HTTP|Tunnel|Did not work|Shift Id|invalid|error");
                if(pattern.matcher(response).find()){
                    progressDialog.dismiss();
                    Toast.makeText(context,""+response,Toast.LENGTH_LONG).show();
                }else{
                    Receipt receipt = BalanceEnquiryParser.getBalanceEnquiry(response,context);
                    String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                    startPrint(receipt,shiftID);
                    progressDialog.dismiss();
                    Toast.makeText(context,"Successfully Found vehicle!",Toast.LENGTH_LONG).show();
                }

            setupRecyclerView(SearchHistoryAdapter.getAllSearchHistory(context));

        }

        @Override
        public void notFound(String response) {
            progressDialog.dismiss();
            Toast.makeText(context,""+response,Toast.LENGTH_LONG).show();
            setupRecyclerView(SearchHistoryAdapter.getAllSearchHistory(context));
        }
    };


    private void startPrint(Receipt receipt,String shiftID){

        PrinterJob printerJob = new PrinterJob();
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



    public void showOptions(final String regNumber){

        final CharSequence[] options = { "Balance Enquiry" ,"Print List"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.ic_car_black_24dp);
        builder.setTitle("Options");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Balance Enquiry")) {


                    dialog.dismiss();
                }else if(options[item].equals("Print List")){


                    progressDialog = new ProgressDialog(context);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Getting Arrears List for vehicle:  "+regNumber+" ....");
                    progressDialog.show();

                    new ArrearsReportTask(context,arrearsResponse).execute();
                    Toast.makeText(context,"This will print a list of Arrears",Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            }

        });

        builder.show();

    }



    ArrearsResponse arrearsResponse = new ArrearsResponse() {
        @Override
        public void getArrearsSuccessfull(Receipt receipt) {
            progressDialog.dismiss();
            startPrint(receipt,UserAdapter.getLoggedInUser(context));
            setupRecyclerView(SearchHistoryAdapter.getAllSearchHistory(context));
        }

        @Override
        public void getArrearsFailed(String response) {
            progressDialog.dismiss();
            Toast.makeText(context,""+response,Toast.LENGTH_LONG).show();
            setupRecyclerView(SearchHistoryAdapter.getAllSearchHistory(context));
        }

    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
