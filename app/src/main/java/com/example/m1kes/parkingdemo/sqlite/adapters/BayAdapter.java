package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.models.Bay;
import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Tarriff;
import com.example.m1kes.parkingdemo.models.Zone;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.BaysTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TarriffsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.ZonesTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.m1kes.parkingdemo.util.DBUtils.convertIntToBool;
import static com.example.m1kes.parkingdemo.util.DBUtils.getDateFromDateTime;
import static com.example.m1kes.parkingdemo.util.DBUtils.parseDate;

public class BayAdapter {

    /**
     * used to return a List of all the Bay in the Database
     * @param context
     * @return List<Bay> from the database
     */
    public static List<Bay> getAllBays(Context context){

        String dateTimeParseFormat = "dd-MM-yyyy HH:mm:ss";
        List<Bay> bays = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.BAYS_CONTENT_URI , null, null, null, null);

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
                Bay bay = new Bay();

                int id = cursor.getInt(cursor.getColumnIndex(BaysTable.COLUMN_ID));
                int precinctId = cursor.getInt (cursor.getColumnIndex (BaysTable.COLUMN_PRECINCT_ID));
                String bayNumber = cursor.getString (cursor.getColumnIndex (BaysTable.COLUMN_BAY_NUMBER));
                String audp = cursor.getString (cursor.getColumnIndex (BaysTable.COLUMN_AUDP));
                String lu_audp = cursor.getString (cursor.getColumnIndex (BaysTable.COLUMN_LU_AUDP));


                bay.setId(id);
                bay.setPrecinctId(precinctId);
                bay.setBayNumber(bayNumber);
                bay.setAudp(audp);
                bay.setLu_audp(lu_audp);

                // Show phone number in Logcat
                System.out.println("Loading from DB: "+bay.toString());
                // end of while loop

                bays.add(bay);
            }

        }

        return bays;
    }


    public static void addBay(Bay bay, Context context) {
        String format = "yyyy-MM-dd HH:mm:ss";

        ContentValues initialValues = new ContentValues();
        initialValues.put(BaysTable.COLUMN_ID, bay.getId());
        initialValues.put(BaysTable.COLUMN_PRECINCT_ID,bay.getPrecinctId());
        initialValues.put(BaysTable.COLUMN_BAY_NUMBER, bay.getBayNumber());
        initialValues.put(BaysTable.COLUMN_AUDP, bay.getAudp());
        initialValues.put(BaysTable.COLUMN_LU_AUDP, bay.getLu_audp());

        System.out.println("Adding Bay : "+bay.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, BaysTable.TABLE_BAYS);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added A Bay Successfully");

    }

    //Not tested
    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, BaysTable.TABLE_BAYS);
        int result = context.getContentResolver().delete(contentUri,null,null);

        System.out.println("Deleted status: "+result);


    }

    public static void refillBays(List<Bay> bays, Context context) {
        deleteAll(context);
        for(Bay bay : bays){
            addBay(bay,context);
        }
    }



}
