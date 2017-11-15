package com.example.m1kes.parkingdemo.sqlite.tables;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SiteTable {


    // Database table
    public static final String TABLE_SITE = "Site";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SITE_CODE = "SiteCode";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_DESCRIPTION = "Description";
    public static final String COLUMN_ADDRESS = "Address";
    public static final String COLUMN_CITY = "City";
    public static final String COLUMN_TEL = "Tel";
    public static final String COLUMN_CELL = "Cell";
    public static final String COLUMN_FAX = "Fax";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_MOTTO = "Motto";
    public static final String COLUMN_COMMENT = "Comment";
    public static final String COLUMN_AUDP = "Audp";
    public static final String COLUMN_LU_AUDP = "lu_Audp";


    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_SITE
            + "("
            + COLUMN_ID + " integer primary key , "
            + COLUMN_SITE_CODE + " integer not null, "
            + COLUMN_NAME + " text not null, "
            + COLUMN_DESCRIPTION + " text not null, "
            + COLUMN_ADDRESS + " text not null, "
            + COLUMN_CITY + " text not null, "
            //Not sure datatypes here for these
            + COLUMN_TEL + " text not null, "
            + COLUMN_CELL + " text not null, "
            + COLUMN_FAX + " text not null, "
            + COLUMN_EMAIL + " text not null, "
            + COLUMN_MOTTO + " text not null, "
            + COLUMN_COMMENT + " text not null, "
            //till here
            + COLUMN_AUDP + " text ,"
            + COLUMN_LU_AUDP + " text "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SITE);
        onCreate(database);
    }






}
