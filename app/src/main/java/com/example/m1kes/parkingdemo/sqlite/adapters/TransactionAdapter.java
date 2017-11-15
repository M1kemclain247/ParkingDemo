package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.models.TransactionType;
import com.example.m1kes.parkingdemo.printer.models.Vehicle;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.ShiftsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TransactionsTable;
import com.example.m1kes.parkingdemo.util.DBUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static com.example.m1kes.parkingdemo.util.DBUtils.*;
import static com.example.m1kes.parkingdemo.util.DBUtils.convertBoolToInt;

public class TransactionAdapter {





    public static List<Transaction> getAllLoginTransactions(Context context){


        List<Transaction> transactions = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.TRANSACTIONS_CONTENT_URI , null, TransactionsTable.COLUMN_TRANSACTION_TYPE+" = '" + "MakeFine" + "' AND "+
                        TransactionsTable.COLUMN_IS_EXPIRED+" = '" + "0" + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.

            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.
                String vehicleReg = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_VEHICLE_REG));
                long expiry_datetime = Long.parseLong(cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_EXPIRY_DATETIME)));
                String receiptNumber = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_RECIEPT_NO));

                Transaction transaction = new Transaction();

                transaction.setVehicleregNumber(vehicleReg);
                transaction.setExpiry_datetime(expiry_datetime);
                transaction.setRecieptNumber(receiptNumber);


                Log.i("Transaction Adapter ", "Checking for logged In Vehicles");

                String expiryDateTime = ""+transaction.getExpiry_datetime();

                Calendar cal = Calendar.getInstance();
                String[] parts= getDateParts(expiryDateTime);
                cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                Date lastLoggedDate = cal.getTime();

                System.out.println("*********************************GETTING LOGGED VEHICLES************************************");
                System.out.println("Expiry Date Time : "+lastLoggedDate + " Current Date Time: "+new Date());
                System.out.println("Expiry Date Time : "+lastLoggedDate.getTime() + " Current Date Time: "+new Date().getTime());
                System.out.println("Transaction : "+transaction);
                System.out.println("*******************************************************************************************");

                if(new Date().before(lastLoggedDate)){

                    // Show phone number in Logcat
                    Log.i("Transaction Adapter ", transaction.toString());
                    // end of while loop

                    long diff = lastLoggedDate.getTime() - new Date().getTime();//as given

                    long diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000);

                    String duration = parseDuration(diffHours,diffMinutes,diffSeconds);
                    transaction.setRemaining_time(duration);

                    transactions.add(transaction);
                }



            }
            cursor.close();

        }



        return transactions;
    }


    /**
     * PREPAYMENTS
     * **/

    public static int setLocalUserBalance(String shiftID,String vehicleReg,double newAmount,Context context){

        ContentValues updateValues = new ContentValues();
        updateValues.put(TransactionsTable.COLUMN_USER_BALANCE,newAmount);
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI,TransactionsTable.TABLE_TRANSACTIONS);

        System.out.println("Updating transaction with ID: "+shiftID);
        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                TransactionsTable.COLUMN_SHIFT_ID+"=? AND "+
                        TransactionsTable.COLUMN_VEHICLE_REG+"=?",new String[]{shiftID,vehicleReg});

        if(rowsAffected>0){
            System.out.println("Successfully added local User Transaction amount to Shift ");
        }else{
            System.out.println("Failed to add local User Transaction amount to this shift");
        }

        return rowsAffected;
    }


    public static void updateLocalUserPrepaidTransactionBalance(String shiftID, String vehicleReg , double localAmountToAdd, Context context){

        //TODO NEGATING TO APPROPRIATE THE SERVER WITH NEGATIVE OR POSITIVE VALUES
        double currentTotal =  getLocalUserTransactionBalance(shiftID,vehicleReg,context);
        double newTotal = currentTotal - localAmountToAdd;

        System.out.println("Current local Transaction Balance: "+currentTotal);
        System.out.println("Adding new amount: "+localAmountToAdd);
        System.out.println("New local Transaction Balance: "+newTotal);
        addLocalUserTransactionBalanceAmount(shiftID,vehicleReg,newTotal,context);
    }

    public static void deductLocalUserPrepaidTransactionBalance(String shiftID, String vehicleReg , double localAmountToAdd, Context context){

        //TODO NEGATING TO APPROPRIATE THE SERVER WITH NEGATIVE OR POSITIVE VALUES
        double currentTotal =  getLocalUserTransactionBalance(shiftID,vehicleReg,context);
        double newTotal = currentTotal + localAmountToAdd;

        System.out.println("Current local Transaction Balance: "+currentTotal);
        System.out.println("Adding new amount: "+localAmountToAdd);
        System.out.println("New local Transaction Balance: "+newTotal);
        addLocalUserTransactionBalanceAmount(shiftID,vehicleReg,newTotal,context);
    }


    public static double getLocalUserTransactionBalance(String shiftID, String vehicleReg,Context context){

        double currentBalance = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.TRANSACTIONS_CONTENT_URI , null,  TransactionsTable.COLUMN_SHIFT_ID + " = '" + shiftID+"' AND "+
                TransactionsTable.COLUMN_VEHICLE_REG+" = '"+vehicleReg+"'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return currentBalance;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return currentBalance;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.
            while (cursor.moveToNext()) {

                currentBalance  = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_USER_BALANCE));
                System.out.println("Loading Shift Total Current local User Transaction Amount: "+currentBalance);
            }
            cursor.close();
        }
        return currentBalance;
    }

    public static int addLocalUserTransactionBalanceAmount(String shiftID,String vehicleReg,double newAmount,Context context){

        ContentValues updateValues = new ContentValues();
        updateValues.put(TransactionsTable.COLUMN_USER_BALANCE,newAmount);
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI,TransactionsTable.TABLE_TRANSACTIONS);

        System.out.println("Updating transaction with ID: "+shiftID);
        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                TransactionsTable.COLUMN_SHIFT_ID+"=? AND "+
                TransactionsTable.COLUMN_VEHICLE_REG+"=?",new String[]{shiftID,vehicleReg});

        if(rowsAffected>0){
            System.out.println("Successfully added local User Transaction amount to Shift ");
        }else{
            System.out.println("Failed to add local User Transaction amount to this shift");
        }

        return rowsAffected;
    }



    public static long isVehicleLoggedIn(String vehicleReg,Context context){

        long expiryTime = 0;
        List<Long> expiryTimes = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.TRANSACTIONS_CONTENT_URI , null,  TransactionsTable.COLUMN_VEHICLE_REG + " = '" + vehicleReg + "' AND "+
                TransactionsTable.COLUMN_TRANSACTION_TYPE + " = '" + TransactionType.TYPE_FINE + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return expiryTime;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return expiryTime;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.
                long tempExpiryTime  = Long.valueOf(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_EXPIRY_DATETIME)));
                System.out.println("Checking if this vehicle is logged in with all transactions made");

                System.out.println("adding expiry times for this vehicle in loop: "+tempExpiryTime);
                expiryTimes.add(tempExpiryTime);
            }
            cursor.close();

            for(long ex: expiryTimes){

                System.out.println("Expiry Time in loop: "+ex);
                String strExprTime =""+ex;
                Calendar cal = Calendar.getInstance();
                String[] parts= getDateParts(strExprTime);
                cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                Date lastLoggedDate = cal.getTime();
                if (new Date().before(lastLoggedDate)) {
                    //Then it means it is logged in
                    expiryTime = ex;
                    break;
                }
            }
        }

        return expiryTime;
    }

    public static List<Vehicle> getAllLoginPrintoutTransactions(Context context){

        List<Vehicle> vehicles = new ArrayList<>();
        List<Transaction> transactions = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.TRANSACTIONS_CONTENT_URI , null, TransactionsTable.COLUMN_TRANSACTION_TYPE+" = '" + "MakeFine" + "' AND "+
                TransactionsTable.COLUMN_IS_EXPIRED+" = '" + "0" + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.

            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.
                String vehicleReg = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_VEHICLE_REG));
                long expiry_datetime = Long.parseLong(cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_EXPIRY_DATETIME)));
                String receiptNumber = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_RECIEPT_NO));
                String transactionType = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_TRANSACTION_TYPE));
                long in_dateTime =  Long.valueOf(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_IN_DATETIME)));
                double amountDue = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_AMOUNT_DUE));
                double amountPaid = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_AMOUNT_PAID));
                boolean isPrepayment = DBUtils.convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_PREPAYMENT)));
                double userBalance = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_USER_BALANCE));
                int bayNumber = cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_BAY_NUMBER));

                Transaction transaction = new Transaction();

                transaction.setVehicleregNumber(vehicleReg);
                transaction.setExpiry_datetime(expiry_datetime);
                transaction.setRecieptNumber(receiptNumber);
                transaction.setTransactionType(transactionType);
                transaction.setAmountPaid(amountPaid);
                transaction.setAmountDue(amountDue);
                transaction.setIs_Prepayment(isPrepayment);
                transaction.setUserBalance(userBalance);
                transaction.setIn_datetime(in_dateTime);
                transaction.setBayNumber(bayNumber);

                Log.i("Transaction Adapter ", "Checking for logged In Vehicles");

                String expiryDateTime = ""+transaction.getExpiry_datetime();

                Calendar cal = Calendar.getInstance();
                String[] parts= getDateParts(expiryDateTime);
                cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                Date lastLoggedDate = cal.getTime();

                System.out.println("*********************************GETTING LOGGED VEHICLES************************************");
                System.out.println("Expiry Date Time : "+lastLoggedDate + " Current Date Time: "+new Date());
                System.out.println("Expiry Date Time : "+lastLoggedDate.getTime() + " Current Date Time: "+new Date().getTime());
                System.out.println("Transaction : "+transaction);
                System.out.println("*******************************************************************************************");

                if(new Date().before(lastLoggedDate)){

                    // Show phone number in Logcat
                    Log.i("Transaction Adapter ", transaction.toString());
                    // end of while loop

                    long diff = lastLoggedDate.getTime() - new Date().getTime();//as given

                    long diffSeconds = diff / 1000 % 60;
                    long diffMinutes = diff / (60 * 1000) % 60;
                    long diffHours = diff / (60 * 60 * 1000);

                    String duration = parseDuration(diffHours,diffMinutes,diffSeconds);
                    transaction.setRemaining_time(duration);
                    transactions.add(transaction);
                }



            }
            cursor.close();



            for(Transaction t : transactions){

                Vehicle vehicle = new Vehicle();
                vehicle.setVehicleReg(t.getVehicleregNumber());
                vehicle.setBaynumber(t.getBayNumber());
                vehicle.setIn_datetime(getInTimeAsString(t));
                if(t.is_Prepayment()){
                    //Left over balance
                    double leftOverBalance = 0;
                    double amountPaid = 0;

                    ///TODO Absolute this
                    leftOverBalance = Math.abs(t.getUserBalance()) - t.getAmountDue();

                    amountPaid = Math.abs(t.getUserBalance()) - leftOverBalance;
                    vehicle.setDeposit(amountPaid);

                }else{
                    vehicle.setDeposit(t.getAmountPaid());
                }
                vehicles.add(vehicle);
            }


        }

        return vehicles;

    }

    private static String getInTimeAsString(Transaction transaction){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String strInTime = ""+transaction.getIn_datetime();

        Calendar cal = Calendar.getInstance();
        String[] parts= getDateParts(strInTime);

        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));

        Date date = cal.getTime();

        return formatter.format(date);

    }


    public static void updateTransactionDetails(Transaction transaction,Context context){

        ContentValues updateValues = new ContentValues();

        updateValues.put(TransactionsTable.COLUMN_SHIFT_ID,transaction.getShiftId());
        updateValues.put(TransactionsTable.COLUMN_USERNAME, transaction.getUsername());
        updateValues.put(TransactionsTable.COLUMN_TERMINAL_ID, transaction.getTerminalId());
        updateValues.put(TransactionsTable.COLUMN_RECIEPT_NO, transaction.getRecieptNumber());
        updateValues.put(TransactionsTable.COLUMN_PRECINCT_ID, transaction.getPrecinctID());
        updateValues.put(TransactionsTable.COLUMN_BAY_NUMBER, transaction.getBayNumber());
        updateValues.put(TransactionsTable.COLUMN_REQUEST_DATE_TIME, transaction.getRequestDateTime());
        updateValues.put(TransactionsTable.COLUMN_VEHICLE_REG,transaction.getVehicleregNumber());
        updateValues.put(TransactionsTable.COLUMN_DURATION,transaction.getDuration());
        updateValues.put(TransactionsTable.COLUMN_IN_DATETIME, transaction.getIn_datetime());
        updateValues.put(TransactionsTable.COLUMN_TRANSACTION_DATE_TIME, transaction.getTransactionDateTime());

        updateValues.put(TransactionsTable.COLUMN_IS_SYNCED,convertBoolToInt(transaction.is_synced()));
        updateValues.put(TransactionsTable.COLUMN_AMOUNT_DUE,transaction.getAmountDue());
        updateValues.put(TransactionsTable.COLUMN_AMOUNT_PAID,transaction.getAmountPaid());

        updateValues.put(TransactionsTable.COLUMN_TENDER_AMOUNT,transaction.getTenderAmount());
        updateValues.put(TransactionsTable.COLUMN_CHANGE,transaction.getChange());

        updateValues.put(TransactionsTable.COLUMN_TRANSACTION_TYPE,transaction.getTransactionType());

        updateValues.put(TransactionsTable.COLUMN_PAYMENT_MODE_ID,transaction.getPayment_mode_id());
        updateValues.put(TransactionsTable.COLUMN_EXPIRY_DATETIME,transaction.getExpiry_datetime());
        updateValues.put(TransactionsTable.COLUMN_IS_EXPIRED,convertBoolToInt(transaction.is_Expired()));
        updateValues.put(TransactionsTable.COLUMN_IS_PREPAYMENT,convertBoolToInt(transaction.is_Prepayment()));

        updateValues.put(TransactionsTable.COLUMN_IS_PAID,convertBoolToInt(transaction.is_Paid()));
        updateValues.put(TransactionsTable.COLUMN_FINE_REF_NO,transaction.getFineRefNo());
        updateValues.put(TransactionsTable.COLUMN_PAYMENT_REF_NO,transaction.getPaymentRefNo());



        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TransactionsTable.TABLE_TRANSACTIONS);
        System.out.println("Updating Record where Reciept no: "+transaction.getRecieptNumber());

        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                TransactionsTable.COLUMN_RECIEPT_NO+"=?",new String[]{String.valueOf(transaction.getRecieptNumber())});
        //show that it is inserted successfully

        if(rowsAffected>0){
            System.out.println("Successfully Updated This transaction Details : "+transaction.toString());
        }else{
            System.out.println("Failed Update Transaction details : "+transaction.toString());
        }



    }


    public static void setDueFinePayed(double amountPaid ,String receiptNumber,Context context){

        ContentValues updateValues = new ContentValues();

        updateValues.put(TransactionsTable.COLUMN_AMOUNT_PAID,amountPaid);

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TransactionsTable.TABLE_TRANSACTIONS);
        System.out.println("Updating Record where Reciept no: "+receiptNumber);

        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                TransactionsTable.COLUMN_RECIEPT_NO+"=?",new String[]{String.valueOf(receiptNumber)});
        //show that it is inserted successfully

        if(rowsAffected>0){
            System.out.println("Successfully Updated This transaction Details");
        }else{
            System.out.println("Failed Update Transaction details");
        }



    }

    public static List<Transaction> checkIfExpiredNotifyIfNotNotified(Context context){


        List<Transaction> transactions = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.TRANSACTIONS_CONTENT_URI , null, TransactionsTable.COLUMN_TRANSACTION_TYPE+" = '" + "MakeFine" + "' AND "+
                TransactionsTable.COLUMN_IS_NOTIFIED+" = '" + "0" + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.

            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.
                String vehicleReg = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_VEHICLE_REG));
                long expiry_datetime = Long.parseLong(cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_EXPIRY_DATETIME)));
                boolean is_expired = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_EXPIRED)));
                String receiptNumber = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_RECIEPT_NO));

                Transaction transaction = new Transaction();

                transaction.setVehicleregNumber(vehicleReg);
                transaction.setExpiry_datetime(expiry_datetime);
                transaction.setIs_Expired(is_expired);
                transaction.setRecieptNumber(receiptNumber);



                Log.i("Transaction Adapter ", "Checking for Expired Vehicles");

                String expiryDateTime = ""+transaction.getExpiry_datetime();

                Calendar cal = Calendar.getInstance();
                String[] parts= getDateParts(expiryDateTime);
                cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                Date lastLoggedDate = cal.getTime();

                System.out.println("-------------------------------EXPIRY TIME CHECK------------------------------------------");
                System.out.println("Expiry Date Time : "+lastLoggedDate + " Current Date Time: "+new Date());
                System.out.println("Expiry Date Time : "+lastLoggedDate.getTime() + " Current Date Time: "+new Date().getTime());
                System.out.println("Transaction: "+transaction);
                System.out.println("-------------------------------------------------------------------------------------------");

                if(new Date().after(lastLoggedDate)){
                    // Show phone number in Logcat
                    Log.i("Transaction Adapter ", transaction.toString());
                    // end of while loop
                    System.out.println("This vehicle is expiring Now: "+transaction);
                    transaction.setIs_Expired(true);
                    TransactionAdapter.setExpired(transaction,context);
                    transactions.add(transaction);
                }
            }
            cursor.close();

        }

        return transactions;
    }





    private static String parseDuration(long diffHours,long diffMinutes, long diffSeconds){

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





    public static List<Transaction> getAllUnSyncedTransactionsForShift(String shiftID ,Context context){


        List<Transaction> transactions = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.TRANSACTIONS_CONTENT_URI , null, TransactionsTable.COLUMN_IS_SYNCED+" = '" + "0" + "'  AND "+
                TransactionsTable.COLUMN_SHIFT_ID + " = '" +shiftID+ "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.

            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.
                Transaction transaction = new Transaction();

                int id = cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_ID));
                String strshiftID = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_SHIFT_ID));
                System.out.println("Getting Shift ID From DB: "+strshiftID);
                String strusername = cursor.getString (cursor.getColumnIndex (TransactionsTable.COLUMN_USERNAME));
                int terminalId = cursor.getInt (cursor.getColumnIndex (TransactionsTable.COLUMN_TERMINAL_ID));
                String receiptNumber = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_RECIEPT_NO));
                String precinctID = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_PRECINCT_ID));
                int bayNumber = cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_BAY_NUMBER));
                long requestDateTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_REQUEST_DATE_TIME)));
                String vehicleReg = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_VEHICLE_REG));
                int duration = cursor.getInt (cursor.getColumnIndex (TransactionsTable.COLUMN_DURATION));
                long in_datetime = Long.parseLong(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_IN_DATETIME)));
                long transactionDateTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_TRANSACTION_DATE_TIME)));

                boolean is_synced = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_SYNCED)));
                long expiry_date_time = Long.parseLong(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_EXPIRY_DATETIME)));
                double amountDue = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_AMOUNT_DUE));
                double amountPaid = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_AMOUNT_PAID));
                double tenderAmount = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_TENDER_AMOUNT));
                double change = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_CHANGE));
                String transactionType = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_TRANSACTION_TYPE));

                int payment_mode_id = cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_PAYMENT_MODE_ID));
                boolean is_expired = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_EXPIRED)));
                boolean is_prepayment = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_PREPAYMENT)));

                boolean is_Paid = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_PAID)));
                String fineRefNo = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_FINE_REF_NO));
                String paymentRefNo = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_PAYMENT_REF_NO));


                transaction.setId(id);
                transaction.setShiftId(strshiftID);
                transaction.setUsername(strusername);
                transaction.setTerminalId(terminalId);
                transaction.setRecieptNumber(receiptNumber);//needs to be changed
                transaction.setPrecinctID(precinctID);
                transaction.setBayNumber(bayNumber);
                transaction.setRequestDateTime(requestDateTime);
                transaction.setVehicleregNumber(vehicleReg);
                transaction.setDuration(duration);
                transaction.setIn_datetime(in_datetime);
                transaction.setTransactionDateTime(transactionDateTime);

                transaction.setIs_synced(is_synced);

                transaction.setExpiry_datetime(expiry_date_time);
                transaction.setAmountDue(amountDue);
                transaction.setAmountPaid(amountPaid);

                transaction.setTenderAmount(tenderAmount);
                transaction.setChange(change);

                transaction.setTransactionType(transactionType);

                transaction.setPayment_mode_id(payment_mode_id);
                transaction.setIs_Expired(is_expired);
                transaction.setIs_Prepayment(is_prepayment);

                transaction.setIs_Paid(is_Paid);
                transaction.setFineRefNo(fineRefNo);
                transaction.setPaymentRefNo(paymentRefNo);

                // Show phone number in Logcat
                Log.i("Transaction Adapter ", transaction.toString());
                // end of while loop

                transactions.add(transaction);
            }

            cursor.close();
        }

        return transactions;
    }

    public static List<Transaction> getAllTransactionsForUserInShift(String username,String shiftID,Context context){


        List<Transaction> transactions = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.TRANSACTIONS_CONTENT_URI , null, TransactionsTable.COLUMN_USERNAME+" = '" + username + "'  AND "+
                TransactionsTable.COLUMN_SHIFT_ID + " = '" +shiftID+ "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.

            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.
                Transaction transaction = new Transaction();

                int id = cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_ID));
                String strshiftID = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_SHIFT_ID));
                String strusername = cursor.getString (cursor.getColumnIndex (TransactionsTable.COLUMN_USERNAME));
                int terminalId = cursor.getInt (cursor.getColumnIndex (TransactionsTable.COLUMN_TERMINAL_ID));
                String receiptNumber = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_RECIEPT_NO));
                String precinctID = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_PRECINCT_ID));
                int bayNumber = cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_BAY_NUMBER));
                long requestDateTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_REQUEST_DATE_TIME)));
                String vehicleReg = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_VEHICLE_REG));
                int duration = cursor.getInt (cursor.getColumnIndex (TransactionsTable.COLUMN_DURATION));
                long in_datetime = Long.parseLong(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_IN_DATETIME)));
                long transactionDateTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_TRANSACTION_DATE_TIME)));

                boolean is_synced = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_SYNCED)));
                long expiry_date_time = Long.parseLong(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_EXPIRY_DATETIME)));
                double amountDue = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_AMOUNT_DUE));
                double amountPaid = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_AMOUNT_PAID));
                double tenderAmount = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_TENDER_AMOUNT));
                double change = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_CHANGE));
                String transactionType = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_TRANSACTION_TYPE));

                int payment_mode_id = cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_PAYMENT_MODE_ID));
                boolean is_expired = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_EXPIRED)));
                boolean is_prepayment = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_PREPAYMENT)));

                boolean is_Paid = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_PAID)));
                String fineRefNo = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_FINE_REF_NO));
                String paymentRefNo = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_PAYMENT_REF_NO));

                transaction.setId(id);
                transaction.setShiftId(strshiftID);
                transaction.setUsername(strusername);
                transaction.setTerminalId(terminalId);
                transaction.setRecieptNumber(receiptNumber);//needs to be changed
                transaction.setPrecinctID(precinctID);
                transaction.setBayNumber(bayNumber);
                transaction.setRequestDateTime(requestDateTime);
                transaction.setVehicleregNumber(vehicleReg);
                transaction.setDuration(duration);
                transaction.setIn_datetime(in_datetime);
                transaction.setTransactionDateTime(transactionDateTime);

                transaction.setIs_synced(is_synced);

                transaction.setExpiry_datetime(expiry_date_time);
                transaction.setAmountDue(amountDue);
                transaction.setAmountPaid(amountPaid);

                transaction.setTenderAmount(tenderAmount);
                transaction.setChange(change);

                transaction.setTransactionType(transactionType);

                transaction.setPayment_mode_id(payment_mode_id);
                transaction.setIs_Expired(is_expired);
                transaction.setIs_Prepayment(is_prepayment);

                transaction.setIs_Paid(is_Paid);
                transaction.setFineRefNo(fineRefNo);
                transaction.setPaymentRefNo(paymentRefNo);

                // Show phone number in Logcat
                Log.i("Transaction Adapter ", transaction.toString());
                // end of while loop

                transactions.add(transaction);
            }
            cursor.close();

        }

        return transactions;
    }



    public static List<Transaction> getAllSyncedTransactionsForUserInShift(String username,String shiftID,Context context){


        List<Transaction> transactions = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.TRANSACTIONS_CONTENT_URI , null,TransactionsTable.COLUMN_IS_SYNCED+" = '" + "1" + "'  AND "+
                TransactionsTable.COLUMN_USERNAME+" = '" + username + "'  AND "+
                TransactionsTable.COLUMN_SHIFT_ID + " = '" +shiftID+ "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.

            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.
                Transaction transaction = new Transaction();

                int id = cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_ID));
                String strshiftID = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_SHIFT_ID));
                String strusername = cursor.getString (cursor.getColumnIndex (TransactionsTable.COLUMN_USERNAME));
                int terminalId = cursor.getInt (cursor.getColumnIndex (TransactionsTable.COLUMN_TERMINAL_ID));
                String receiptNumber = cursor.getString(cursor.getColumnIndex (TransactionsTable.COLUMN_RECIEPT_NO));
                String precinctID = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_PRECINCT_ID));
                int bayNumber = cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_BAY_NUMBER));
                long requestDateTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_REQUEST_DATE_TIME)));
                String vehicleReg = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_VEHICLE_REG));
                int duration = cursor.getInt (cursor.getColumnIndex (TransactionsTable.COLUMN_DURATION));
                long in_datetime = Long.parseLong(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_IN_DATETIME)));
                long transactionDateTime = Long.parseLong(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_TRANSACTION_DATE_TIME)));

                boolean is_synced = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_SYNCED)));
                long expiry_date_time = Long.parseLong(cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_EXPIRY_DATETIME)));
                double amountDue = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_AMOUNT_DUE));
                double amountPaid = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_AMOUNT_PAID));
                double tenderAmount = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_TENDER_AMOUNT));
                double change = cursor.getDouble(cursor.getColumnIndex(TransactionsTable.COLUMN_CHANGE));

                String transactionType = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_TRANSACTION_TYPE));

                int payment_mode_id = cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_PAYMENT_MODE_ID));
                boolean is_expired = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_EXPIRED)));
                boolean is_prepayment = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_PREPAYMENT)));

                boolean is_Paid = convertIntToBool(cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_IS_PAID)));
                String fineRefNo = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_FINE_REF_NO));
                String paymentRefNo = cursor.getString(cursor.getColumnIndex(TransactionsTable.COLUMN_PAYMENT_REF_NO));

                transaction.setId(id);
                transaction.setShiftId(strshiftID);
                transaction.setUsername(strusername);
                transaction.setTerminalId(terminalId);
                transaction.setRecieptNumber(receiptNumber);//needs to be changed
                transaction.setPrecinctID(precinctID);
                transaction.setBayNumber(bayNumber);
                transaction.setRequestDateTime(requestDateTime);
                transaction.setVehicleregNumber(vehicleReg);
                transaction.setDuration(duration);
                transaction.setIn_datetime(in_datetime);
                transaction.setTransactionDateTime(transactionDateTime);

                transaction.setIs_synced(is_synced);

                transaction.setExpiry_datetime(expiry_date_time);
                transaction.setAmountDue(amountDue);
                transaction.setAmountPaid(amountPaid);

                transaction.setTenderAmount(tenderAmount);
                transaction.setChange(change);

                transaction.setTransactionType(transactionType);

                transaction.setPayment_mode_id(payment_mode_id);
                transaction.setIs_Expired(is_expired);
                transaction.setIs_Prepayment(is_prepayment);

                transaction.setIs_Paid(is_Paid);
                transaction.setFineRefNo(fineRefNo);
                transaction.setPaymentRefNo(paymentRefNo);

                // Show phone number in Logcat
                Log.i("Transaction Adapter ", transaction.toString());
                // end of while loop

                transactions.add(transaction);
            }

        }

        return transactions;
    }



    public static void addTransaction(Transaction transaction, Context context) {


        ContentValues initialValues = new ContentValues();
        initialValues.put(TransactionsTable.COLUMN_SHIFT_ID,""+transaction.getShiftId());
        initialValues.put(TransactionsTable.COLUMN_USERNAME, transaction.getUsername());
        initialValues.put(TransactionsTable.COLUMN_TERMINAL_ID, transaction.getTerminalId());
        initialValues.put(TransactionsTable.COLUMN_RECIEPT_NO, transaction.getRecieptNumber());
        initialValues.put(TransactionsTable.COLUMN_PRECINCT_ID, transaction.getPrecinctID());
        initialValues.put(TransactionsTable.COLUMN_BAY_NUMBER, transaction.getBayNumber());
        initialValues.put(TransactionsTable.COLUMN_REQUEST_DATE_TIME, transaction.getRequestDateTime());
        initialValues.put(TransactionsTable.COLUMN_VEHICLE_REG,transaction.getVehicleregNumber());
        initialValues.put(TransactionsTable.COLUMN_DURATION,transaction.getDuration());
        initialValues.put(TransactionsTable.COLUMN_IN_DATETIME, transaction.getIn_datetime());
        initialValues.put(TransactionsTable.COLUMN_TRANSACTION_DATE_TIME, transaction.getTransactionDateTime());

        initialValues.put(TransactionsTable.COLUMN_IS_SYNCED,convertBoolToInt(transaction.is_synced()));
        initialValues.put(TransactionsTable.COLUMN_AMOUNT_DUE,transaction.getAmountDue());
        initialValues.put(TransactionsTable.COLUMN_AMOUNT_PAID,transaction.getAmountPaid());

        initialValues.put(TransactionsTable.COLUMN_TENDER_AMOUNT,transaction.getTenderAmount());
        initialValues.put(TransactionsTable.COLUMN_CHANGE,transaction.getChange());

        initialValues.put(TransactionsTable.COLUMN_TRANSACTION_TYPE,transaction.getTransactionType());

        initialValues.put(TransactionsTable.COLUMN_PAYMENT_MODE_ID,transaction.getPayment_mode_id());
        initialValues.put(TransactionsTable.COLUMN_EXPIRY_DATETIME,transaction.getExpiry_datetime());
        initialValues.put(TransactionsTable.COLUMN_IS_EXPIRED,convertBoolToInt(transaction.is_Expired()));
        initialValues.put(TransactionsTable.COLUMN_IS_PREPAYMENT,convertBoolToInt(transaction.is_Prepayment()));
        initialValues.put(TransactionsTable.COLUMN_FINE_REF_NO,transaction.getFineRefNo());
        initialValues.put(TransactionsTable.COLUMN_PAYMENT_REF_NO,transaction.getPaymentRefNo());
        initialValues.put(TransactionsTable.COLUMN_IS_PAID,convertBoolToInt(transaction.is_Paid()));



        System.out.println("Adding Transaction : "+transaction.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TransactionsTable.TABLE_TRANSACTIONS);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added A Transaction Successfully");

    }

    public static void setSynced(Transaction transaction,Context context){

        ContentValues updateValues = new ContentValues();

        updateValues.put(TransactionsTable.COLUMN_IS_SYNCED,convertBoolToInt(transaction.is_synced()));

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TransactionsTable.TABLE_TRANSACTIONS);
        System.out.println("Updating Record where Reciept no: "+transaction.getRecieptNumber());

       int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                TransactionsTable.COLUMN_RECIEPT_NO+"=?",new String[]{String.valueOf(transaction.getRecieptNumber())});
        //show that it is inserted successfully

        if(rowsAffected>0){
            System.out.println("Successfully set Transaction to synced : "+transaction.toString());
        }else{
            System.out.println("Failed set Transaction to synced : "+transaction.toString());
        }



    }

    public static void setExpired(Transaction transaction,Context context){

        ContentValues updateValues = new ContentValues();

        updateValues.put(TransactionsTable.COLUMN_IS_EXPIRED,convertBoolToInt(transaction.is_Expired()));

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TransactionsTable.TABLE_TRANSACTIONS);
        System.out.println("Updating Record where Reciept no: "+transaction.getRecieptNumber());

        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                TransactionsTable.COLUMN_RECIEPT_NO+"=?",new String[]{String.valueOf(transaction.getRecieptNumber())});
        //show that it is inserted successfully

        if(rowsAffected>0){
            System.out.println("Successfully set Transaction to Expired : "+transaction.toString());
        }else {
            System.out.println("Failed set Transaction to Expired : " + transaction.toString());
        }
    }

    public static void setNotified(Transaction transaction,Context context){

        ContentValues updateValues = new ContentValues();

        updateValues.put(TransactionsTable.COLUMN_IS_NOTIFIED,convertBoolToInt(transaction.is_Notified()));

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TransactionsTable.TABLE_TRANSACTIONS);
        System.out.println("Updating Record where Reciept no: "+transaction.getRecieptNumber());

        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                TransactionsTable.COLUMN_RECIEPT_NO+"=?",new String[]{String.valueOf(transaction.getRecieptNumber())});
        //show that it is inserted successfully

        if(rowsAffected>0){
            System.out.println("Successfully set Transaction to Expired : "+transaction.toString());
        }else {
            System.out.println("Failed set Transaction to Expired : " + transaction.toString());
        }
    }



    public static int getTransactionID(String in_datetime, Context context){

        int transactionID = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.TRANSACTIONS_CONTENT_URI , null,  TransactionsTable.COLUMN_IN_DATETIME + " = '" + in_datetime + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            System.out.println("Coulnt find userID with that usrname");
            return transactionID;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            System.out.println("Coulnt find userID with that usrname");
            return transactionID;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.
                transactionID  = cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_ID));
                System.out.println("Found Transaction ID: "+transactionID);
            }
        }
        return transactionID;
    }


    public static int getCurrentTransactionID(String transactionDateTime, Context context){

        int transactionID = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.TRANSACTIONS_CONTENT_URI , null,  TransactionsTable.COLUMN_TRANSACTION_DATE_TIME + " = '" + transactionDateTime + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            System.out.println("Coulnt find userID with that usrname");
            return transactionID;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            System.out.println("Coulnt find userID with that usrname");
            return transactionID;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.
                transactionID  = cursor.getInt(cursor.getColumnIndex(TransactionsTable.COLUMN_ID));
                System.out.println("Found Transaction ID: "+transactionID);
            }
        }
        return transactionID;
    }

    public static void updateReceiptNumber(int transactionID,String receiptNo, Context context){

        ContentValues updateValues = new ContentValues();
        updateValues.put(TransactionsTable.COLUMN_RECIEPT_NO,receiptNo);
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TransactionsTable.TABLE_TRANSACTIONS);

        System.out.println("Updating Transaction where Id: "+transactionID);
        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,""+TransactionsTable.COLUMN_ID+" =?",new String[]{String.valueOf(transactionID)});
        //show that it is inserted successfully

        if(rowsAffected>0){
            System.out.println("Updating Transaction where Id: "+transactionID);
        }else{
            System.out.println("Failed updating transaction with ID: "+transactionID);
        }



    }




    public static void deleteTransaction(Transaction transaction,Context context){

        if(transaction!=null){
            Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TransactionsTable.TABLE_TRANSACTIONS);
            int result = context.getContentResolver().delete(contentUri,
                    TransactionsTable.COLUMN_ID+"=?",new String[]{String.valueOf(transaction.getId())});

            System.out.println("Deleted status: "+result);
            if(result==1) {
                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context, "Failed to Delete Transaction", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Not tested
    public static void deleteAll(Context context){

            Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TransactionsTable.TABLE_TRANSACTIONS);
            int result = context.getContentResolver().delete(contentUri,null,null);

            System.out.println("Deleted status: "+result);


    }

    public static void deleteAllTransactionsForShift(String shiftID,Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TransactionsTable.TABLE_TRANSACTIONS);
        int result = context.getContentResolver().delete(contentUri,
                TransactionsTable.COLUMN_SHIFT_ID+"=?",new String[]{shiftID});

        if(result>0){
            System.out.println("Deleted "+result+" Transactions for this shift");
        }else{
            System.out.println("Deleted "+result+" Transactions for this shift");
        }



    }



    public static void refillTransactions(List<Transaction> transactions  , Context context) {

        deleteAll(context);

        for(Transaction transaction : transactions){
            addTransaction(transaction,context);
        }


    }


}
