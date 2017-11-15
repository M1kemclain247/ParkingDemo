package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.models.Bay;
import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.models.Zone;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.BaysTable;
import com.example.m1kes.parkingdemo.sqlite.tables.PrecinctTable;
import com.example.m1kes.parkingdemo.sqlite.tables.PrinterTable;
import com.example.m1kes.parkingdemo.sqlite.tables.ZonesTable;

import java.util.ArrayList;
import java.util.List;

public class PrecinctAdapter {

    /**
     * used to return a List of all the Precincts in the Database
     * @param context
     * @return List<Precinct> from the database
     */
    public static List<Precinct> getAllPrecincts(Context context){


        List<Precinct> precincts = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.PRECINCT_CONTENT_URI , null, null, null, null);

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
                Precinct precinct = new Precinct();

                int id = cursor.getInt(cursor.getColumnIndex(PrecinctTable.COLUMN_ID));
                String zoneName = cursor.getString (cursor.getColumnIndex (PrecinctTable.COLUMN_ZONE_NAME));
                String name = cursor.getString (cursor.getColumnIndex (PrecinctTable.COLUMN_NAME));
                double target = cursor.getDouble (cursor.getColumnIndex (PrecinctTable.COLUMN_TARGET));
                String lunch = cursor.getString (cursor.getColumnIndex (PrecinctTable.COLUMN_LUNCH));
                double amount = cursor.getDouble(cursor.getColumnIndex(PrecinctTable.COLUMN_AMOUNT));


                precinct.setId(id);
                precinct.setZoneName(zoneName);
                precinct.setName(name);
                precinct.setTarget(target);
                precinct.setLunch(lunch);
                precinct.setAmount(amount);

                // Show phone number in Logcat
                System.out.println("Loading from DB: "+precinct.toString());
                // end of while loop

                precincts.add(precinct);
            }

        }

        return precincts;
    }


    /**
     * used to return a List of all the Precincts in the Database
     * @param context
     * @return List<Precinct> from the database
     */
    public static List<Precinct> getPrecinctsFromZone(Zone zone, Context context){

        List<Precinct> precincts = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.PRECINCT_CONTENT_URI , null,  PrecinctTable.COLUMN_ZONE_NAME + " = '" + zone.getName() + "'", null, null);

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
                Precinct precinct = new Precinct();

                int id = cursor.getInt(cursor.getColumnIndex(PrecinctTable.COLUMN_ID));
                String zoneName = cursor.getString (cursor.getColumnIndex (PrecinctTable.COLUMN_ZONE_NAME));
                String name = cursor.getString (cursor.getColumnIndex (PrecinctTable.COLUMN_NAME));
                double target = cursor.getDouble (cursor.getColumnIndex (PrecinctTable.COLUMN_TARGET));
                String lunch = cursor.getString (cursor.getColumnIndex (PrecinctTable.COLUMN_LUNCH));
                double amount = cursor.getDouble(cursor.getColumnIndex(PrecinctTable.COLUMN_AMOUNT));

                precinct.setId(id);
                precinct.setZoneName(zoneName);
                precinct.setName(name);
                precinct.setTarget(target);
                precinct.setLunch(lunch);
                precinct.setAmount(amount);

                // Show phone number in Logcat
                System.out.println("Loading from DB: "+precinct.toString());
                // end of while loop

                precincts.add(precinct);
            }

        }

        return precincts;
    }


    public static Precinct getPrecinctWithID(String precinctID, Context context){

        Precinct precinct = null;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.PRECINCT_CONTENT_URI , null,  PrecinctTable.COLUMN_ID + " = '" + precinctID + "'", null, null);

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
                precinct = new Precinct();


                int id = cursor.getInt(cursor.getColumnIndex(PrecinctTable.COLUMN_ID));
                String zoneName = cursor.getString (cursor.getColumnIndex (PrecinctTable.COLUMN_ZONE_NAME));
                String name = cursor.getString (cursor.getColumnIndex (PrecinctTable.COLUMN_NAME));
                double target = cursor.getDouble (cursor.getColumnIndex (PrecinctTable.COLUMN_TARGET));
                String lunch = cursor.getString (cursor.getColumnIndex (PrecinctTable.COLUMN_LUNCH));
                double amount = cursor.getDouble(cursor.getColumnIndex(PrecinctTable.COLUMN_AMOUNT));

                precinct.setId(id);
                precinct.setZoneName(zoneName);
                precinct.setName(name);
                precinct.setTarget(target);
                precinct.setLunch(lunch);
                precinct.setAmount(amount);

                // Show phone number in Logcat
                System.out.println("Loading from DB: "+precinct.toString());
                // end of while loop

            }

        }

        return precinct;


    }


    public static double getPrecinctTarrif(String precinctID, Context context){

        double tarriffAmount = 0.00;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.PRECINCT_CONTENT_URI , null,  PrecinctTable.COLUMN_ID + " = '" + precinctID + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return tarriffAmount;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return tarriffAmount;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.
            while (cursor.moveToNext()) {
                // Gets the value from the column.
                tarriffAmount = cursor.getDouble(cursor.getColumnIndex(PrecinctTable.COLUMN_AMOUNT));
                System.out.println("Loading Tarrif for Precinct ID :"+precinctID+" "+tarriffAmount);
            }
        }
        return tarriffAmount;
    }






    public static void addPrecinct(Precinct precinct, Context context) {
        String format = "yyyy-MM-dd HH:mm:ss";

        ContentValues initialValues = new ContentValues();
        initialValues.put(PrecinctTable.COLUMN_ID, precinct.getId());
        initialValues.put(PrecinctTable.COLUMN_ZONE_NAME,precinct.getZoneName());
        initialValues.put(PrecinctTable.COLUMN_NAME, precinct.getName());
        initialValues.put(PrecinctTable.COLUMN_TARGET, precinct.getTarget());
        initialValues.put(PrecinctTable.COLUMN_LUNCH, precinct.getLunch());
        initialValues.put(PrecinctTable.COLUMN_AMOUNT,precinct.getAmount());

        System.out.println("Adding Precinct : "+precinct.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, PrecinctTable.TABLE_PRECINCTS);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added A Precinct Successfully");

    }
    //Not tested
    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, PrecinctTable.TABLE_PRECINCTS);
        int result = context.getContentResolver().delete(contentUri,null,null);

        System.out.println("Deleted status: "+result);

    }

    public static void refillPrecincts(List<Precinct> precincts, Context context) {


        deleteAll(context);
        for(Precinct precinct : precincts){
            addPrecinct(precinct,context);
        }


    }


}
