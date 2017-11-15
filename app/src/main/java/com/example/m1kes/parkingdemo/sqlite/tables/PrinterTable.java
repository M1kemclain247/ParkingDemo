package com.example.m1kes.parkingdemo.sqlite.tables;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PrinterTable {

    // Database table
    public static final String TABLE_PRINTERS = "Printers";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PRINTER_CODE = "PrinterCode";
    public static final String COLUMN_PRINTER_MODEL = "PrinterModel";
    public static final String COLUMN_PRINTER_IMEI = "PrinterIMEI";
    public static final String COLUMN_IS_PAIRED = "IsPaired";

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_PRINTERS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PRINTER_CODE + " text, "
            + COLUMN_PRINTER_MODEL + " text, "
            + COLUMN_PRINTER_IMEI + " text ,"
            + COLUMN_IS_PAIRED + " integer "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CREATE);
        onCreate(database);
    }


}
