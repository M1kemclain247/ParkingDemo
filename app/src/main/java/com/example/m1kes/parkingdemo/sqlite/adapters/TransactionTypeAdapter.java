package com.example.m1kes.parkingdemo.sqlite.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.models.TransactionType;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.TransactionTypeTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TransactionsTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.example.m1kes.parkingdemo.util.DBUtils.*;


public class TransactionTypeAdapter {

    /**
     * used to return a List of all the Transactions in the Database
     *
     * @param context
     * @return List<Transaction> from the database
     */
    public static List<TransactionType> getAllTransactionTypes(Context context){

        List<TransactionType> transactionTypes = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.TRANSACTION_TYPE_CONTENT_URI , null, null, null, null);

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
                TransactionType transactionType = new TransactionType();

                int id = cursor.getInt(cursor.getColumnIndex(TransactionTypeTable.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndex(TransactionTypeTable.COLUMN_NAME));

                // Show Debug info in Logcat
                Log.i("TransactionType Adapter", transactionType.toString());

                transactionType.setId(id);
                transactionType.setName(name);
                //Add to list of all the types
                transactionTypes.add(transactionType);
            }

        }

        return transactionTypes;
    }

    public static void addTransaction(TransactionType transactionType, Context context) {


        ContentValues initialValues = new ContentValues();
        initialValues.put(TransactionTypeTable.COLUMN_ID,transactionType.getId());
        initialValues.put(TransactionTypeTable.COLUMN_NAME, transactionType.getName());

        System.out.println("Adding Transaction TYPE : "+transactionType.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TransactionTypeTable.TABLE_TRANSACTION_TYPES);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added A Transaction TYPE Successfully");

    }


    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TransactionTypeTable.TABLE_TRANSACTION_TYPES);
        int result = context.getContentResolver().delete(contentUri,null,null);

        System.out.println("Deleted status: "+result);

    }

    public static void refillTransactionTypes(List<TransactionType> transactiontypes  , Context context) {

        deleteAll(context);

        for(TransactionType transaction : transactiontypes){
            addTransaction(transaction,context);
        }


    }

}
