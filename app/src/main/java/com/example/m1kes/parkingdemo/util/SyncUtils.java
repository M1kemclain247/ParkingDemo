package com.example.m1kes.parkingdemo.util;


import android.content.Context;

import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.models.TransactionType;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;

import java.util.ArrayList;
import java.util.List;

public class SyncUtils {

    public static int getNumUnsyncedTransactions(Context context){
        String username = UserAdapter.getLoggedInUser(context);
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        List<Transaction> unsyncedTransactions = TransactionAdapter.getAllUnSyncedTransactionsForShift(shiftID,context);
        return unsyncedTransactions.size();
    }

    public static  boolean areTransactionSynced(Context context){
        String username = UserAdapter.getLoggedInUser(context);
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        List<Transaction>  transactions = TransactionAdapter.getAllUnSyncedTransactionsForShift(shiftID,context);
        if(transactions.size()>0){
            return false;
        }else{
            return true;
        }
    }


    public static String getRemainingTransactionsBreakDown(Context context){
        String username = UserAdapter.getLoggedInUser(context);
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        List<Transaction> unsyncedTransactions = TransactionAdapter.getAllUnSyncedTransactionsForShift(shiftID,context);

        int numFines = 0;
        int numPayments = 0;

        for(Transaction t: unsyncedTransactions){
            if(t.getTransactionType().equals(TransactionType.TYPE_FINE)){
                numFines++;
            }else if(t.getTransactionType().equals(TransactionType.TYPE_PAYMENT)){
                numPayments++;
            }
        }

        String strBreakdown = "Fines : "+numFines+"\n"+"Payments : "+numPayments+"\n---------------\n"+"Total : "+unsyncedTransactions.size();
        return strBreakdown;

    }

    public static List<Transaction> getAllUnsyncedTransactions(Context context){
        String username = UserAdapter.getLoggedInUser(context);
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        return TransactionAdapter.getAllUnSyncedTransactionsForShift(shiftID,context);
    }

    public static List<Transaction> getAllTransactionsOrderUnsynced(Context context){

        List<Transaction> transactions = new ArrayList<>();
        String username = UserAdapter.getLoggedInUser(context);
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        List<Transaction> unsyncedTransactions = TransactionAdapter.getAllUnSyncedTransactionsForShift(shiftID,context);
        List<Transaction> syncedTransactions = TransactionAdapter.getAllSyncedTransactionsForUserInShift(username,shiftID,context);

        if(unsyncedTransactions!=null) {
            transactions.addAll(unsyncedTransactions);
        }
        if(syncedTransactions!=null) {
            transactions.addAll(syncedTransactions);
        }
        if(unsyncedTransactions!=null) {
            System.out.println("*********************************************");
            System.out.println("Unsynced Transactions: " + unsyncedTransactions.size());
            System.out.println("Synced Transactions: " + syncedTransactions.size());
            System.out.println("0--------------------------------------------0");
            System.out.println("Total Transactions: " + transactions.size());
            System.out.println("*********************************************");
        }else{
            System.out.println("*********************************************");
            System.out.println("Synced Transactions: " + syncedTransactions.size());
            System.out.println("0--------------------------------------------0");
            System.out.println("Total Transactions: " + transactions.size());
            System.out.println("*********************************************");
        }


        return transactions;
    }


}
