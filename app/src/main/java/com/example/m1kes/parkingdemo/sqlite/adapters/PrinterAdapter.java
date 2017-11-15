package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.models.Site;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.PrecinctTable;
import com.example.m1kes.parkingdemo.sqlite.tables.PrinterTable;
import com.example.m1kes.parkingdemo.sqlite.tables.SiteTable;
import com.example.m1kes.parkingdemo.sqlite.tables.ZonesTable;
import com.example.m1kes.parkingdemo.util.DBUtils;

import java.util.ArrayList;
import java.util.List;

public class PrinterAdapter {

    /**
     * used to return a List of all the Precincts in the Database
     * @param context
     * @return List<Precinct> from the database
     */
    public static List<Printer> getAllPrinters(Context context){


        List<Printer> printers = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.PRINTER_CONTENT_URI , null, null, null, null);

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
                Printer printer = new Printer();

                int id = cursor.getInt(cursor.getColumnIndex(PrinterTable.COLUMN_ID));
                String printerCode = cursor.getString (cursor.getColumnIndex (PrinterTable.COLUMN_PRINTER_CODE));
                String printerModel = cursor.getString (cursor.getColumnIndex (PrinterTable.COLUMN_PRINTER_MODEL));
                String printerImei = cursor.getString (cursor.getColumnIndex (PrinterTable.COLUMN_PRINTER_IMEI));
                boolean isPaired = DBUtils.convertIntToBool(cursor.getInt(cursor.getColumnIndex(PrinterTable.COLUMN_IS_PAIRED)));

                printer.setId(id);
                printer.setPrinterCode(printerCode);
                printer.setPrinterModel(printerModel);
                printer.setPrinterIMEI(printerImei);
                printer.setPaired(isPaired);

                // Show phone number in Logcat
                System.out.println("Loading from DB: "+printer.toString());
                // end of while loop

                printers.add(printer);
            }

        }

        return printers;
    }

    public static List<Printer> getPairedPrinters(Context context){


        List<Printer> printers = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.PRINTER_CONTENT_URI , null, PrinterTable.COLUMN_IS_PAIRED+ " = '" +  "1" + "'", null, null);

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
                Printer printer = new Printer();

                int id = cursor.getInt(cursor.getColumnIndex(PrinterTable.COLUMN_ID));
                String printerCode = cursor.getString (cursor.getColumnIndex (PrinterTable.COLUMN_PRINTER_CODE));
                String printerModel = cursor.getString (cursor.getColumnIndex (PrinterTable.COLUMN_PRINTER_MODEL));
                String printerImei = cursor.getString (cursor.getColumnIndex (PrinterTable.COLUMN_PRINTER_IMEI));
                boolean isPaired = DBUtils.convertIntToBool(cursor.getInt(cursor.getColumnIndex(PrinterTable.COLUMN_IS_PAIRED)));

                printer.setId(id);
                printer.setPrinterCode(printerCode);
                printer.setPrinterModel(printerModel);
                printer.setPrinterIMEI(printerImei);
                printer.setPaired(isPaired);


                // Show phone number in Logcat
                System.out.println("Loading from DB: "+printer.toString());
                // end of while loop

                printers.add(printer);
            }

        }

        return printers;
    }


    public static String getPrinterDepCode(String IMEI,Context context){

        String printerCode = null;

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.PRINTER_CONTENT_URI , null,
                PrinterTable.COLUMN_PRINTER_IMEI+" = '" + IMEI + "'", null, null);

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

                printerCode = cursor.getString (cursor.getColumnIndex (PrinterTable.COLUMN_PRINTER_CODE));
                // Show phone number in Logcat
                System.out.println("Found it in DB: "+printerCode.toString());
                // end of while loop
            }

        }

        return printerCode;
    }



    public static void addPrinter(Printer printer, Context context) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(PrinterTable.COLUMN_PRINTER_CODE,printer.getPrinterCode());
        initialValues.put(PrinterTable.COLUMN_PRINTER_MODEL,printer.getPrinterModel());
        initialValues.put(PrinterTable.COLUMN_PRINTER_IMEI, printer.getPrinterIMEI());
        initialValues.put(PrinterTable.COLUMN_IS_PAIRED, DBUtils.convertBoolToInt(printer.isPaired()));

        System.out.println("Adding Printer : "+printer.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, PrinterTable.TABLE_PRINTERS);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added A Printer Successfully");

    }

    //Not tested
    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, PrinterTable.TABLE_PRINTERS);
        int result = context.getContentResolver().delete(contentUri,null,null);

        System.out.println("Deleted status: "+result);

    }


    public static void refillPrinters(List<Printer> printers, Context context) {

        deleteAll(context);
        for(Printer printer : printers){
          addPrinter(printer,context);
        }


    }


}
