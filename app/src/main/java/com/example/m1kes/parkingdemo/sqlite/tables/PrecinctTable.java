package com.example.m1kes.parkingdemo.sqlite.tables;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PrecinctTable {


    // Database table
    public static final String TABLE_PRECINCTS = "Precincts";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ZONE_NAME = "Zonename";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_TARGET = "Target";
    public static final String COLUMN_LUNCH = "Lunch";
    public static final String COLUMN_AMOUNT = "Amount";

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_PRECINCTS
            + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_ZONE_NAME + " text, "
            + COLUMN_NAME + " text, "
            + COLUMN_TARGET + " integer,"
            + COLUMN_LUNCH + " text,"
            + COLUMN_AMOUNT + " integer"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PRECINCTS);
        onCreate(database);
    }



}
