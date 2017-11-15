package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.ShiftUserData;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.ShiftsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.UserShiftDataTable;

import java.util.ArrayList;
import java.util.List;

import static com.example.m1kes.parkingdemo.util.DBUtils.convertBoolToInt;
import static com.example.m1kes.parkingdemo.util.DBUtils.convertIntToBool;

public class UserShiftDataAdapter {

    public static List<ShiftUserData> getData(Context context){

        List<ShiftUserData> dataList = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.USER_SHIFT_DATA_CONTENT_URI , null, null, null, null);

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

                ShiftUserData data = new ShiftUserData();

                int id  = cursor.getInt(cursor.getColumnIndex(UserShiftDataTable.COLUMN_ID));
                String tempShiftID = cursor.getString(cursor.getColumnIndex(UserShiftDataTable.USERNAME));
                long date = cursor.getLong(cursor.getColumnIndex(UserShiftDataTable.DATE));

                data.setId(id);
                data.setUsername(tempShiftID);
                data.setDate(date);

                System.out.println("Getting data :"+data);

                dataList.add(data);

            }
            cursor.close();
        }

        return dataList;
    }


    public static void putData(ShiftUserData data, Context context) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(UserShiftDataTable.USERNAME,data.getUsername());
        initialValues.put(UserShiftDataTable.DATE, data.getDate());

        System.out.println("*************************************");
        System.out.println("Adding Data to DB : "+data.toString());
        System.out.println("*************************************");
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, UserShiftDataTable.TABLE_SHIFT_USER_DATA);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);

        System.out.println("Started Shift Successfully");
    }

}
