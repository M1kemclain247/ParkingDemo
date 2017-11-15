package com.example.m1kes.parkingdemo.sqlite.tables;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PrintJobsTable {



    // Database table
    public static final String TABLE_PRINT_JOBS = "PrintJobs";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PRINTER_MODEL = "PrinterModel";
    public static final String COLUMN_PRINTER_CODE = "PrinterCode";

    public static final String COLUMN_PRINTER_IMEI = "PrinterIMEI";
    public static final String COLUMN_LAST_ATTEMPTED_PRINT_TIME = "LastPrintTime";
    public static final String COLUMN_SHIFT_ID = "ShiftId";
    public static final String COLUMN_PRINT_TYPE = "PrintType";
    public static final String COLUMN_PRINT_DATA = "PrintData";
    public static final String COLUMN_PRINT_STATUS = "PrintStatus";
    public static final String COLUMN_NUM_ATTEMPTS = "Attempts";



    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_PRINT_JOBS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PRINTER_MODEL + " text, "
            + COLUMN_PRINTER_CODE + " text, "
            + COLUMN_PRINTER_IMEI + " text,"
            + COLUMN_LAST_ATTEMPTED_PRINT_TIME + " text,"
            + COLUMN_SHIFT_ID + " text,"
            + COLUMN_PRINT_TYPE + " text,"
            + COLUMN_PRINT_DATA + " text,"
           // + COLUMN_NUM_ATTEMPTS + "integer, "
            + COLUMN_PRINT_STATUS + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PRINT_JOBS);
        onCreate(database);
    }





}
