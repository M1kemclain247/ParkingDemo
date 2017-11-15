package com.example.m1kes.parkingdemo.sqlite.tables;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class VehiclesTable {

    // Database table
    public static final String TABLE_VEHICLES = "Vehicles";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_REG_NO = "reg_no";
    public static final String COLUMN_LAST_LOGGED = "last_logged";
    public static final String COLUMN_BALANCE = "balance";

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_VEHICLES
            + "("
            + COLUMN_ID + " text primary key autoincrement, "
            + COLUMN_REG_NO + " text , "
            + COLUMN_LAST_LOGGED + " text ,"
            + COLUMN_BALANCE + " double "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICLES);
        onCreate(database);
    }



}
