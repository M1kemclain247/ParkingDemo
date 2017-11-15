package com.example.m1kes.parkingdemo.sqlite.tables;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TransactionsTable {

    // table Name
    public static final String TABLE_TRANSACTIONS = "Transactions";

    //TODO Add all table columns here
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_SHIFT_ID = "ShiftId";
    public static final String COLUMN_USERNAME = "Username";//username
    public static final String COLUMN_TERMINAL_ID = "TerminalID";//Dep number
    public static final String COLUMN_RECIEPT_NO = "RecieptNo";//Reciept number
    public static final String COLUMN_BAY_NUMBER = "BayNumber";//Reciept number
    public static final String COLUMN_PRECINCT_ID = "PrecinctId";//Reciept number
    public static final String COLUMN_REQUEST_DATE_TIME = "Request_Date_Time";//Reciept number
    public static final String COLUMN_VEHICLE_REG = "VehicleReg";
    public static final String COLUMN_DURATION = "Duration";
    public static final String COLUMN_IN_DATETIME = "in_datetime";
    public static final String COLUMN_TRANSACTION_DATE_TIME= "TransactionDateTime";
    public static final String COLUMN_IS_SYNCED = "Is_Synced";

    public static final String COLUMN_AMOUNT_DUE = "AmountDue";
    public static final String COLUMN_AMOUNT_PAID = "AmountPaid";
    public static final String COLUMN_USER_BALANCE = "UserBalance";

    public static final String COLUMN_TENDER_AMOUNT = "TenderAmount";
    public static final String COLUMN_CHANGE = "Change";

    public static final String COLUMN_TRANSACTION_TYPE = "TransactionType";
    public static final String COLUMN_PAYMENT_MODE_ID = "PaymentModeID";//payment method
    public static final String COLUMN_EXPIRY_DATETIME = "out_datetime"; //Need to confirm if this has been changed
    public static final String COLUMN_REMAINING_DATE_TIME = "remaining_datetime"; //Need to confirm if this has been changed
    public static final String COLUMN_IS_EXPIRED = "is_expired"; //Need to confirm if this has been changed
    public static final String COLUMN_IS_NOTIFIED = "is_notified"; //Need to confirm if this has been changed
    public static final String COLUMN_IS_PREPAYMENT = "is_prepayment"; //Need to confirm if this has been changed

    public static final String COLUMN_IS_PAID = "is_paid"; //Need to confirm if this has been changed
    public static final String COLUMN_FINE_REF_NO = "fineRefNo"; //Need to confirm if this has been changed
    public static final String COLUMN_PAYMENT_REF_NO = "paymentRefNo"; //Need to confirm if this has been changed

    // Database creation SQL statement
    private static final String TABLE_CREATE = "create table "
            + TABLE_TRANSACTIONS
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SHIFT_ID + " integer ,"
            + COLUMN_USERNAME + " text ,"
            + COLUMN_TERMINAL_ID + " text ,"
            + COLUMN_RECIEPT_NO + " text ,"
            + COLUMN_PRECINCT_ID + " integer ,"
            + COLUMN_BAY_NUMBER + " integer ,"
            + COLUMN_REQUEST_DATE_TIME + " text ,"
            + COLUMN_VEHICLE_REG + " text ,"
            + COLUMN_DURATION + " integer ,"
            + COLUMN_IN_DATETIME + " text ,"
            + COLUMN_TRANSACTION_DATE_TIME + " text ,"
            + COLUMN_IS_SYNCED + " integer ,"
            + COLUMN_AMOUNT_DUE + " double ,"
            + COLUMN_AMOUNT_PAID + " double ,"
            + COLUMN_TENDER_AMOUNT + " double ,"
            + COLUMN_USER_BALANCE + " double ,"
            + COLUMN_CHANGE + " double ,"
            + COLUMN_TRANSACTION_TYPE + " text ,"
            + COLUMN_PAYMENT_MODE_ID + " integer ,"
            + COLUMN_EXPIRY_DATETIME + " text, "
            + COLUMN_REMAINING_DATE_TIME + " text ,"
            + COLUMN_IS_EXPIRED + " integer, "
            + COLUMN_IS_NOTIFIED + " integer ,"
            + COLUMN_IS_PREPAYMENT + " integer ,"
            + COLUMN_IS_PAID + " integer ,"
            + COLUMN_FINE_REF_NO + " text ,"
            + COLUMN_PAYMENT_REF_NO + " text "
            + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(TransactionsTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(database);
    }



}
