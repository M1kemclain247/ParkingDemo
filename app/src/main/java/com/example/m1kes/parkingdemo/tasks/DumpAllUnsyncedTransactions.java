package com.example.m1kes.parkingdemo.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.base.TransactionAdapter;
import com.example.m1kes.parkingdemo.callbacks.OnTransactionDumpResponse;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;

import java.util.ArrayList;
import java.util.List;


public class DumpAllUnsyncedTransactions extends AsyncTask<List<Transaction>,Void,Void> {

    private Context context;
    private Handler mHandler;
    private OnTransactionDumpResponse response;


    public DumpAllUnsyncedTransactions(Context context){
        this.context = context;
        mHandler = new Handler();
    }

    public DumpAllUnsyncedTransactions(Context context,OnTransactionDumpResponse response){
        this.context = context;
        mHandler = new Handler();
        this.response = response;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }


    @Override
    protected Void doInBackground(List<Transaction>... params) {


        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,"Attempting to push Transactions to Server",Toast.LENGTH_SHORT).show();
            }
        });

        Boolean needsToRetry = false;

        List<Transaction> unsyncedTransactions = params[0];
        List<Transaction> failedTransactions = new ArrayList<>();

        String shiftID =  ShiftDataManager.getCurrentActiveShiftID(context);
        Shift shift = ShiftsAdapter.getShiftForUser(shiftID,context);
        TransactionAdapter adapter = new TransactionAdapter(unsyncedTransactions,context);

        if(shift.isStartShiftSynced()){
             failedTransactions =  adapter.PushAllTransactions();
            //If for any reason some of them failed to push to the server it will try again only 1 once

            if(failedTransactions.size()>0){
                needsToRetry = true;
                System.out.println("Failed to Sync : "+failedTransactions.size()+" With Server");
            }
        }else{
            System.out.println("Start shift hasnt been synced yet.");

            if(adapter.attemptSyncOpenShift(shift)){
                ShiftsAdapter.setShiftStartSynced(shift.getShiftId(),context);
                System.out.println("Shift opened now trying to sync transactions");
                adapter.PushAllTransactions();
            }else{
                System.out.println("Failed to open Shift so cannot Sync Transactions");
            }
        }





        if(needsToRetry){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Failed to push all transactions to the server should retry some
                    Toast.makeText(context,"Unable to Sync all transactions to server please retry again",Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Successfully pushed All Transactions to the server
                    Toast.makeText(context,"All Transactions are synced with the server",Toast.LENGTH_SHORT).show();
                }
            });
        }

        if(response!=null) {
            final List<Transaction> transactionsToSendResponse = new ArrayList<>(failedTransactions);
            if (needsToRetry) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        response.onDumpFailed(transactionsToSendResponse);
                    }
                });
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        response.onDumpComplete();
                    }
                });
            }
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        //TODO Only if the transactions are all uploaded then u can Start Cashup Task Online


    }


}
