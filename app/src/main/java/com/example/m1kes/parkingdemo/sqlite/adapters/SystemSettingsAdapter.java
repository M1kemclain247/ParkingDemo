package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.models.SystemSettings;
import com.example.m1kes.parkingdemo.models.Tarriff;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.PrinterTable;
import com.example.m1kes.parkingdemo.sqlite.tables.SystemSettingsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TarriffsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TransactionsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.ZonesTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.example.m1kes.parkingdemo.util.DBUtils.*;

public class SystemSettingsAdapter {

    /**
     * used to return a List of all the SystemSettings in the Database
     * @param context
     * @return List<SystemSettings> from the database
     */
    public static List<SystemSettings> getAllSystemSettings(Context context){

        String dateTimeParseFormat = "yyyy-MM-dd";
        List<SystemSettings> allSystemSettings = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SYSTEM_SETTINGS_CONTENT_URI , null, null, null, null);

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
                SystemSettings systemSettings = new SystemSettings();

                int id = cursor.getInt(cursor.getColumnIndex(SystemSettingsTable.COLUMN_ID));
                String version = cursor.getString (cursor.getColumnIndex (SystemSettingsTable.COLUMN_VERSION));
                Date versionDate = parseDate(cursor.getString (cursor.getColumnIndex (SystemSettingsTable.COLUMN_VERSION_DATE)),dateTimeParseFormat);
                String serverName = cursor.getString (cursor.getColumnIndex (SystemSettingsTable.COLUMN_SERVER_NAME));
                String serverIp = cursor.getString (cursor.getColumnIndex (SystemSettingsTable.COLUMN_SERVER_IP));
                String localAddress = cursor.getString (cursor.getColumnIndex (SystemSettingsTable.COLUMN_LOCAL_ADDRESS));
                String deviceServiceAddress = cursor.getString (cursor.getColumnIndex (SystemSettingsTable.COLUMN_DEVICE_SERVICE_ADDRESS));

                systemSettings.setId(id);
                systemSettings.setVersion(version);
                systemSettings.setVersionDate(versionDate);
                systemSettings.setServerName(serverName);
                systemSettings.setServerIp(serverIp);
                systemSettings.setLocalAddress(localAddress);
                systemSettings.setDeviceServiceAddress(deviceServiceAddress);


                // Show phone number in Logcat
                System.out.println("Loading from DB: "+systemSettings.toString());
                // end of while loop

                allSystemSettings.add(systemSettings);
            }

        }

        return allSystemSettings;
    }



    public static void addSystemSettings(SystemSettings systemSettings, Context context) {

        String dateTimeParseFormat = "yyyy-MM-dd";
        ContentValues initialValues = new ContentValues();
        initialValues.put(SystemSettingsTable.COLUMN_ID, systemSettings.getId());
        initialValues.put(SystemSettingsTable.COLUMN_VERSION,systemSettings.getVersion());
        initialValues.put(SystemSettingsTable.COLUMN_VERSION_DATE,getDateFromDateTime( systemSettings.getVersionDate(),dateTimeParseFormat));
        initialValues.put(SystemSettingsTable.COLUMN_SERVER_NAME,systemSettings.getServerName());
        initialValues.put(SystemSettingsTable.COLUMN_SERVER_IP,systemSettings.getServerIp());
        initialValues.put(SystemSettingsTable.COLUMN_LOCAL_ADDRESS,systemSettings.getLocalAddress());
        initialValues.put(SystemSettingsTable.COLUMN_DEVICE_SERVICE_ADDRESS,systemSettings.getDeviceServiceAddress());


        System.out.println("Adding SystemSetting : "+systemSettings.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, SystemSettingsTable.TABLE_SYSTEM_SETTINGS);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added A SystemSetting Successfully");

    }

    //Not tested
    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, SystemSettingsTable.TABLE_SYSTEM_SETTINGS);
        int result = context.getContentResolver().delete(contentUri,null,null);

        System.out.println("Deleted status: "+result);


    }

    public static void refillSystemSettings(List<SystemSettings> systemSettingsList, Context context) {

        deleteAll(context);
        for(SystemSettings systemSettings : systemSettingsList){

          addSystemSettings(systemSettings,context);
        }


    }

}
