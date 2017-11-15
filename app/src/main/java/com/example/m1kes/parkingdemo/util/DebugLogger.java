package com.example.m1kes.parkingdemo.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.models.TransactionType;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DebugLogger {


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void writeShiftToLogFile(Shift shift){

        String filename = "shiftlog.txt";
        File root = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Marshall");

        boolean success = true;
        if (!root.exists()) {
            success = root.mkdirs();
        }
        if (success) {
            // Do something on success
            File writableFile = new File(root, filename);
            try {

                try (PrintWriter pw = new PrintWriter(new FileWriter(writableFile,true))) {
                    pw.println("*********************************");
                    pw.println("*********OPENED SHIFT ***********");
                    pw.println("UserName: "+shift.getUsername());
                    pw.println("ShiftID : "+shift.getShiftId());
                    pw.println("PrecinctID: "+shift.getPrecinctID());
                    pw.println("StartTime: "+shift.getStart_time());
                    pw.println(" ");
                    pw.println("*********************************");
                    System.out.println("Successfully logged Shift to File");
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            // Do something else on failure
            System.out.println("Failed to Log Shift to  File");
        }






    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void writeCloseShift(Shift shift){

        String filename = "shiftlog.txt";

        File root = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Marshall");

        boolean success = true;
        if (!root.exists()) {
            success = root.mkdirs();
        }
        if (success) {
            // Do something on success
            File writableFile = new File(root, filename);
            try {

                try (PrintWriter pw = new PrintWriter(new FileWriter(writableFile,true))) {
                    pw.println("*********************************");
                    pw.println("*********CLOSED SHIFT ***********");
                    pw.println("UserName: "+shift.getUsername());
                    pw.println("ShiftID : "+shift.getShiftId());
                    pw.println("PrecinctID: "+shift.getPrecinctID());
                    pw.println("StartTime: "+shift.getStart_time());
                    pw.println(" ");
                    pw.println("*********************************");
                    System.out.println("Successfully logged Shift to File");
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            // Do something else on failure
            System.out.println("Failed to Log Shift to File");
        }



    }




    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void readTransactionFromLogFile(){


        //reading the file will take place on the background Thread since it may be quite a heavy operation to
        //materialize the objects ect..
        Thread t = null;

            //Make a new Thread to handle this background working
        t  = new Thread(){
            @Override
            public void run() {
                JsonParser parser = new JsonParser();

                String filename = "TransactionLogs.txt";
                File root = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "Marshall");

                boolean success = true;
                if (!root.exists()) {
                    success = root.mkdirs();
                }
                if (success) {
                    //First Step read the file so that we can append
                    try {
                        System.out.println("Trying to read File: " + root + filename);
                        FileReader reader = new FileReader(root + "" + filename);
                        Object object = parser.parse(reader);
                        //Try to convert the parsed read file into a JSONArray
                        JSONArray jsonArray = (JSONArray) object;
                        System.out.println("Trying to parse File: ");
                        List<Transaction> transactions = convertJsonArrayToList(jsonArray);

                        System.out.println(transactions);
                        System.out.println("Successfully Read All of the Transactions!");

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        System.out.println("Failed to Read the JSON File");
                    }
                }
                }
        };
        t.start();

    }


    public static List<Transaction> convertJsonArrayToList(JSONArray jsonArray) {

        List<Transaction> transactions = new ArrayList<>();

        System.out.println("About to try and parse this JSONArray:");
        System.out.println(""+jsonArray.toString());
        try {
            for(int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = null;
                jsonObject = jsonArray.getJSONObject(i);

                int id = jsonObject.getInt("id");
                String shiftID = jsonObject.getString("ShiftId");
                String username = jsonObject.getString("Username");
                int terminalID = jsonObject.getInt("TerminalId");
                String receiptNo = jsonObject.getString("ReceiptNo");
                String preinctID = jsonObject.getString("PrecinctID");
                int baynumber = jsonObject.getInt("Baynumber");
                long requestDateTime = jsonObject.getLong("RequestDateTime");
                String vehicleReg = jsonObject.getString("VehicleReg");
                int duration = jsonObject.getInt("Duration");
                long inDateTime = jsonObject.getLong("In_DateTime");
                long transactionDateTime = jsonObject.getLong("Transaction_DateTime");
                long expiryDateTime = jsonObject.getLong("Expiry_DateTime");

                double amountDue = jsonObject.getDouble("AmountDue");
                String transactionType = jsonObject.getString("TransactionType");
                int paymentModeID = jsonObject.getInt("Payment_Mode_ID");
                //Fields for payments but they have to be here since its JSONArray
                double amountPaid = jsonObject.getDouble("AmountPaid");
                double tenderAmount = jsonObject.getDouble("TenderAmount");
                double change = jsonObject.getDouble("Change");

                Transaction transaction = new Transaction();
                transaction.setId(id);
                transaction.setShiftId(shiftID);
                transaction.setUsername(username);
                transaction.setTerminalId(terminalID);
                transaction.setRecieptNumber(receiptNo);//needs to be changed
                transaction.setPrecinctID(preinctID);
                transaction.setBayNumber(baynumber);
                transaction.setRequestDateTime(requestDateTime);
                transaction.setVehicleregNumber(vehicleReg);
                transaction.setDuration(duration);
                transaction.setIn_datetime(inDateTime);
                transaction.setTransactionDateTime(transactionDateTime);
                transaction.setExpiry_datetime(expiryDateTime);
                transaction.setAmountDue(amountDue);
                transaction.setTransactionType(transactionType);
                transaction.setPayment_mode_id(paymentModeID);

                transaction.setAmountPaid(amountPaid);
                transaction.setTenderAmount(tenderAmount);
                transaction.setChange(change);


                //Add to list of transactions the materialized Transaction
                System.out.println("Adding Transaction to List: "+transaction.toString());

                transactions.add(transaction);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Error Occured While Trying to parse Json!");
        }

        return transactions;
    }


    public static Map toMap(Transaction transaction) {

        HashMap<String, String> jsonMap = new HashMap<>();

        //get each Transaction of all types and save them into the JsonFile

            //NON-NULL FIELDS
            jsonMap.put("id", String.valueOf(transaction.getId()));
            jsonMap.put("ShiftId", transaction.getShiftId());
            jsonMap.put("Username", transaction.getUsername());
            jsonMap.put("TerminalId", String.valueOf(transaction.getTerminalId()));
            jsonMap.put("ReceiptNo", transaction.getRecieptNumber());
            jsonMap.put("PrecinctID", transaction.getPrecinctID());
            jsonMap.put("Baynumber", String.valueOf(transaction.getBayNumber()));
            jsonMap.put("RequestDateTime", String.valueOf(transaction.getRequestDateTime()));
            jsonMap.put("VehicleReg", transaction.getVehicleregNumber());
            jsonMap.put("Duration", String.valueOf(transaction.getDuration()));
            jsonMap.put("In_DateTime", String.valueOf(transaction.getIn_datetime()));
            jsonMap.put("Expiry_DateTime", String.valueOf(transaction.getExpiry_datetime()));
            jsonMap.put("Transaction_DateTime", String.valueOf(transaction.getTransactionDateTime()));
            jsonMap.put("AmountDue", String.valueOf(transaction.getAmountDue()));
            jsonMap.put("TransactionType", transaction.getTransactionType());
            jsonMap.put("Payment_Mode_ID", String.valueOf(transaction.getPayment_mode_id()));
            //Payment items should all be 0 for a fine only reason are here is for JsonArray
            jsonMap.put("AmountPaid", String.valueOf(transaction.getAmountPaid()));
            jsonMap.put("TenderAmount", String.valueOf(transaction.getTenderAmount()));
            jsonMap.put("Change", String.valueOf(transaction.getChange()));


        return jsonMap;
    }

}
