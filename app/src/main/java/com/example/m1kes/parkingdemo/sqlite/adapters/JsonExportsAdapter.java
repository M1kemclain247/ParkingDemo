package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.m1kes.parkingdemo.models.SearchHistory;
import com.example.m1kes.parkingdemo.modules.supervisor.models.JsonExports;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.JsonExportsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.SearchHistoryTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JsonExportsAdapter {

    public static List<JsonExports> getAllExportedFiles(Context context){

        List<JsonExports> jsonExports = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.JSON_EXPORTS_CONTENT_URI , null, null, null, null);

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
                JsonExports jsonExport = new JsonExports();

                int id = cursor.getInt(cursor.getColumnIndex(JsonExportsTable.COLUMN_ID));
                String filePath = cursor.getString (cursor.getColumnIndex (JsonExportsTable.COLUMN_FILE_PATH));
                long creationTime = Long.valueOf(cursor.getString(cursor.getColumnIndex (JsonExportsTable.COLUMN_CREATION_TIME)));

                jsonExport.setId(id);
                jsonExport.setFilePath(filePath);
                jsonExport.setCreationDateTime(creationTime);


                // Show phone number in Logcat
                System.out.println("Loading from DB: "+jsonExport.toString());
                // end of while loop

                jsonExports.add(jsonExport);
            }

        }

        // Sorting
        Collections.sort(jsonExports, new Comparator<JsonExports>() {
            @Override
            public int compare(JsonExports jsonExports2, JsonExports jsonExports1)
            {
                return  String.valueOf(jsonExports1.getCreationDateTime()).compareTo(String.valueOf(jsonExports2.getCreationDateTime()));
            }
        });


        return jsonExports;
    }


    public static void addJsonExport(JsonExports jsonExport, Context context) {
        String format = "yyyy-MM-dd HH:mm:ss";

        ContentValues initialValues = new ContentValues();
        initialValues.put(JsonExportsTable.COLUMN_FILE_PATH,jsonExport.getFilePath());
        initialValues.put(JsonExportsTable.COLUMN_CREATION_TIME, jsonExport.getCreationDateTime());

        System.out.println("Adding Json Export : "+jsonExport.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, JsonExportsTable.TABLE_JSON_EXPORTS);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added Json Export Successfully");

    }

    //Not tested
    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, JsonExportsTable.TABLE_JSON_EXPORTS);
        int result = context.getContentResolver().delete(contentUri,null,null);

        System.out.println("Rows Deleted: "+result);
    }

    public static void refillBays(List<JsonExports> jsonExports, Context context) {
        deleteAll(context);
        for(JsonExports jsonExport : jsonExports){
            addJsonExport(jsonExport,context);
        }
    }


}
