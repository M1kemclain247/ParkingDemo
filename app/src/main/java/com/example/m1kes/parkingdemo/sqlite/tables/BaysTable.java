package com.example.m1kes.parkingdemo.sqlite.tables;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BaysTable {


    // Database table
    public static final String TABLE_BAYS = "Bays";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PRECINCT_ID = "PrecinctId";
    public static final String COLUMN_BAY_NUMBER = "BayNumber";

    public static final String COLUMN_AUDP = "Audp";
    public static final String COLUMN_LU_AUDP = "lu_Audp";


    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_BAYS
            + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_PRECINCT_ID + " integer not null, "
            + COLUMN_BAY_NUMBER + " text not null, "
            + COLUMN_AUDP + " text,"
            + COLUMN_LU_AUDP + " text"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BAYS);
        onCreate(database);
    }



}
