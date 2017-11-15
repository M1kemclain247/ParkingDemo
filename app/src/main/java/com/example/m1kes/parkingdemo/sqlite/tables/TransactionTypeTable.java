package com.example.m1kes.parkingdemo.sqlite.tables;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TransactionTypeTable {

    // Database table
    public static final String TABLE_TRANSACTION_TYPES = "TransactionTypes";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "Name";



    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_TRANSACTION_TYPES
            + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_NAME + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTION_TYPES);
        onCreate(database);
    }


}
