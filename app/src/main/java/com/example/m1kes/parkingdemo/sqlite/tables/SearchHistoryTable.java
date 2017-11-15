package com.example.m1kes.parkingdemo.sqlite.tables;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class SearchHistoryTable {


    // Database table
    public static final String TABLE_SEARCH_HISTORY = "SearchHistory";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_VEHICLE_REG = "VehicleReg";
    public static final String COLUMN_SEARCH_TIME = "SearchTime";

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_SEARCH_HISTORY
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_VEHICLE_REG + " text, "
            + COLUMN_SEARCH_TIME + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_HISTORY);
        onCreate(database);
    }

}
