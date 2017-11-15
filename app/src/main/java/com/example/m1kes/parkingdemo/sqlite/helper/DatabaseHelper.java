package com.example.m1kes.parkingdemo.sqlite.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "parkdata.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        UsersTable.onCreate(sqLiteDatabase);
        TransactionsTable.onCreate(sqLiteDatabase);
        TransactionTypeTable.onCreate(sqLiteDatabase);
        ZonesTable.onCreate(sqLiteDatabase);
        TarriffsTable.onCreate(sqLiteDatabase);
        BaysTable.onCreate(sqLiteDatabase);
        SiteTable.onCreate(sqLiteDatabase);
        PrecinctTable.onCreate(sqLiteDatabase);
        PrinterTable.onCreate(sqLiteDatabase);
        SystemSettingsTable.onCreate(sqLiteDatabase);
        PrintJobsTable.onCreate(sqLiteDatabase);
        PaymentModesTable.onCreate(sqLiteDatabase);
        ShiftsTable.onCreate(sqLiteDatabase);
        SearchHistoryTable.onCreate(sqLiteDatabase);
        JsonExportsTable.onCreate(sqLiteDatabase);
        UserShiftDataTable.onCreate(sqLiteDatabase);


        System.out.println("Database's Created");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        UsersTable.onUpgrade(sqLiteDatabase, i, i1);
        TransactionsTable.onUpgrade(sqLiteDatabase, i, i1);
        TransactionTypeTable.onUpgrade(sqLiteDatabase, i, i1);
        ZonesTable.onUpgrade(sqLiteDatabase,i,i1);
        TarriffsTable.onUpgrade(sqLiteDatabase,i,i1);
        BaysTable.onUpgrade(sqLiteDatabase,i,i1);
        SiteTable.onUpgrade(sqLiteDatabase,i,i1);
        PrecinctTable.onUpgrade(sqLiteDatabase,i,i1);
        PrinterTable.onUpgrade(sqLiteDatabase,i,i1);
        SystemSettingsTable.onUpgrade(sqLiteDatabase,i,i1);
        PrintJobsTable.onUpgrade(sqLiteDatabase,i,i1);
        PaymentModesTable.onUpgrade(sqLiteDatabase,i,i1);
        ShiftsTable.onUpgrade(sqLiteDatabase,i,i1);
        SearchHistoryTable.onUpgrade(sqLiteDatabase,i,i1);
        JsonExportsTable.onUpgrade(sqLiteDatabase,i,i1);
        UserShiftDataTable.onUpgrade(sqLiteDatabase,i,i1);

        System.out.println("Database's Upgraded");
    }






}
