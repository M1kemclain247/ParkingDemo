package com.example.m1kes.parkingdemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.adapters.recyclerviews.AllTransactionsAdapter;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.decoration.DividerItemDecoration;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnTransactionSelected;
import com.example.m1kes.parkingdemo.callbacks.OnTransactionDumpResponse;
import com.example.m1kes.parkingdemo.callbacks.UpdateResourceResponse;
import com.example.m1kes.parkingdemo.managers.InternetServiceManager;
import com.example.m1kes.parkingdemo.managers.recievers.NetworkChangeReceiver;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.models.TransactionType;
import com.example.m1kes.parkingdemo.settings.manager.ApiManager;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.tasks.DumpAllUnsyncedTransactions;
import com.example.m1kes.parkingdemo.tasks.UpdateTask;
import com.example.m1kes.parkingdemo.transactions.MakeFineTransaction;
import com.example.m1kes.parkingdemo.transactions.MakePaymentTransaction;
import com.example.m1kes.parkingdemo.transactions.PayArrearsTransaction;
import com.example.m1kes.parkingdemo.transactions.callbacks.OnMakeFineResponse;
import com.example.m1kes.parkingdemo.transactions.callbacks.OnMakePaymentResponse;
import com.example.m1kes.parkingdemo.transactions.callbacks.OnPayArrearsResponse;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;
import com.example.m1kes.parkingdemo.util.StartupUtils;
import com.example.m1kes.parkingdemo.util.SyncUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SyncTransactions extends AppCompatActivity {


    private RecyclerView recyclerView;
    private AllTransactionsAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Transaction> transactions;
    private Context context;
    private Button btnSyncTransactions;
    private LinearLayout rootViewSync;
    private TextView txtUnsyncedTransactions,txtTerminalStatus,txtTerminalLastOnline,txtTerminalSyncStatus;
    private NetworkChangeReceiver receiver;
    private ProgressBar syncProgress;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sync_transactions);
        StartupUtils.setupActionBar("Sync Manager",this);
        context = SyncTransactions.this;
        initGui();
        getAllTransactions();
        setupRecyclerView(transactions);
        setupConnectivityManager();

    }



    private void initGui(){

        recyclerView = (RecyclerView)findViewById(R.id.allTransactions);
        rootViewSync = (LinearLayout)findViewById(R.id.rootViewSync);
        txtUnsyncedTransactions = (TextView)findViewById(R.id.txtUnsyncedTransactions);
        txtTerminalStatus = (TextView)findViewById(R.id.txtTerminalStatus);
        txtTerminalLastOnline = (TextView)findViewById(R.id.txtTerminalLastOnline);
        txtTerminalSyncStatus = (TextView)findViewById(R.id.txtTerminalSyncStatus);
        syncProgress = (ProgressBar)findViewById(R.id.syncProgress);
        btnSyncTransactions = (Button)findViewById(R.id.btnSyncTransactions);
        btnSyncTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                List<Transaction>  transactions = TransactionAdapter.getAllUnSyncedTransactionsForShift(shiftID,context) ;
                if(transactions.size()>0){
                    showSyncState(transactions);
                    new DumpAllUnsyncedTransactions(context,response).execute(transactions);
                }else {
                    Toast.makeText(context, "All Transactions Are Synced!", Toast.LENGTH_LONG).show();
                }
            }
        });

        if(checkInternet(context)){
            txtTerminalStatus.setTextColor(context.getResources().getColor(R.color.green));
            txtTerminalStatus.setText("Online");
            String lastOnline = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
            txtTerminalLastOnline.setText(lastOnline);
        }else{
            txtTerminalStatus.setTextColor(context.getResources().getColor(R.color.red));
            txtTerminalStatus.setText("Offline");
        }

    }


    private void updateList(){
        adapter = new AllTransactionsAdapter(context,SyncUtils.getAllTransactionsOrderUnsynced(context),
                onTransactionSelected);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void setupConnectivityManager(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkChangeReceiver(txtTerminalStatus,txtTerminalLastOnline,txtTerminalSyncStatus,txtUnsyncedTransactions,
                syncProgress,onTransactionSelected,adapter,recyclerView);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        if(receiver!=null)
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    OnTransactionDumpResponse response  = new OnTransactionDumpResponse() {
        @Override
        public void onDumpComplete() {
            //Just refresh incase any changes happened
            setupRecyclerView(getAllTransactions());
            showIdleState();
        }

        @Override
        public void onDumpFailed(List<Transaction> failedTransactions) {
            //Just refresh incase any changes happened
            setupRecyclerView(getAllTransactions());
            showIdleState();
            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(context).create();
            alertDialog.setTitle("Failed To Sync Transactions");
            alertDialog.setMessage("Failed to sync "+failedTransactions.size()+" Transactions \n\nWould you like to Retry ");
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "Retry",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                            List<Transaction>  transactions = TransactionAdapter.getAllUnSyncedTransactionsForShift(shiftID,context) ;
                            if(transactions.size()>0){
                                showSyncState(transactions);
                                new DumpAllUnsyncedTransactions(context,response).execute(transactions);
                            }else{
                                Toast.makeText(context,"All Transactions Are Already Synced for this shift",Toast.LENGTH_LONG).show();
                            }
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();


        }
    };

    boolean checkInternet(Context context) {
        InternetServiceManager serviceManager = new InternetServiceManager(context);
        if (serviceManager.isNetworkAvailable()) {
            return true;
        } else {
            return false;
        }
    }

    private void showSyncState(List<Transaction> transactions){
        Toast.makeText(context,"Syncing Transactions...",Toast.LENGTH_SHORT).show();
        txtTerminalSyncStatus.setText("Syncing... "+ transactions.size()+" Transactions");
        txtTerminalSyncStatus.setTextColor(context.getResources().getColor(R.color.light_green));
        syncProgress.setVisibility(View.VISIBLE);
        updateList();
    }

    private void showSyncState(Transaction transaction){
        Toast.makeText(context,"Syncing Transactions...",Toast.LENGTH_SHORT).show();
        txtTerminalSyncStatus.setText("Syncing...");
        txtTerminalSyncStatus.setTextColor(context.getResources().getColor(R.color.light_green));
        syncProgress.setVisibility(View.VISIBLE);
        updateList();
    }

    private void showUpToDateState(){
        txtUnsyncedTransactions.setText(SyncUtils.getNumUnsyncedTransactions(context));
        txtTerminalSyncStatus.setText("Up-To-Date");
        syncProgress.setVisibility(View.GONE);
        updateList();
    }

    private void showIdleState(){

        txtTerminalSyncStatus.setText("Idle");
        txtTerminalSyncStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        String strNumTransUnsynced = ""+ SyncUtils.getNumUnsyncedTransactions(context);
        txtUnsyncedTransactions.setText(strNumTransUnsynced);
        syncProgress.setVisibility(View.GONE);
        updateList();
    }

    private List<Transaction> getAllTransactions(){

        String username = UserAdapter.getLoggedInUser(context);
        String shiftId = ShiftDataManager.getCurrentActiveShiftID(context);


        transactions = new ArrayList<>();
        List<Transaction> unsyncedTransactions = TransactionAdapter.getAllUnSyncedTransactionsForShift(shiftId,context);
        List<Transaction> syncedTransactions = TransactionAdapter.getAllSyncedTransactionsForUserInShift(username,shiftId,context);

        if(unsyncedTransactions!=null) {
            txtUnsyncedTransactions.setText(""+SyncUtils.getNumUnsyncedTransactions(context));
            transactions.addAll(unsyncedTransactions);
        }
        if(syncedTransactions!=null) {
            transactions.addAll(syncedTransactions);
        }

        return transactions;
    }



    private void setupRecyclerView(List<Transaction> transactions){
        adapter = new AllTransactionsAdapter(context,transactions,onTransactionSelected);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context));
        adapter.notifyDataSetChanged();
        this.transactions = transactions;
    }


    OnTransactionSelected onTransactionSelected = new OnTransactionSelected() {
        @Override
        public void onClicked(Transaction transaction) {
            showOptions(transaction);
        }
    };



    private void showOptions(final Transaction transaction){


        final OnPayArrearsResponse onPayArrearsResponse = new OnPayArrearsResponse() {
            @Override
            public void onSuccess() {
                if(SyncUtils.areTransactionSynced(context)) {
                    showUpToDateState();
                }else {
                    showIdleState();
                }
                setupRecyclerView(getAllTransactions());
            }

            @Override
            public void onFailed() {
                showIdleState();
            }
        };

        final OnMakeFineResponse onMakeFineResponse = new OnMakeFineResponse() {
            @Override
            public void onSuccess() {

                if(SyncUtils.areTransactionSynced(context)) {
                    showUpToDateState();

                }else {
                    showIdleState();
            }
                setupRecyclerView(getAllTransactions());
            }

            @Override
            public void onFailed() {
                showIdleState();
            }
        };

        final OnMakePaymentResponse onMakePaymentResponse = new OnMakePaymentResponse() {
            @Override
            public void onSuccess() {
                if(SyncUtils.areTransactionSynced(context)) {
                    showUpToDateState();
                    updateList();
                }else {
                    showIdleState();
                }
                setupRecyclerView(getAllTransactions());
            }

            @Override
            public void onFailed() {
                showIdleState();
            }
        };

        if(transaction.is_synced()){
            setupRecyclerView(getAllTransactions());
        }else{
            final CharSequence[] options = { "Retry Upload Transaction" };
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setIcon(R.drawable.ic_coin_black_24dp);
            builder.setTitle("Options");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Retry Upload Transaction")) {
                        dialog.dismiss();
                        showSyncState(transaction);
                        //Call appropriate upload method
                        if (transaction.getTransactionType().equalsIgnoreCase(TransactionType.TYPE_FINE)) {
                            MakeFineTransaction makeFineTransaction = new MakeFineTransaction(context, onMakeFineResponse);
                            makeFineTransaction.execute(transaction);
                        } else if (transaction.getTransactionType().equalsIgnoreCase(TransactionType.TYPE_PAYMENT)) {
                            MakePaymentTransaction makePaymentTransaction = new MakePaymentTransaction(context, onMakePaymentResponse);
                            makePaymentTransaction.execute(transaction);
                        } else if (transaction.getTransactionType().equalsIgnoreCase(TransactionType.TYPE_PAY_ARREARS)) {
                            PayArrearsTransaction payArrearsTransaction = new PayArrearsTransaction(context,onPayArrearsResponse);
                            payArrearsTransaction.execute(transaction);
                        }
                    }
                }
            });
            builder.show();
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(this,MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
