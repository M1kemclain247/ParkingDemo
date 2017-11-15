package com.example.m1kes.parkingdemo.sqlite.tables;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UsersTable {

    // Database table
    public static final String TABLE_USERS = "Users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMP_NO = "EmpNo";
    public static final String COLUMN_FIRSTNAME = "Firstname";
    public static final String COLUMN_SURNAME = "Surname";
    public static final String COLUMN_IS_LOGGED_IN = "loggedin"; //boolean 0 false 1 true
    public static final String COLUMN_ROLES = "roles";
    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_USERS
            + "("
            + COLUMN_ID + " text primary key , "
            + COLUMN_USERNAME + " text , "
            + COLUMN_PASSWORD + " text ,"
            + COLUMN_EMP_NO + " integer ,"
            + COLUMN_FIRSTNAME + " text ,"
            + COLUMN_SURNAME + " text ,"
            + COLUMN_ROLES + " text ,"
            + COLUMN_IS_LOGGED_IN + " integer" //boolean 0 false 1 true
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(database);
    }




}
