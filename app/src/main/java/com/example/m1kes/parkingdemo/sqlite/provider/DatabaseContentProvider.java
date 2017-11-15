package com.example.m1kes.parkingdemo.sqlite.provider;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.m1kes.parkingdemo.sqlite.helper.DatabaseHelper;
import com.example.m1kes.parkingdemo.sqlite.tables.BaysTable;
import com.example.m1kes.parkingdemo.sqlite.tables.JsonExportsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.PaymentModesTable;
import com.example.m1kes.parkingdemo.sqlite.tables.PrecinctTable;
import com.example.m1kes.parkingdemo.sqlite.tables.PrintJobsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.PrinterTable;
import com.example.m1kes.parkingdemo.sqlite.tables.SearchHistoryTable;
import com.example.m1kes.parkingdemo.sqlite.tables.ShiftsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.SiteTable;
import com.example.m1kes.parkingdemo.sqlite.tables.SystemSettingsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TarriffsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TransactionTypeTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TransactionsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.UserShiftDataTable;
import com.example.m1kes.parkingdemo.sqlite.tables.UsersTable;
import com.example.m1kes.parkingdemo.sqlite.tables.ZonesTable;

public class DatabaseContentProvider extends ContentProvider {

    private DatabaseHelper dbHelper ;

    public static final String AUTHORITY = "parkProviderAuthorities"; //specific for our our app, will be specified in manifest
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    //Create Content Uri's for the adapters to access
    public static final Uri USERS_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + UsersTable.TABLE_USERS);
    public static final Uri TRANSACTIONS_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TransactionsTable.TABLE_TRANSACTIONS);
    public static final Uri TRANSACTION_TYPE_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TransactionTypeTable.TABLE_TRANSACTION_TYPES);
    public static final Uri BAYS_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BaysTable.TABLE_BAYS);
    public static final Uri SITE_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + SiteTable.TABLE_SITE);
    public static final Uri TARRIFS_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + TarriffsTable.TABLE_TARRIFFS);
    public static final Uri ZONES_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + ZonesTable.TABLE_ZONES);

    public static final Uri PRECINCT_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PrecinctTable.TABLE_PRECINCTS);

    public static final Uri PRINTER_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PrinterTable.TABLE_PRINTERS);

    public static final Uri SYSTEM_SETTINGS_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + SystemSettingsTable.TABLE_SYSTEM_SETTINGS);

    public static final Uri PRINT_JOBS_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PrintJobsTable.TABLE_PRINT_JOBS);

    public static final Uri PAYMENT_MODES_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + PaymentModesTable.TABLE_PAYMENT_MODES);

    public static final Uri SHIFTS_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + ShiftsTable.TABLE_SHIFTS);

    public static final Uri SEARCH_HISTORY_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + SearchHistoryTable.TABLE_SEARCH_HISTORY);

    public static final Uri JSON_EXPORTS_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + JsonExportsTable.TABLE_JSON_EXPORTS);

    public static final Uri USER_SHIFT_DATA_CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + UserShiftDataTable.TABLE_SHIFT_USER_DATA);

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        String table = getTableName(uri);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor =database.query(table,  projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        String table = getTableName(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long value = database.insert(table, null, initialValues);
        if(value>0){
            System.out.println("Successfully Inserted Using Content Provider Rows Affected: "+value);
        }else{
            System.out.println("Failed to insert record in content provider");
        }

        return Uri.withAppendedPath(CONTENT_URI, String.valueOf(value));
    }

    @Override
    public int delete(Uri uri, String where, String[] args) {
        String table = getTableName(uri);
        SQLiteDatabase dataBase=dbHelper.getWritableDatabase();
        return dataBase.delete(table, where, args);
    }

    @Override
    public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
        String table = getTableName(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.update(table, values, whereClause, whereArgs);
    }

    public static String getTableName(Uri uri){
        String value = uri.getPath();
        value = value.replace("/", "");//we need to remove '/'
        return value;
    }


}

