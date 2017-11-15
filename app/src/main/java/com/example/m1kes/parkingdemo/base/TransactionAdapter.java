package com.example.m1kes.parkingdemo.base;


import android.content.Context;
import android.util.Log;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.models.TransactionType;
import com.example.m1kes.parkingdemo.settings.developer.DeveloperSettings;
import com.example.m1kes.parkingdemo.settings.manager.ApiManager;
import com.example.m1kes.parkingdemo.util.GeneralUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.regex.Pattern;

import static com.example.m1kes.parkingdemo.networking.Converter.convertInputStreamToString;
import static com.example.m1kes.parkingdemo.settings.manager.ApiManager.getAvailableIps;

public class TransactionAdapter {


    private Stack<Transaction> transactionStack;
    private Context context;


    public TransactionAdapter(List<Transaction> transactions, Context context){
        this.context = context;
        transactionStack = new Stack<>();
        //Add all of the transactions to the stack
        System.out.println("Attempting to add "+transactions.size()+" transactions to Queue...  \n");
        for(Transaction transaction : transactions){
            transactionStack.push(transaction);
        }
        System.out.println("Done! \n");
    }


    public boolean attemptSyncOpenShift(Shift shift){

        List<String> failedRequests = new ArrayList<>();
        List<String> succesfullResponse = new ArrayList<>();

        String[] availableApis = getAvailableIps(context);
        boolean worked = false;
        for (int i = 0; i < availableApis.length; i++) {

            System.out.println("Attempt: " + (i + 1) + "/" + availableApis.length);

            final String tempResponse = attemptStartNewShift(availableApis[i],shift);

            System.out.println("Response : "+tempResponse);

            if (tempResponse.contains("already")||tempResponse.contains("DeviceShiftCode")) {

                //Worked with this IP
                succesfullResponse.add(tempResponse);
                worked = true;
                break;
            } else {
                //Failed to connect with this IP
                failedRequests.add(tempResponse);
            }
        }


        String tempReponse = "";
        if (worked) {
            tempReponse = succesfullResponse.get(0);
        } else {
            tempReponse = failedRequests.get(0);
        }


        if(tempReponse.contains("already")||tempReponse.contains("DeviceShiftCode")){
            //Success
            return true;
        }else{
            //Failed
            return false;
        }
    }

    public List<Transaction> PushAllTransactions(){

        Map<String,Transaction> responses = new HashMap<>();

        Random r = new Random();
        int Low = 10;
        int High = 999;

        System.out.println("Transaction Stack Contains: "+transactionStack);
        System.out.println("Attempting to Resend "+transactionStack.size()+"  Transactions \n");
        List<Transaction> failedTransactions = new ArrayList<>();
        List<Transaction> completedTransactions = new ArrayList<>();
        try {
            if(!transactionStack.empty()) {
                Iterator<Transaction> iterator = transactionStack.iterator();
                while (iterator.hasNext()) {
                    int Result = r.nextInt(High - Low) + Low;

                        Transaction transaction = iterator.next();
                        System.out.println("**************************************\n");
                        System.out.println(transaction + "\n");
                        System.out.println("**************************************\n");
                        if (transaction.getTransactionType().equalsIgnoreCase(TransactionType.TYPE_FINE)) {
                            responses.put(tryDifferentApis(transaction) + "/" + Result, transaction);
                        } else if (transaction.getTransactionType().equalsIgnoreCase(TransactionType.TYPE_PAYMENT)) {
                            responses.put(tryDifferentApis(transaction) + "/" + Result, transaction);
                        } else if (transaction.getTransactionType().equalsIgnoreCase(TransactionType.TYPE_PAY_ARREARS)) {
                            responses.put(tryDifferentApis(transaction) + "/" + Result, transaction);
                        } else if (transaction.getTransactionType().equalsIgnoreCase(TransactionType.TYPE_PREPAYMENT)) {
                            responses.put(tryDifferentApis(transaction) + "/" + Result, transaction);
                        }

                }


                System.out.println("Iterating through results of Transactions Push TotalNo: " + responses.size() + "\n");
                for (Map.Entry<String, Transaction> entry : responses.entrySet()) {
                    System.out.println("**************************************\n");
                    System.out.println(entry.getKey() + " / " + entry.getValue() + "\n");
                    System.out.println("**************************************\n");

                    if (entry.getKey().contains("Success") || entry.getKey().contains("already")) {
                        System.out.println("Setting Transaction to Synced");
                        entry.getValue().setIs_synced(true);
                        completedTransactions.add(entry.getValue());
                        com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter.setSynced(entry.getValue(), context);
                    } else {
                        failedTransactions.add(entry.getValue());
                    }
                }
            }

            System.out.println("Successfully synced " + completedTransactions.size() + " Transactions successfully!");

            if (failedTransactions.size() > 0) {
                System.out.println("Failed to upload " + failedTransactions.size() + " Transactions!");
            }

        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        //Return the Transactions that failed to send
        return failedTransactions;

    }


    private String tryDifferentApis(Transaction transaction){

        List<String> failedRequests = new ArrayList<>();
        List<String> succesfullResponse = new ArrayList<>();

        String[] availableApis = getAvailableIps(context);
        boolean worked = false;
        for(int i = 0 ;i<availableApis.length;i++) {

            System.out.println("Attempt: "+(i+1) +"/"+availableApis.length);
            String tempResponse = "";
            if(transaction.getTransactionType().equalsIgnoreCase(TransactionType.TYPE_FINE)){
                tempResponse =  pushMakeFineTransaction(transaction,availableApis[i]);
            }else if(transaction.getTransactionType().equalsIgnoreCase(TransactionType.TYPE_PAYMENT)){
                tempResponse =  pushMakePaymentTransaction(transaction,availableApis[i]);
            }else if(transaction.getTransactionType().equalsIgnoreCase(TransactionType.TYPE_PAY_ARREARS)){
                tempResponse =  pushPayArrearsTransaction(transaction,availableApis[i]);
            }else if(transaction.getTransactionType().equalsIgnoreCase(TransactionType.TYPE_PREPAYMENT)){
                tempResponse =  pushPayArrearsTransaction(transaction,availableApis[i]);
            }

            System.out.println("Response was :"+tempResponse);

             if(tempResponse.contains("Success") || tempResponse.contains("already")){
                //Worked with this IP
                succesfullResponse.add(tempResponse);
                worked = true;
                break;

            } else {
                failedRequests.add(tempResponse);
            }
        }

        String tempReponse ="";
        if(worked){
            tempReponse =succesfullResponse.get(0);
        }else{
            tempReponse = failedRequests.get(0);
        }

        return tempReponse;
    }

    private String pushMakeFineTransaction(Transaction transaction,String ip){

        InputStream inputStream = null;
        String result = "";
        try {
            String shiftID = transaction.getShiftId();
            String username = transaction.getUsername();
            String terminalID = String.valueOf(transaction.getTerminalId());
            String receiptNo = transaction.getRecieptNumber();
            String precinctID = transaction.getPrecinctID();
            String bayNumber = String.valueOf(transaction.getBayNumber());
            String requestDateTime = GeneralUtils.getCurrentDateTime();
            String vehicleReg = transaction.getVehicleregNumber();
            String duration = String.valueOf(transaction.getDuration());
            String in_date_time = String.valueOf(transaction.getIn_datetime());
            boolean isPaid = transaction.is_Paid();
            String paymentRefNo = transaction.getPaymentRefNo();


            HttpClient httpclient;
            //Set the timeout to 6 Seconds
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpConnectionParams.setSoTimeout(httpParams, 6000);

            httpclient = new DefaultHttpClient(httpParams);

            String url = "";

            System.out.println("Using SERVER IP address: "+ip);
            url += ip;

            url += context.getResources().getString(R.string.api_dev_make_fine);

            System.out.println("Request URL: "+url);
            // 2. make POST request to the given URL
            url +=""+shiftID+"/";
            url +=""+username+"/";
            url +=""+terminalID+"/";
            url +=""+receiptNo+"/";
            url +=""+precinctID+"/";
            //BAY NUMBER
            System.out.println("Bay Number is: "+bayNumber);
            url +=""+bayNumber+"/";
            url +=""+requestDateTime+"/";
            url +=""+vehicleReg+"/";
            url +=""+duration+"/";
            url +=""+in_date_time+"/";

            if (isPaid) {
                url = url + "0/";
            } else {
                url = url + "1/";
            }

            url += paymentRefNo + "/";

            System.out.println("URL Being Processed  "+url);

            System.out.println("\nMaking a Vehicle LOGIN request using GET: \n/{ShiftID}="+shiftID + "/\n{Username}="+
                    username+"/\n{TerminalID}="+terminalID+"/\n{RecieptNo}="+receiptNo+
                    "/\n{PrecinctID}="+precinctID+ "/\n{BayNumber}="+bayNumber+"/\n{RequestDateTime}="+requestDateTime+
                    "/\n{VehicleReg}="+vehicleReg+"/\n{Duration}="+duration+"/\n{In_Date_Time}="+in_date_time);

            HttpGet request = new HttpGet(url);

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(request);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            result = "errormsg : No Internet Connection";
        }

        // 11. return result
        return result;




    }


    private String pushMakePaymentTransaction(Transaction transaction,String ip){

        InputStream inputStream = null;
        String result = "";
        try {

            String shiftID = transaction.getShiftId();
            String username = transaction.getUsername();
            String terminalID = String.valueOf(transaction.getTerminalId());
            String receiptNo = transaction.getRecieptNumber();
            String precinctID = transaction.getPrecinctID();
            String requestDateTime = GeneralUtils.getCurrentDateTime();
            String vehicleReg = transaction.getVehicleregNumber();
            String payment_mode_id = String.valueOf(transaction.getPayment_mode_id());
            String amount = String.valueOf(transaction.getAmountPaid());
            String paymentDateTime = String.valueOf(transaction.getTransactionDateTime());
            String fineRefNo = transaction.getFineRefNo();

            HttpClient httpclient;
            //Set the timeout to 6 Seconds
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpConnectionParams.setSoTimeout(httpParams, 6000);

            httpclient = new DefaultHttpClient(httpParams);
            String url = "";
            System.out.println("Using SERVER IP address: "+ip);
            url += ip;

            url += context.getResources().getString(R.string.api_dev_make_payment);

            System.out.println("Request URL: "+url);
            // 2. make POST request to the given URL
            url +=""+shiftID+"/";
            url +=""+username+"/";
            url +=""+terminalID+"/";
            url +=""+precinctID+"/";
            url +=""+receiptNo+"/";
            url +=""+vehicleReg+"/";
            url +=""+payment_mode_id+"/";
            url +=""+amount+"/";
            url +=""+paymentDateTime+"/";
            url +=""+requestDateTime+"/";
            url +=""+fineRefNo+"/";

            System.out.println("URL Being Processed  "+url);

            System.out.println("\nMaking a Vehicle LOGIN request using GET: \n/{ShiftID}="+shiftID + "/\n{Username}="+
                    username+"/\n{TerminalID}="+terminalID+  "/\n{PrecinctID}="+precinctID+
                    "/\n{RecieptNo}="+receiptNo+ "/\n{VehicleReg}="+vehicleReg+
                    "/\n{payment_mode_id}="+payment_mode_id+
                    "/\n{amount}="+amount +
                    "/\n{paymentDateTime}="+paymentDateTime+"/\n{RequestDateTime}="+requestDateTime);

            HttpGet request = new HttpGet(url);

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(request);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            result = "errormsg : No Internet Connection";
        }

        // 11. return result
        return result;


    }

    private String pushPayArrearsTransaction(Transaction transaction,String ip){

        InputStream inputStream = null;
        String result = "";
        try {
            String shiftID = transaction.getShiftId();
            String username = transaction.getUsername();
            String terminalID = String.valueOf(transaction.getTerminalId());
            String receiptNo = transaction.getRecieptNumber();
            String precinctID = transaction.getPrecinctID();
            String requestDateTime = GeneralUtils.getCurrentDateTime();
            String vehicleReg = transaction.getVehicleregNumber();
            String payment_mode_id = String.valueOf(transaction.getPayment_mode_id());
            String amount = String.valueOf(transaction.getAmountPaid());
            String paymentDateTime = String.valueOf(transaction.getTransactionDateTime());



            HttpClient httpclient;
            //Set the timeout to 6 Seconds
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpConnectionParams.setSoTimeout(httpParams, 6000);
            httpclient = new DefaultHttpClient(httpParams);

            String url = "";

            System.out.println("Using SERVER IP address: "+ip);
            url += ip;
            url += context.getResources().getString(R.string.api_pay_arrears);

            System.out.println("Request URL: "+url);
            // 2. make POST request to the given URL
            url +=""+shiftID+"/";
            url +=""+username+"/";
            url +=""+terminalID+"/";
            url +=""+precinctID+"/";
            url +=""+receiptNo+"/";
            url +=""+vehicleReg+"/";
            url +=""+payment_mode_id+"/";
            url +=""+amount+"/";
            url +=""+paymentDateTime+"/";
            url +=""+requestDateTime+"/";

            System.out.println("URL Being Processed for PayArrears: "+url);

            System.out.println("\nMaking a PayArrears request using GET: \n/{ShiftID}="+shiftID + "/\n{Username}="+
                    username+"/\n{TerminalID}="+terminalID+  "/\n{PrecinctID}="+precinctID+
                    "/\n{RecieptNo}="+receiptNo+ "/\n{VehicleReg}="+vehicleReg+
                    "/\n{payment_mode_id}="+payment_mode_id+
                    "/\n{amount}="+amount +
                    "/\n{paymentDateTime}="+paymentDateTime+"/\n{RequestDateTime}="+requestDateTime);

            HttpGet request = new HttpGet(url);

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(request);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            result = "errormsg : No Internet Connection";
        }

        // 11. return result
        return result;



    }


    private String attemptStartNewShift(String ip,Shift shift){
        InputStream inputStream = null;
        String result = "";
        try {


            HttpClient httpclient;
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpConnectionParams.setSoTimeout(httpParams, 6000);
            httpclient = new DefaultHttpClient(httpParams);
            // 1. create HttpClient
            // HttpClient httpclient = new DefaultHttpClient();

            String url = "";
            //String ipAddress = ApiManager.getServerIP(context);
            //System.out.println("Using SERVER IP Address: "+ipAddress);
            // url += ipAddress;
            System.out.println("Trying to Sync Opening of Shift");
            System.out.println("Using SERVER IP address: "+ip);
            url += ip;
            url += context.getResources().getString(R.string.api_start_shift);
            url +=""+shift.getUsername()+"/";
            url +=""+shift.getTerminalID()+"/";
            url +=""+shift.getPrecinctID()+"/";
            url +=""+shift.getShiftId()+"/";
            url +=""+shift.getStart_time()+"/";
            url +=""+new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+"/";

            System.out.println(url);

            System.out.println("Making a request using GET: "+shift.getUsername()+"/"+shift.getTerminalID()+"/"+shift.getPrecinctID()+"/"+new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));

            HttpGet request = new HttpGet(url);

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(request);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            result = "errormsg : No Internet Connection!";
        }

        // 11. return result
        return result;


    }

}
