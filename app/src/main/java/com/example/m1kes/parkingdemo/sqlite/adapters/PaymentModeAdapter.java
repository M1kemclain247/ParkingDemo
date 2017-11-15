package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.m1kes.parkingdemo.models.Bay;
import com.example.m1kes.parkingdemo.models.PaymentMode;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.BaysTable;
import com.example.m1kes.parkingdemo.sqlite.tables.PaymentModesTable;
import com.example.m1kes.parkingdemo.sqlite.tables.PrinterTable;

import java.util.ArrayList;
import java.util.List;

public class PaymentModeAdapter {

    public static List<PaymentMode> getAllPaymentTypes(Context context){

        List<PaymentMode> paymentModes = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.PAYMENT_MODES_CONTENT_URI , null, null, null, null);

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
                PaymentMode paymentMode = new PaymentMode();

                int id = cursor.getInt(cursor.getColumnIndex(PaymentModesTable.COLUMN_ID));
                String name = cursor.getString (cursor.getColumnIndex (PaymentModesTable.COLUMN_NAME));
                paymentMode.setId(id);
                paymentMode.setName(name);

                // Show phone number in Logcat
                System.out.println("Loading Payment methods from DB: "+paymentMode.toString());
                // end of while loop

                paymentModes.add(paymentMode);
            }

        }

        return paymentModes;
    }


    public static void addPaymentMethod(PaymentMode paymentMode, Context context) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(BaysTable.COLUMN_ID, paymentMode.getId());
        initialValues.put(PaymentModesTable.COLUMN_NAME,paymentMode.getName());

        System.out.println("Adding PaymentMethod : "+paymentMode.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, PaymentModesTable.TABLE_PAYMENT_MODES);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added A PaymentMethod Successfully");

    }

    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, PaymentModesTable.TABLE_PAYMENT_MODES);
        int result = context.getContentResolver().delete(contentUri,null,null);

        System.out.println("Deleted status: "+result);


    }


    public static void refillPaymentMethods(List<PaymentMode> paymentModes, Context context) {
        deleteAll(context);
        for(PaymentMode mode : paymentModes){
            addPaymentMethod(mode,context);
        }
    }


    public static int getPaymentModeID(String name,Context context){

        int paymentModeId = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.PAYMENT_MODES_CONTENT_URI , null,
                PaymentModesTable.COLUMN_NAME+" = '" + name + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.

            // If the Cursor is empty, the provider found no matches
            System.out.println("Unable to find payment method");
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            System.out.println("Unable to find payment method ID try search again");
        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.
            System.out.println("Unable to find payment method ID");

            while (cursor.moveToNext()) {
                // Gets the value from the column.

                paymentModeId = cursor.getInt (cursor.getColumnIndex (PaymentModesTable.COLUMN_ID));
                // Show phone number in Logcat
                System.out.println("Found Payment Method in DB: "+paymentModeId);
                // end of while loop
            }

        }

        return paymentModeId;
    }




}
