package com.example.m1kes.parkingdemo.sqlite.tables;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ShiftsTable {


    // Database table
    public static final String TABLE_SHIFTS = "Shifts";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "Username";
    public static final String COLUMN_TERMINAL_ID = "TerminalID";
    public static final String COLUMN_START_TIME = "StartTime";
    public static final String COLUMN_END_TIME = "EndTime";
    public static final String COLUMN_PRECINCT_ID = "Precinct_id";
    public static final String COLUMN_PRECINCT_ZONENAME = "Precinct_Zonename";
    public static final String COLUMN_PRECINCT_NAME = "Precinct_Name";
    public static final String COLUMN_SHIFT_ID = "ShiftID";
    public static final String COLUMN_IS_ACTIVE= "IsActive";
    public static final String COLUMN_IS_START_SYNCED= "Is_Start_Synced";
    public static final String COLUMN_IS_CLOSE_SYNCED= "Is_Close_Synced";

    public static final String COLUMN_TOTAL_DECLARED_AMOUNT = "total_declaredAmount";
    public static final String COLUMN_CASH_COLLECTED = "cash_collected";
    public static final String COLUMN_TOTAL_COLLECTED = "total_collected";
    public static final String COLUMN_TOTAL_CASHED = "total_cashed";
    public static final String COLUMN_EPAYMENTS = "Epayments";
    public static final String COLUMN_TOTAL_ARREARS = "total_arrears";
    public static final String COLUMN_TOTAL_PREPAID = "total_prepaid";
    public static final String COLUMN_TICKET_ISSUED_AMOUNT = "ticketissued_amount";


    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_SHIFTS
            + "("
            + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_USERNAME + " text , "
            + COLUMN_START_TIME + " text  ,"
            + COLUMN_END_TIME + " text  ,"
            + COLUMN_PRECINCT_ID + " integer  ,"
            + COLUMN_TERMINAL_ID + " integer  ,"
            + COLUMN_SHIFT_ID + " text  ,"
            + COLUMN_PRECINCT_ZONENAME + " text  ,"
            + COLUMN_PRECINCT_NAME + " text  ,"
            + COLUMN_IS_ACTIVE + " integer  ,"
            + COLUMN_IS_START_SYNCED + " integer  ,"
            + COLUMN_IS_CLOSE_SYNCED + " integer  ,"
            + COLUMN_TOTAL_DECLARED_AMOUNT + " double  ,"
            + COLUMN_CASH_COLLECTED + " double  ,"
            + COLUMN_TOTAL_COLLECTED + " double  ,"
            + COLUMN_TOTAL_CASHED + " double  ,"
            + COLUMN_EPAYMENTS + " double  ,"
            + COLUMN_TOTAL_ARREARS + " double  ,"
            + COLUMN_TOTAL_PREPAID + " double  ,"
            + COLUMN_TICKET_ISSUED_AMOUNT + " double "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SHIFTS);
        onCreate(database);
    }



}
