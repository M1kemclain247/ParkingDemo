package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.models.User;
import com.example.m1kes.parkingdemo.models.Zone;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.TransactionsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.UsersTable;
import com.example.m1kes.parkingdemo.sqlite.tables.ZonesTable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.m1kes.parkingdemo.util.DBUtils.convertIntToBool;
import static com.example.m1kes.parkingdemo.util.DBUtils.getDateFromDateTime;
import static com.example.m1kes.parkingdemo.util.DBUtils.parseDate;

public class ZonesAdapter {

    /**
     * used to return a List of all the Zones in the Database
     *
     * @param context
     * @return List<Zone> from the database
     */

    public static List<Zone> getAllZones(Context context){

        String dateTimeParseFormat = "yyyy-MM-dd HH:mm:ss";
        List<Zone> zones = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.ZONES_CONTENT_URI , null, null, null, null);

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
                Zone zone = new Zone();

                int id = cursor.getInt(cursor.getColumnIndex(ZonesTable.COLUMN_ID));
                String name = cursor.getString (cursor.getColumnIndex (ZonesTable.COLUMN_NAME));
                int amount = cursor.getInt (cursor.getColumnIndex (ZonesTable.COLUMN_AMOUNT));


                zone.setId(id);
                zone.setName(name);
                zone.setAmount(amount);


                // Show phone number in Logcat
                Log.i("ZoneAdapter ", zone.toString());
                // end of while loop

                zones.add(zone);
            }

        }

        return zones;
    }





    public static void addZone(Zone zone, Context context) {
        String format = "yyyy-MM-dd HH:mm:ss";

        ContentValues initialValues = new ContentValues();
        initialValues.put(ZonesTable.COLUMN_ID,zone.getId());
        initialValues.put(ZonesTable.COLUMN_NAME, zone.getName());
        initialValues.put(ZonesTable.COLUMN_AMOUNT, zone.getAmount());


        System.out.println("Adding Zone : "+zone.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, ZonesTable.TABLE_ZONES);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added A Zone Successfully");

    }

    //Not tested
    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, ZonesTable.TABLE_ZONES);
        int result = context.getContentResolver().delete(contentUri,null,null);

        System.out.println("Deleted status: "+result);

    }

    public static void refillZones(List<Zone> zones, Context context) {

        deleteAll(context);

        for(Zone zone : zones){
            addZone(zone,context);
        }


    }



}
