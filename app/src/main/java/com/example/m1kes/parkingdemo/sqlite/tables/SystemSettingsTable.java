package com.example.m1kes.parkingdemo.sqlite.tables;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SystemSettingsTable {

    // Database table
    public static final String TABLE_SYSTEM_SETTINGS = "SystemSettings";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_VERSION = "Version";
    public static final String COLUMN_VERSION_DATE = "VersionDate";
    public static final String COLUMN_SERVER_NAME = "ServerName";
    public static final String COLUMN_SERVER_IP = "ServerIP";
    public static final String COLUMN_LOCAL_ADDRESS = "LocalAddress";
    public static final String COLUMN_DEVICE_SERVICE_ADDRESS = "DeviceServiceAddress";

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_SYSTEM_SETTINGS
            + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_VERSION + " text, "
            + COLUMN_VERSION_DATE + " text, "
            + COLUMN_SERVER_NAME + " text, "
            + COLUMN_SERVER_IP + " text, "
            + COLUMN_LOCAL_ADDRESS + " text, "
            + COLUMN_DEVICE_SERVICE_ADDRESS + " text "
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
