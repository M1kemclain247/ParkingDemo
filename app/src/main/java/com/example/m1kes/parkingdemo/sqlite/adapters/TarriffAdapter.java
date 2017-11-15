package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.models.Site;
import com.example.m1kes.parkingdemo.models.Tarriff;
import com.example.m1kes.parkingdemo.models.Zone;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.SiteTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TarriffsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.ZonesTable;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.m1kes.parkingdemo.util.DBUtils.convertIntToBool;
import static com.example.m1kes.parkingdemo.util.DBUtils.getDateFromDateTime;
import static com.example.m1kes.parkingdemo.util.DBUtils.parseDate;

public class TarriffAdapter {

    /**
     * used to return a List of all the Tarriffs in the Database
     *
     * @param context
     * @return List<Tarriff> from the database
     */

    public static List<Tarriff> getAllTarriffs(Context context){

        String dateTimeParseFormat = "dd-MM-yyyy HH:mm:ss";
        List<Tarriff> tarriffs = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.TARRIFS_CONTENT_URI , null, null, null, null);

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
                Tarriff tarriff = new Tarriff();

                int id = cursor.getInt(cursor.getColumnIndex(TarriffsTable.COLUMN_ID));
                String name = cursor.getString (cursor.getColumnIndex (TarriffsTable.COLUMN_NAME));
                int amount = cursor.getInt (cursor.getColumnIndex (TarriffsTable.COLUMN_AMOUNT));

                tarriff.setId(id);
                tarriff.setName(name);
                tarriff.setAmount(amount);





                // Show phone number in Logcat
                Log.i("TarriffAdapter ", tarriff.toString());
                // end of while loop

                tarriffs.add(tarriff);
            }

        }

        return tarriffs;
    }

    public static void addTarriff(Tarriff tarriff, Context context) {
        String format = "yyyy-MM-dd HH:mm:ss";

        ContentValues initialValues = new ContentValues();
        initialValues.put(TarriffsTable.COLUMN_ID, tarriff.getId());
        initialValues.put(TarriffsTable.COLUMN_NAME, tarriff.getName());
        initialValues.put(TarriffsTable.COLUMN_AMOUNT, tarriff.getAmount());


        System.out.println("Adding Tarriff : "+tarriff.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TarriffsTable.TABLE_TARRIFFS);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added A Tarriff Successfully");

    }

    //Not tested
    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, TarriffsTable.TABLE_TARRIFFS);
        int result = context.getContentResolver().delete(contentUri,null,null);

        System.out.println("Deleted status: "+result);


    }

    public static void refillTarriffs(List<Tarriff> tarriffs, Context context) {

        deleteAll(context);
        for(Tarriff tarriff : tarriffs){
          addTarriff(tarriff,context);
        }


    }


}
