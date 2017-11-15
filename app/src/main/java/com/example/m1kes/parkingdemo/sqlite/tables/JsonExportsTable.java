package com.example.m1kes.parkingdemo.sqlite.tables;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class JsonExportsTable {

    // Database table
    public static final String TABLE_JSON_EXPORTS = "JsonExports";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_FILE_PATH = "FilePath";
    public static final String COLUMN_CREATION_TIME = "CreationTime";

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_JSON_EXPORTS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_FILE_PATH + " text, "
            + COLUMN_CREATION_TIME + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_JSON_EXPORTS);
        onCreate(database);
    }

}
