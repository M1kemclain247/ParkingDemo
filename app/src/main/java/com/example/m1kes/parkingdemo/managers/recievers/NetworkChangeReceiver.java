package com.example.m1kes.parkingdemo.managers.recievers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.AllTransactionsAdapter;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnTransactionSelected;
import com.example.m1kes.parkingdemo.callbacks.OnTransactionDumpResponse;
import com.example.m1kes.parkingdemo.managers.InternetServiceManager;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.tasks.DumpAllUnsyncedTransactions;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;
import com.example.m1kes.parkingdemo.util.SyncUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static boolean firstConnect = true;
    private static int NUM_TRIES = 0;
    private Context context;
    private ProgressBar bar;
    private AllTransactionsAdapter adapter;
    private OnTransactionSelected onTransactionSelected;
    private RecyclerView recyclerView;

    private TextView txtConnectionStatus,txtTerminalLastOnline,txtSyncStatus,txtUnsyncedTransactions;

    public NetworkChangeReceiver(){
    }

    public NetworkChangeReceiver(TextView txtConnectionStatus,TextView txtTerminalLastOnline){
        this.txtConnectionStatus = txtConnectionStatus;
        this.txtTerminalLastOnline = txtTerminalLastOnline;
        System.out.println("Creating Constructor for Search vehicle");

    }
    public NetworkChangeReceiver(TextView txtConnectionStatus, TextView txtTerminalLastOnline, TextView txtSyncStatus,
                                 TextView txtUnsyncedTransactions, ProgressBar bar,OnTransactionSelected onTransactionSelected,
                                 AllTransactionsAdapter adapter,RecyclerView recyclerView){
        this.txtConnectionStatus = txtConnectionStatus;
        this.txtTerminalLastOnline = txtTerminalLastOnline;
        this.txtSyncStatus = txtSyncStatus;
        this.txtUnsyncedTransactions = txtUnsyncedTransactions;
        this.bar = bar;
        this.onTransactionSelected = onTransactionSelected;
        this.adapter = adapter;
        this.recyclerView = recyclerView;
        System.out.println("Creating Activty Constructor for Reciever");
    }


    @Override
    public void onReceive(final Context context, final Intent intent) {

        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null) {
            //ONLINE
            System.out.println("Connectivity Receiver : Terminal Online");
            txtConnectionStatus.setTextColor(context.getResources().getColor(R.color.green));
            txtConnectionStatus.setText("Online");
            String lastOnline = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
            txtTerminalLastOnline.setText(lastOnline);
            if (firstConnect) {
                // do subroutines here
                firstConnect = false;
                System.out.println("Recieved this Reciever For Connectivity");
                this.context = context;

                String username = UserAdapter.getLoggedInUser(context);
                String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
                List<Transaction> unsyncedTransactions = TransactionAdapter.getAllUnSyncedTransactionsForShift(shiftID, context);

                if (!unsyncedTransactions.isEmpty()) {
                    if (txtSyncStatus != null) {
                        showSyncState(unsyncedTransactions);
                        DumpAllUnsyncedTransactions task = new DumpAllUnsyncedTransactions(context, response);
                        task.execute(unsyncedTransactions);
                    }else{
                        DumpAllUnsyncedTransactions task = new DumpAllUnsyncedTransactions(context);
                        task.execute(unsyncedTransactions);
                    }

                }
            } else {
                firstConnect = true;

            }


        }else{
            //OFFLINE
            txtConnectionStatus.setTextColor(context.getResources().getColor(R.color.red));
            txtConnectionStatus.setText("Offline");
        }

    }

    OnTransactionDumpResponse response = new OnTransactionDumpResponse() {
        @Override
        public void onDumpComplete() {
            if(txtSyncStatus!=null) {
                  showUpToDateState();
                  updateList();

            }
        }

        @Override
        public void onDumpFailed(List<Transaction> failedTransactions) {
            if(txtSyncStatus!=null) {
                showIdleState();
                updateList();
            }
        }
    };




    private void showUpToDateState(){
        txtUnsyncedTransactions.setText(""+SyncUtils.getNumUnsyncedTransactions(context));
        txtSyncStatus.setText("Up-To-Date");
        bar.setVisibility(View.GONE);
    }

    private void showSyncState(List<Transaction> transactions){
        Toast.makeText(context,"Syncing Transactions...",Toast.LENGTH_SHORT).show();
        txtSyncStatus.setText("Syncing... "+ transactions.size()+" Transactions");
        txtSyncStatus.setTextColor(context.getResources().getColor(R.color.light_green));
        bar.setVisibility(View.VISIBLE);
    }

    private void showIdleState(){
        txtSyncStatus.setText("Idle");
        txtSyncStatus.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        String strNumTransUnsynced = ""+ SyncUtils.getNumUnsyncedTransactions(context);
        txtUnsyncedTransactions.setText(strNumTransUnsynced);
        bar.setVisibility(View.GONE);
    }

    private void updateList(){
        adapter = new AllTransactionsAdapter(context,SyncUtils.getAllTransactionsOrderUnsynced(context),
                onTransactionSelected);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


}