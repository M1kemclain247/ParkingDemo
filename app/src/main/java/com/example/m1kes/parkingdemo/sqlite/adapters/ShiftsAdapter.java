package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.m1kes.parkingdemo.models.Bay;
import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.BaysTable;
import com.example.m1kes.parkingdemo.sqlite.tables.PrecinctTable;
import com.example.m1kes.parkingdemo.sqlite.tables.ShiftsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.SiteTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TransactionsTable;
import com.example.m1kes.parkingdemo.util.GeneralUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.m1kes.parkingdemo.util.DBUtils.convertBoolToInt;
import static com.example.m1kes.parkingdemo.util.DBUtils.convertIntToBool;

public class ShiftsAdapter {


    public static Shift getShiftForUser(String shiftID, Context context){

        Shift shift = null;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SHIFTS_CONTENT_URI , null,  ShiftsTable.COLUMN_SHIFT_ID + " = '" + shiftID + "'", null, null);

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

                shift = new Shift();

                int id  = cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_ID));
                String tempShiftID = cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_SHIFT_ID));
                String username = cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_USERNAME));
                int precinctID = cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_PRECINCT_ID));
                int terminalID = cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_TERMINAL_ID));
                String precinctZoneName = cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_PRECINCT_ZONENAME));
                String precinctName = cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_PRECINCT_NAME));
                long shiftStartTime = Long.valueOf(cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_START_TIME)));
                long endTime = Long.valueOf(cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_START_TIME)));
                boolean isActive = convertIntToBool(cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_IS_ACTIVE)));
                boolean isStartSynced = convertIntToBool(cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_IS_START_SYNCED)));
                boolean isClosedSynced = convertIntToBool(cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_IS_CLOSE_SYNCED)));


                double total_declaredAmount = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TOTAL_DECLARED_AMOUNT));
                double cash_collected = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_CASH_COLLECTED));
                double total_collected = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TOTAL_COLLECTED));
                double total_cashed = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TOTAL_CASHED));
                double ePayments = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_EPAYMENTS));
                double total_arrears = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TOTAL_ARREARS));
                double total_prepaid = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TOTAL_PREPAID));
                double ticketissued_amount = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TICKET_ISSUED_AMOUNT));


                shift.setId(id);
                shift.setShiftId(tempShiftID);
                shift.setUsername(username);
                shift.setPrecinctID(precinctID);
                shift.setPrecinctZoneName(precinctZoneName);
                shift.setPrecinctName(precinctName);
                shift.setTerminalID(terminalID);
                shift.setStart_time(shiftStartTime);
                shift.setActive(isActive);
                shift.setStartShiftSynced(isStartSynced);
                shift.setCloseShiftSynced(isClosedSynced);
                //get amounts
                shift.setTotal_declaredAmount(total_declaredAmount);
                shift.setCash_collected(cash_collected);
                shift.setTotal_collected(total_collected);
                shift.setTotal_cashed(total_cashed);
                shift.setePayments(ePayments);
                shift.setTotal_arrears(total_arrears);
                shift.setTotal_prepaid(total_prepaid);
                shift.setTicketissued_amount(ticketissued_amount);


                // Show phone number in Logcat
                System.out.println("Loading Shift ID: "+id);
                // end of while loop

                System.out.println("Getting shift :"+shift);


            }
        cursor.close();
        }

        //If the shift was found and is active then return it otherwise return null if couldnt find shift active for user
        if(shift!=null){
                return shift;
        }else{
            return null;
        }
    }


    public static List<Shift> getAllLocalShifts(Context context){

        List<Shift> shifts = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SHIFTS_CONTENT_URI , null, null, null, null);

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

                Shift shift = new Shift();

                int id  = cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_ID));
                String tempShiftID = cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_SHIFT_ID));
                String username = cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_USERNAME));
                int precinctID = cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_PRECINCT_ID));
                int terminalID = cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_TERMINAL_ID));
                String precinctZoneName = cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_PRECINCT_ZONENAME));
                String precinctName = cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_PRECINCT_NAME));
                long shiftStartTime = Long.valueOf(cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_START_TIME)));
                String strEndTime = cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_END_TIME));
                long endTime;
                if(strEndTime!=null) {
                   endTime = Long.valueOf(cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_END_TIME)));
                }else{
                    endTime = 0;
                }
                boolean isActive = convertIntToBool(cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_IS_ACTIVE)));
                boolean isStartSynced = convertIntToBool(cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_IS_START_SYNCED)));
                boolean isClosedSynced = convertIntToBool(cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_IS_CLOSE_SYNCED)));


                double total_declaredAmount = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TOTAL_DECLARED_AMOUNT));
                double cash_collected = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_CASH_COLLECTED));
                double total_collected = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TOTAL_COLLECTED));
                double total_cashed = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TOTAL_CASHED));
                double ePayments = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_EPAYMENTS));
                double total_arrears = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TOTAL_ARREARS));
                double total_prepaid = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TOTAL_PREPAID));
                double ticketissued_amount = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TICKET_ISSUED_AMOUNT));


                shift.setId(id);
                shift.setShiftId(tempShiftID);
                shift.setUsername(username);
                shift.setPrecinctID(precinctID);
                shift.setPrecinctZoneName(precinctZoneName);
                shift.setPrecinctName(precinctName);
                shift.setTerminalID(terminalID);
                shift.setStart_time(shiftStartTime);
                shift.setEnd_time(endTime);
                shift.setActive(isActive);
                shift.setStartShiftSynced(isStartSynced);
                shift.setCloseShiftSynced(isClosedSynced);
                //get amounts
                shift.setTotal_declaredAmount(total_declaredAmount);
                shift.setCash_collected(cash_collected);
                shift.setTotal_collected(total_collected);
                shift.setTotal_cashed(total_cashed);
                shift.setePayments(ePayments);
                shift.setTotal_arrears(total_arrears);
                shift.setTotal_prepaid(total_prepaid);
                shift.setTicketissued_amount(ticketissued_amount);


                System.out.println("Getting shift :"+shift);

                shifts.add(shift);

            }
            cursor.close();
        }

        return shifts;
    }



    public static void createShift(Shift shift, Context context) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(ShiftsTable.COLUMN_USERNAME,shift.getUsername());
        initialValues.put(ShiftsTable.COLUMN_PRECINCT_ID, shift.getPrecinctID());
        initialValues.put(ShiftsTable.COLUMN_PRECINCT_ZONENAME, shift.getPrecinctZoneName());
        initialValues.put(ShiftsTable.COLUMN_PRECINCT_NAME, shift.getPrecinctName());
        initialValues.put(ShiftsTable.COLUMN_TERMINAL_ID, shift.getTerminalID());
        initialValues.put(ShiftsTable.COLUMN_START_TIME, shift.getStart_time());
        initialValues.put(ShiftsTable.COLUMN_IS_START_SYNCED,convertBoolToInt(false));
        initialValues.put(ShiftsTable.COLUMN_IS_ACTIVE, convertBoolToInt(true));

        //SET THESE ALL TO 0 WHEN STARTING A NEW SHIFT SO THAT WHEN CASHING UP THERE WONT BE ERRORS
        initialValues.put(ShiftsTable.COLUMN_TOTAL_DECLARED_AMOUNT, 0);
        initialValues.put(ShiftsTable.COLUMN_CASH_COLLECTED, 0);
        initialValues.put(ShiftsTable.COLUMN_TOTAL_COLLECTED,0);
        initialValues.put(ShiftsTable.COLUMN_TOTAL_CASHED, 0);
        initialValues.put(ShiftsTable.COLUMN_EPAYMENTS, 0);
        initialValues.put(ShiftsTable.COLUMN_TOTAL_ARREARS, 0);
        initialValues.put(ShiftsTable.COLUMN_TOTAL_PREPAID, 0);
        initialValues.put(ShiftsTable.COLUMN_TICKET_ISSUED_AMOUNT, 0);

        System.out.println("*************************************");
        System.out.println("Adding Shift to DB : "+shift.toString());
        System.out.println("*************************************");
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, ShiftsTable.TABLE_SHIFTS);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);


        System.out.println("Started Shift Successfully");

    }



    public static int endShift(Shift shift,Context context){

        ContentValues updateValues = new ContentValues();

        updateValues.put(ShiftsTable.COLUMN_IS_ACTIVE,convertBoolToInt(false));
        updateValues.put(ShiftsTable.COLUMN_IS_CLOSE_SYNCED,convertBoolToInt(false));
        updateValues.put(ShiftsTable.COLUMN_END_TIME, shift.getEnd_time());
        updateValues.put(ShiftsTable.COLUMN_TOTAL_DECLARED_AMOUNT, shift.getTotal_declaredAmount());

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI,ShiftsTable.TABLE_SHIFTS);

        System.out.println("Trying to close shift with ID: "+shift.getShiftId() );

        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                ShiftsTable.COLUMN_SHIFT_ID+"=?",new String[]{shift.getShiftId()});

        if(rowsAffected>0){
            System.out.println("Successfully closed This Shift ");
        }else{
            System.out.println("Failed to Close this shift");
        }
            if(rowsAffected>0){
                System.out.println("*************************************");
                System.out.println(" ********************************");
                System.out.println(" Closed Shift Successfully");
                System.out.println(" ********************************");
            }else{
                System.out.println(" ***************************");
                System.out.println(" FAILED Closing Shift");
                System.out.println(" ***************************");
            }
            return rowsAffected;

    }

    public static int addTransactionAmount(String shiftID,double amount,Context context){

        ContentValues updateValues = new ContentValues();
        updateValues.put(ShiftsTable.COLUMN_TOTAL_COLLECTED,amount);
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI,ShiftsTable.TABLE_SHIFTS);

        System.out.println("Trying to set shift to synced with ID: "+shiftID);
        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                ShiftsTable.COLUMN_SHIFT_ID+"=?",new String[]{shiftID});

        if(rowsAffected>0){
            System.out.println("Successfully added amount to Shift ");
        }else{
            System.out.println("Failed to add amount to this shift");
        }

        return rowsAffected;
    }

    public static double getCurrentShiftTotalAmount(String shiftID, Context context){

        double amount = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SHIFTS_CONTENT_URI , null,  ShiftsTable.COLUMN_SHIFT_ID + " = '" + shiftID+"'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return amount;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return amount;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.
            while (cursor.moveToNext()) {

                amount  = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TOTAL_COLLECTED));
                System.out.println("Loading Shift Total Current Amount: "+amount);
            }
            cursor.close();
        }
        return amount;
    }
    public static void updateShiftAmountCollected(String shiftID ,double amountToAdd,Context context){

        double currentTotal =  getCurrentShiftTotalAmount(shiftID,context);
        double newTotal = currentTotal + amountToAdd;

        System.out.println("Current Shift Balance: "+currentTotal);
        System.out.println("Adding amount: "+amountToAdd);
        System.out.println("New Shift Balance: "+newTotal);
        addTransactionAmount(shiftID,newTotal,context);
    }



    public static int addShiftCashAmount(String shiftID,double amount,Context context){

        ContentValues updateValues = new ContentValues();
        updateValues.put(ShiftsTable.COLUMN_CASH_COLLECTED,amount);
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI,ShiftsTable.TABLE_SHIFTS);


        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                ShiftsTable.COLUMN_SHIFT_ID+"=?",new String[]{shiftID});

        if(rowsAffected>0){
            System.out.println("Successfully added Cash amount to Shift ");
        }else{
            System.out.println("Failed to add Cash amount to this shift");
        }

        return rowsAffected;
    }

    public static double getCurrentShiftCashAmount(String shiftID, Context context){

        double cashCollected = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SHIFTS_CONTENT_URI , null,  ShiftsTable.COLUMN_SHIFT_ID + " = '" + shiftID+"'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return cashCollected;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return cashCollected;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.
            while (cursor.moveToNext()) {

                cashCollected  = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_CASH_COLLECTED));
                System.out.println("Loading Shift Cash Current Amount: "+cashCollected);
            }
            cursor.close();
        }
        return cashCollected;
    }


    public static void updateShiftCashCollected(String shiftID ,double amountToAdd,Context context){

        double currentTotal =  getCurrentShiftCashAmount(shiftID,context);
        double newTotal = currentTotal + amountToAdd;

        System.out.println("Current Shift Cash Balance: "+currentTotal);
        System.out.println("Adding Cash amount: "+amountToAdd);
        System.out.println("New Shift Cash Balance: "+newTotal);
        //Add to shift Cash total
        addShiftCashAmount(shiftID,newTotal,context);
        //Add to shift total collected
        updateShiftAmountCollected(shiftID,amountToAdd,context);
    }


    public static int addEPaymentsAmount(String shiftID,double amount,Context context){

        ContentValues updateValues = new ContentValues();
        updateValues.put(ShiftsTable.COLUMN_EPAYMENTS,amount);
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI,ShiftsTable.TABLE_SHIFTS);


        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                ShiftsTable.COLUMN_SHIFT_ID+"=?",new String[]{shiftID});

        if(rowsAffected>0){
            System.out.println("Successfully added E-payments amount to Shift ");
        }else{
            System.out.println("Failed to add E-payments amount to this shift");
        }

        return rowsAffected;
    }

    public static double getCurrentShiftEPaymentAmount(String shiftID, Context context){

        double epaymentsAmount = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SHIFTS_CONTENT_URI , null,  ShiftsTable.COLUMN_SHIFT_ID + " = '" + shiftID+"'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return epaymentsAmount;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return epaymentsAmount;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.
            while (cursor.moveToNext()) {

                epaymentsAmount  = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_EPAYMENTS));
                System.out.println("Loading Shift E-payments Current Amount: "+epaymentsAmount);
            }
            cursor.close();
        }
        return epaymentsAmount;
    }

    public static void updateShiftEpaymentsCollected(String shiftID ,double amountToAdd,Context context){

        double currentTotal =  getCurrentShiftEPaymentAmount(shiftID,context);
        double newTotal = currentTotal + amountToAdd;

        System.out.println("Current Shift E-Payment Balance: "+currentTotal);
        System.out.println("Adding E-Payment amount: "+amountToAdd);
        System.out.println("New Shift E-Payment Balance: "+newTotal);
        //Add to shift Cash total
        addEPaymentsAmount(shiftID,newTotal,context);
        //Add to shift total collected
        updateShiftAmountCollected(shiftID,amountToAdd,context);
    }

    /**
     * PREPAYMENTS
     * **/

    public static void updateShiftPrepaidAmountCollected(String shiftID ,double prepaidAmountToAdd,Context context){

        double currentTotal =  getCurrentShiftTotalPrepaid(shiftID,context);
        double newTotal = currentTotal + prepaidAmountToAdd;

        System.out.println("Current Shift Prepaid Balance: "+currentTotal);
        System.out.println("Adding Prepaid amount: "+prepaidAmountToAdd);
        System.out.println("New Shift Prepaid Balance: "+newTotal);
        addPrepaidTransactionAmount(shiftID,newTotal,context);
    }


    public static double getCurrentShiftTotalPrepaid(String shiftID, Context context){

        double prepaidAmount = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SHIFTS_CONTENT_URI , null,  ShiftsTable.COLUMN_SHIFT_ID + " = '" + shiftID+"'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return prepaidAmount;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return prepaidAmount;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.
            while (cursor.moveToNext()) {

                prepaidAmount  = cursor.getDouble(cursor.getColumnIndex(ShiftsTable.COLUMN_TOTAL_PREPAID));
                System.out.println("Loading Shift Total Current Prepaid Amount: "+prepaidAmount);
            }
            cursor.close();
        }
        return prepaidAmount;
    }

    public static int addPrepaidTransactionAmount(String shiftID,double prepaidAmount,Context context){

        ContentValues updateValues = new ContentValues();
        updateValues.put(ShiftsTable.COLUMN_TOTAL_PREPAID,prepaidAmount);
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI,ShiftsTable.TABLE_SHIFTS);

        System.out.println("Trying to set shift to synced with ID: "+shiftID);
        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                ShiftsTable.COLUMN_SHIFT_ID+"=?",new String[]{shiftID});

        if(rowsAffected>0){
            System.out.println("Successfully added Prepaid amount to Shift ");
        }else{
            System.out.println("Failed to add prepaid amount to this shift");
        }

        return rowsAffected;
    }





    public static int setShiftCloseSynced(String shiftID,Context context){

        ContentValues updateValues = new ContentValues();

        updateValues.put(ShiftsTable.COLUMN_IS_CLOSE_SYNCED,convertBoolToInt(true));
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI,ShiftsTable.TABLE_SHIFTS);

        System.out.println("Trying to set shift to synced with ID: "+shiftID);
        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                ShiftsTable.COLUMN_SHIFT_ID+"=?",new String[]{shiftID});

        if(rowsAffected>0){
            System.out.println("Successfully Synced Closing of This Shift ");
        }else{
            System.out.println("Failed to Sync Closing of this shift");
        }

        return rowsAffected;

    }

    public static int setShiftStartSynced(String shiftID,Context context){

        ContentValues updateValues = new ContentValues();

        updateValues.put(ShiftsTable.COLUMN_IS_START_SYNCED,convertBoolToInt(true));
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI,ShiftsTable.TABLE_SHIFTS);

        System.out.println("Trying to set shift to synced with ID: "+shiftID);
        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                ShiftsTable.COLUMN_SHIFT_ID+"=?",new String[]{shiftID});

        if(rowsAffected>0){
            System.out.println("Successfully Synced Starting of This Shift ");
        }else{
            System.out.println("Failed to Sync Starting of this shift");
        }

        return rowsAffected;

    }

    public static int setCodedShiftID(int localID,String shiftID,Context context){

        ContentValues updateValues = new ContentValues();
        updateValues.put(ShiftsTable.COLUMN_SHIFT_ID,shiftID);

        System.out.println("Local ID to update:"+localID);
        System.out.println("Shift ID:"+shiftID);

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI,ShiftsTable.TABLE_SHIFTS);

        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,
                ShiftsTable.COLUMN_ID+"=?",new String[]{String.valueOf(localID)});
        if(rowsAffected>0){
            System.out.println("Successfully updated the coded Shift ID ");
        }else{
            System.out.println("Failed to update the coded shiftID");
        }
        return rowsAffected;

    }


   /* public static int getShiftIDForUser(String username, Context context){

        int id = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SHIFTS_CONTENT_URI , null,  ShiftsTable.COLUMN_USERNAME + " = '" + username + "' AND "+
                ShiftsTable.COLUMN_IS_ACTIVE+" = '1'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return 0;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return 0;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {

                id  = cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_ID));
                System.out.println("Loading Shift local ID: "+id);
            }
            cursor.close();
        }
        return id;
    }
*/
    public static String getGeneratedShiftID(int localID, Context context){

        String shiftID = null;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SHIFTS_CONTENT_URI , null,  ShiftsTable.COLUMN_ID + " = '" + localID + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return shiftID;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return shiftID;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {

                shiftID  = cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_SHIFT_ID));
                System.out.println("Loading Shift ID: "+shiftID);
            }
            cursor.close();
        }
        return shiftID;
    }

    public static int getShiftIDForNewShift(String username,long shift_start_time, Context context){

        int id = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SHIFTS_CONTENT_URI , null,  ShiftsTable.COLUMN_USERNAME + " = '" + username + "' AND "+
                ShiftsTable.COLUMN_IS_ACTIVE+" = '1' AND "+ShiftsTable.COLUMN_START_TIME+" = '"+shift_start_time+"'", null, null);
        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return 0;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return 0;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.

                id  = cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_ID));
                // Show phone number in Logcat
                System.out.println("Loading Shift ID: "+id);
                // end of while loop
            }
            cursor.close();
        }
        return id;
    }

    public static int checkIfActiveShifts(Context context){

        int numActiveShifts = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SHIFTS_CONTENT_URI , null,
                ShiftsTable.COLUMN_IS_ACTIVE+" = '1'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return 0;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return 0;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.

                String username = cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_USERNAME));
                System.out.println("Found Active shift with Username: "+username);
                // end of while loop
                numActiveShifts++;

            }
            cursor.close();

        }



        return numActiveShifts;
    }



    public static String getZoneForUser(String username, Context context){

        String zonename = null;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SHIFTS_CONTENT_URI , null,  ShiftsTable.COLUMN_USERNAME + " = '" + username + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return null;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return null;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.

                zonename  = cursor.getString(cursor.getColumnIndex(ShiftsTable.COLUMN_PRECINCT_ZONENAME));

                // Show phone number in Logcat
                System.out.println("Loading Zone Name:  "+zonename+" For User: "+username);
                // end of while loop
            }
            cursor.close();
        }



        return zonename;
    }


    public static int getPrecinctIDForUser(String username, Context context){

        int id = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SHIFTS_CONTENT_URI , null,  ShiftsTable.COLUMN_USERNAME + " = '" + username + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return 0;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return 0;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.

                id  = cursor.getInt(cursor.getColumnIndex(ShiftsTable.COLUMN_PRECINCT_ID));

                // Show phone number in Logcat
                System.out.println("Loading Precinct ID for user "+username+" : "+id);
                // end of while loop
            }
            cursor.close();
        }



        return id;
    }


    public static String makePaddedShiftID(Shift shift,int localID,Context context){

        String terminalID = String.valueOf(shift.getTerminalID());
        String shiftLocalID = String.valueOf(localID);
        String marshalID = String.valueOf(UserAdapter.getUserEmpNo(shift.getUsername(),context));
        String precinctID = String.valueOf(shift.getPrecinctID());



        String shiftIdNew,terminalIDnew,marshalIDnew,precinctIDnew;
        System.out.println(shiftLocalID.length());

        //Date
        String date = new SimpleDateFormat("yyMMdd").format(new Date());

        //TerminalID
        terminalIDnew = terminalID.trim();
        if(terminalIDnew.length()== 1){
            terminalIDnew= "00"+terminalIDnew;

        }else if(terminalIDnew.length()== 2){
            System.out.println("else if of emp id");
            terminalIDnew= "0"+terminalIDnew;
        }

        //UserID/MarhsalID
        marshalIDnew = marshalID.trim();
        if(marshalIDnew.length()== 1){
            marshalIDnew= "00"+marshalIDnew;
        }else if(marshalIDnew.length()== 2){
            System.out.println("else if of emp id");
            marshalIDnew= "0"+marshalIDnew;
        }

        //PrecinctID
        precinctIDnew = precinctID.trim();
        if(precinctIDnew.length()== 1){
            precinctIDnew = "00"+precinctIDnew;
        }else if(precinctIDnew.length()== 2){
            precinctIDnew= "0"+precinctIDnew;
        }

        //LocalShiftID
        shiftIdNew = shiftLocalID.trim();
        if(shiftIdNew.length()== 1){

            shiftIdNew = "00"+shiftIdNew;
            System.out.println("if stmnt");
            System.out.println(shiftIdNew);

        }else if(shiftIdNew.length()== 2){

            shiftIdNew= "0"+shiftIdNew;
            System.out.println("else if 1 stmnt");
        }

        System.out.println("******************");
        System.out.println("Details Padded:\n");
        System.out.println(date);
        System.out.println(terminalIDnew);
        System.out.println(marshalIDnew);
        System.out.println(precinctIDnew);
        System.out.println(shiftIdNew);
        System.out.println("******************");


        //return "A"+date+terminalIDnew+marshalIDnew+precinctIDnew+shiftIdNew;
        return "A"+terminalIDnew+marshalIDnew+precinctIDnew+shiftIdNew;
    }



    public static void deleteShift(String shiftID,Context context){

        System.out.println("Trying to delete shift from local db with ShiftID : "+shiftID);

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, ShiftsTable.TABLE_SHIFTS);
        int result = context.getContentResolver().delete(contentUri,
                ShiftsTable.COLUMN_SHIFT_ID+"=?",new String[]{shiftID});

        System.out.println("Rows Affected: "+result);
        if(result>0){
            System.out.println("Successfully deleted shift from local DB");
        }else{
            System.out.println("Failed to delete shift from local DB");
        }




    }



}
