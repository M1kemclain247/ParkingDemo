package com.example.m1kes.parkingdemo.sqlite.tables;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by Michael on 8/5/2017.
 */

public class UserShiftDataTable {


    // Database table
    public static final String TABLE_SHIFT_USER_DATA = "shiftdata";

    public static final String COLUMN_ID = "id";
    public static final String USERNAME = "PrecinctId";
    public static final String DATE = "BayNumber";

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_SHIFT_USER_DATA
            + "("
            + COLUMN_ID + " integer primary key, "
            + USERNAME + " text , "
            + DATE + " integer "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SHIFT_USER_DATA);
        onCreate(database);
    }



}
