package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.m1kes.parkingdemo.models.Bay;
import com.example.m1kes.parkingdemo.printer.models.PrinterJob;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.BaysTable;
import com.example.m1kes.parkingdemo.sqlite.tables.PrintJobsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.ShiftsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TransactionsTable;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.example.m1kes.parkingdemo.util.DBUtils.*;

public class PrintJobAdapter {


    public static List<PrinterJob> getAllPrintJobs(String STATUS,Context context){

        String shiftID  = ShiftDataManager.getCurrentActiveShiftID(context);
        String format = "yyyy-MM-dd HH:mm:ss";
        List<PrinterJob> printJobs = new ArrayList<>();

        if(shiftID!=null) {

            ContentResolver cr = context.getContentResolver();
            Cursor cursor = cr.query(DatabaseContentProvider.PRINT_JOBS_CONTENT_URI, null,
                    PrintJobsTable.COLUMN_PRINT_STATUS + " = '" + STATUS + "' AND " +
                            PrintJobsTable.COLUMN_SHIFT_ID + " = '" + shiftID + "'", null, null);

            // Some providers return null if an error occurs, others throw an exception
            if (null == cursor) {
                // Insert code here to handle the error. Be sure not to use the cursor! You may want to
                // call android.util.Log.e() to log this error.

                // If the Cursor is empty, the provider found no matches
            } else if (cursor.getCount() < 1) {
                // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
                // an error. You may want to offer the user the option to insert a new row, or re-type the
                // search term.

            } else {
                // Insert code here to do something with the results
                // Moves to the next row in the cursor. Before the first movement in the cursor, the
                // "row pointer" is -1, and if you try to retrieve data at that position you will get an
                // exception.

                while (cursor.moveToNext()) {
                    // Gets the value from the column.
                    PrinterJob printJob = new PrinterJob();

                    int id = cursor.getInt(cursor.getColumnIndex(PrintJobsTable.COLUMN_ID));
                    String printerModel = cursor.getString(cursor.getColumnIndex(PrintJobsTable.COLUMN_PRINTER_MODEL));
                    String printerCode = cursor.getString(cursor.getColumnIndex(PrintJobsTable.COLUMN_PRINTER_CODE));
                    String printerIMEI = cursor.getString(cursor.getColumnIndex(PrintJobsTable.COLUMN_PRINTER_IMEI));
                    Date lastPrintTime = parseDate(cursor.getString(cursor.getColumnIndex(PrintJobsTable.COLUMN_LAST_ATTEMPTED_PRINT_TIME)), format);
                    String shiftId = cursor.getString(cursor.getColumnIndex(PrintJobsTable.COLUMN_SHIFT_ID));
                    String printType = cursor.getString(cursor.getColumnIndex(PrintJobsTable.COLUMN_PRINT_TYPE));
                    String printData = cursor.getString(cursor.getColumnIndex(PrintJobsTable.COLUMN_PRINT_DATA));
                    String printStatus = cursor.getString(cursor.getColumnIndex(PrintJobsTable.COLUMN_PRINT_STATUS));
                  //  int numAttempts = cursor.getInt(cursor.getColumnIndex(PrintJobsTable.COLUMN_NUM_ATTEMPTS));

                    printJob.setId(id);
                    printJob.setPrinterModel(printerModel);
                    printJob.setPrinterCode(printerCode);
                    printJob.setPrinterIMEI(printerIMEI);
                    printJob.setPrintAttemptTime(lastPrintTime);
                    printJob.setShiftId(shiftId);
                    printJob.setPrintType(printType);
                    printJob.setPrintData(printData);
                    printJob.setPrintStatus(printStatus);
                   // printJob.setNumAttempts(numAttempts);

                    // Show phone number in Logcat
                    System.out.println("Loading from DB: " + printJob.toString());
                    // end of while loop

                    printJobs.add(printJob);
                }

            }
        }

        return printJobs;
    }


    public static void addPrintJob(PrinterJob printJob, Context context) {
        String format = "yyyy-MM-dd HH:mm:ss";

        ContentValues initialValues = new ContentValues();
        initialValues.put(PrintJobsTable.COLUMN_PRINTER_MODEL,printJob.getPrinterModel());
        initialValues.put(PrintJobsTable.COLUMN_PRINTER_CODE, printJob.getPrinterCode());
        initialValues.put(PrintJobsTable.COLUMN_PRINTER_IMEI, printJob.getPrinterIMEI());
        initialValues.put(PrintJobsTable.COLUMN_LAST_ATTEMPTED_PRINT_TIME, getDateFromDateTime(printJob.getPrintAttemptTime(),format));
        initialValues.put(PrintJobsTable.COLUMN_SHIFT_ID, printJob.getShiftId());
        initialValues.put(PrintJobsTable.COLUMN_PRINT_TYPE, printJob.getPrintType());
        initialValues.put(PrintJobsTable.COLUMN_PRINT_DATA, printJob.getPrintData());
       // initialValues.put(PrintJobsTable.COLUMN_NUM_ATTEMPTS, 1);
        initialValues.put(PrintJobsTable.COLUMN_PRINT_STATUS, printJob.getPrintStatus());

        System.out.println("Adding PrintJob : "+printJob.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, PrintJobsTable.TABLE_PRINT_JOBS);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        long id =  ContentUris.parseId(resultUri);
        //show that it is inserted successfully
        System.out.println("Added A PrintJob Successfully with ID: "+id);

    }

    public static void updatePrintStatus(PrinterJob printJob, Context context) {

        String format = "yyyy-MM-dd HH:mm:ss";
        String lastAttemptedUpdate = getDateFromDateTime(printJob.getPrintAttemptTime(),format);

        ContentValues updateValues = new ContentValues();
        //These are the only things that could change if another printer is used
        updateValues.put(PrintJobsTable.COLUMN_PRINTER_MODEL,printJob.getPrinterModel());
        updateValues.put(PrintJobsTable.COLUMN_PRINTER_CODE, printJob.getPrinterCode());
        updateValues.put(PrintJobsTable.COLUMN_PRINTER_IMEI, printJob.getPrinterIMEI());
        updateValues.put(PrintJobsTable.COLUMN_LAST_ATTEMPTED_PRINT_TIME, getDateFromDateTime(new Date(),format));
        updateValues.put(PrintJobsTable.COLUMN_SHIFT_ID, printJob.getShiftId());
        updateValues.put(PrintJobsTable.COLUMN_PRINT_TYPE, printJob.getPrintType());
        updateValues.put(PrintJobsTable.COLUMN_PRINT_DATA, printJob.getPrintData());
        updateValues.put(PrintJobsTable.COLUMN_PRINT_STATUS, printJob.getPrintStatus());
        //updateValues.put(PrintJobsTable.COLUMN_NUM_ATTEMPTS, printJob.getNumAttempts());

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, PrintJobsTable.TABLE_PRINT_JOBS);


        System.out.println("Updating Print Job Shift Id:"+lastAttemptedUpdate);
        //NEED TO CONFIRM THE AND FIRST
       int rowsUpdated = context.getContentResolver().update(contentUri,updateValues,
                PrintJobsTable.COLUMN_LAST_ATTEMPTED_PRINT_TIME+"=?",new String[]{lastAttemptedUpdate});


       // PrintJobAdapter.updatePrintJobsAttempts(context,printJob);

        System.out.println("Rows Updated for status: "+rowsUpdated);

    }

    public static PrinterJob getPrintjobID(PrinterJob printerJob, Context context){

        String format = "yyyy-MM-dd HH:mm:ss";
        String lastAttemptedUpdate = getDateFromDateTime(printerJob.getPrintAttemptTime(),format);

        PrinterJob printJob = new PrinterJob();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.PRINT_JOBS_CONTENT_URI , null,
                PrintJobsTable.COLUMN_LAST_ATTEMPTED_PRINT_TIME+" = '" + lastAttemptedUpdate + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            printJob = null;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            printJob = null;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.


                int id = cursor.getInt(cursor.getColumnIndex(PrintJobsTable.COLUMN_ID));
                int numAttempts = cursor.getInt(cursor.getColumnIndex(PrintJobsTable.COLUMN_NUM_ATTEMPTS));

                printJob.setId(id);
                printJob.setNumAttempts(numAttempts);

                // Show phone number in Logcat
                System.out.println("Loading PrintJob from DB with ID: "+printJob.getId());
                // end of while loop
            }
        }
        return printJob;
    }


    public static void updateNumPrints(PrinterJob printJob, Context context) {

        String format = "yyyy-MM-dd HH:mm:ss";
        String lastAttemptedUpdate = getDateFromDateTime(printJob.getPrintAttemptTime(),format);
        ContentValues updateValues = new ContentValues();

        //These are the only things that could change if another printer is used
        updateValues.put(PrintJobsTable.COLUMN_NUM_ATTEMPTS, printJob.getNumAttempts()+1);

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, PrintJobsTable.TABLE_PRINT_JOBS);
        System.out.println("Updating Print Job with Id:"+printJob.getId());
        //NEED TO CONFIRM THE AND FIRST

        int rowsUpdated = context.getContentResolver().update(contentUri,updateValues,
                PrintJobsTable.COLUMN_LAST_ATTEMPTED_PRINT_TIME+"=?",new String[]{lastAttemptedUpdate});

        System.out.println("Updated num of prints to :"+printJob.getNumAttempts()+1);
        System.out.println("Affected Rows: "+rowsUpdated);
    }

    public static void updatePrintJobsAttempts(Context context, PrinterJob printerJob){
        PrinterJob  job = getPrintjobID(printerJob,context);
        System.out.println("Found PrintJob with ID: "+printerJob.getId());
        if(job!=null){
            updateNumPrints(job,context);
        }
    }

    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, PrintJobsTable.TABLE_PRINT_JOBS);
        int result = context.getContentResolver().delete(contentUri,null,null);
        System.out.println("Deleted status: "+result);
    }


    public static void deletePrintJob(PrinterJob printerJob,Context context){

        System.out.println("Trying to delete printjob from local db with ID : "+printerJob.getId());

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, PrintJobsTable.TABLE_PRINT_JOBS);
        int result = context.getContentResolver().delete(contentUri,
                PrintJobsTable.COLUMN_ID+"=?",new String[]{String.valueOf(printerJob.getId())});

        System.out.println("Rows Affected: "+result);
        if(result>0){
            System.out.println("Successfully deleted printjob from local DB");
        }else{
            System.out.println("Failed to delete printjob from local DB");
        }




    }


}



