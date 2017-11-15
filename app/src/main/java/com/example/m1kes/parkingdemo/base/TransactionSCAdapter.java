package com.example.m1kes.parkingdemo.base;

import android.content.Context;
import android.util.Log;
import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.networking.Converter;
import com.example.m1kes.parkingdemo.settings.manager.ApiManager;
import com.example.m1kes.parkingdemo.util.GeneralUtils;
import com.zebra.sdk.util.internal.StringUtilities;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Stack;
import java.util.regex.Pattern;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class TransactionSCAdapter {
    private Context context;
    private Pattern pattern = Pattern.compile("errormsg|HTTP|Tunnel|Did not work|incorrect|error");
    private Stack<Transaction> transactionStack;

    public TransactionSCAdapter(){}

    public TransactionSCAdapter(List<Transaction> transactions, Context context) {
        this.context = context;
        this.transactionStack = new Stack();
        System.out.println("Attempting to add " + transactions.size() + " transactions to Queue...  \n");
        for (Transaction transaction : transactions) {
            this.transactionStack.push(transaction);
        }
        System.out.println("Done! \n");
    }

    public boolean attemptSyncOpenShift(Shift shift) {
        List<String> failedRequests = new ArrayList();
        List<String> succesfullResponse = new ArrayList();
        String[] availableApis = ApiManager.getAvailableIps(this.context);
        boolean worked = false;
        for (int i = 0; i < availableApis.length; i++) {
            System.out.println("Attempt: " + (i + 1) + "/" + availableApis.length);
            String tempResponse = attemptStartNewShift(availableApis[i], shift);
            if (!this.pattern.matcher(tempResponse).find()) {
                succesfullResponse.add(tempResponse);
                worked = true;
                break;
            }
            failedRequests.add(tempResponse);
        }
        String tempReponse = "";
        if (worked) {
            tempReponse = (String) succesfullResponse.get(0);
        } else {
            tempReponse = (String) failedRequests.get(0);
        }
        if (this.pattern.matcher(tempReponse).find()) {
            return false;
        }
        return true;
    }

    public List<Transaction> PushAllTransactions() {
        Map<String, Transaction> responses = new HashMap();
        Random r = new Random();
        System.out.println("Transaction Stack Contains: " + this.transactionStack);
        System.out.println("Attempting to Resend " + this.transactionStack.size() + "  Transactions \n");
        List<Transaction> failedTransactions = new ArrayList();
        List<Transaction> completedTransactions = new ArrayList();
        try {
            Iterator<Transaction> iterator = this.transactionStack.iterator();
            while (iterator.hasNext()) {
                int Result = r.nextInt(989) + 10;
                Transaction transaction = (Transaction) iterator.next();
                System.out.println("**************************************\n");
                System.out.println(transaction + StringUtilities.LF);
                System.out.println("**************************************\n");
                if (transaction.getTransactionType().equalsIgnoreCase("MakeFine")) {
                    responses.put(tryDifferentApis(transaction) + "/" + Result, transaction);
                } else if (transaction.getTransactionType().equalsIgnoreCase("MakePayment")) {
                    responses.put(tryDifferentApis(transaction) + "/" + Result, transaction);
                } else if (transaction.getTransactionType().equalsIgnoreCase("PayArrears") || transaction.getTransactionType().equalsIgnoreCase("Prepayment")) {
                    responses.put(tryDifferentApis(transaction) + "/" + Result, transaction);
                }
            }
            System.out.println("Iterating through results of Transactions Push TotalNo: " + responses.size() + StringUtilities.LF);
            for (Entry<String, Transaction> entry : responses.entrySet()) {
                System.out.println("**************************************\n");
                System.out.println(((String) entry.getKey()) + " / " + entry.getValue() + StringUtilities.LF);
                System.out.println("**************************************\n");
                Pattern pattern = Pattern.compile("errormsg|HTTP|Tunnel|Did not work");
                if (((String) entry.getKey()).contains("Success") || ((String) entry.getKey()).contains("already")) {
                    System.out.println("Setting synced");
                    ((Transaction) entry.getValue()).setIs_synced(true);
                    completedTransactions.add(entry.getValue());
                    com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter.setSynced((Transaction) entry.getValue(), this.context);
                } else if (pattern.matcher((CharSequence) entry.getKey()).find()) {
                    failedTransactions.add(entry.getValue());
                } else {
                    failedTransactions.add(entry.getValue());
                }
            }
            System.out.println("Successfully synced " + completedTransactions.size() + " Transactions successfully!");
            if (failedTransactions.size() > 0) {
                System.out.println("Failed to upload " + failedTransactions.size() + " Transactions!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return failedTransactions;
    }

    private String tryDifferentApis(Transaction transaction) {
        List<String> failedRequests = new ArrayList();
        List<String> succesfullResponse = new ArrayList();
        String[] availableApis = ApiManager.getAvailableIps(this.context);
        boolean worked = false;
        for (int i = 0; i < availableApis.length; i++) {
            System.out.println("Attempt: " + (i + 1) + "/" + availableApis.length);
            String tempResponse = "";
            if (transaction.getTransactionType().equalsIgnoreCase("MakeFine")) {
                tempResponse = pushMakeFineTransaction(transaction, availableApis[i]);
            } else if (transaction.getTransactionType().equalsIgnoreCase("MakePayment")) {
                tempResponse = pushMakePaymentTransaction(transaction, availableApis[i]);
            } else if (transaction.getTransactionType().equalsIgnoreCase("PayArrears") || transaction.getTransactionType().equalsIgnoreCase("Prepayment")) {
                tempResponse = pushPayArrearsTransaction(transaction, availableApis[i]);
            }
            Pattern pattern = Pattern.compile("errormsg|HTTP|Tunnel|Did not work");
            if ((tempResponse.contains("Success") | tempResponse.contains("already"))) {
                System.out.println("Going to set to synced");
                succesfullResponse.add(tempResponse);
                worked = true;
                break;
            }
            if (pattern.matcher(tempResponse).find()) {
                failedRequests.add(tempResponse);
            } else {
                failedRequests.add(tempResponse);
            }
        }
        String tempReponse = "";
        if (worked) {
            return (String) succesfullResponse.get(0);
        }
        return (String) failedRequests.get(0);
    }

    private String pushMakeFineTransaction(Transaction transaction, String ip) {
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
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpClient httpclient = new DefaultHttpClient(httpParams);
            System.out.println("Using SERVER IP address: " + ip);
            String url = ("" + ip) + this.context.getResources().getString(R.string.api_dev_make_fine);
            System.out.println("Request URL: " + url);
            url = ((((url + "" + shiftID + "/") + "" + username + "/") + "" + terminalID + "/") + "" + receiptNo + "/") + "" + precinctID + "/";
            System.out.println("Bay Number is: " + bayNumber);
            url = ((((url + "" + bayNumber + "/") + "" + requestDateTime + "/") + "" + vehicleReg + "/") + "" + duration + "/") + "" + in_date_time + "/";
            if (isPaid) {
                url = url + "0/";
            } else {
                url = url + "1/";
            }
            url = url + "" + paymentRefNo + "/";
            System.out.println("URL Being Processed  " + url);
            System.out.println("\nMaking a Vehicle LOGIN request using GET: \n/{ShiftID}=" + shiftID + "/\n{Username}=" + username + "/\n{TerminalID}=" + terminalID + "/\n{RecieptNo}=" + receiptNo + "/\n{PrecinctID}=" + precinctID + "/\n{BayNumber}=" + bayNumber + "/\n{RequestDateTime}=" + requestDateTime + "/\n{VehicleReg}=" + vehicleReg + "/\n{Duration}=" + duration + "/\n{In_Date_Time}=" + in_date_time);
            InputStream inputStream = httpclient.execute(new HttpGet(url)).getEntity().getContent();
            if (inputStream == null) {
                return "Did not work!";
            }
            result = Converter.convertInputStreamToString(inputStream);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            return "errormsg : No Internet Connection";
        }
    }

    private String pushMakePaymentTransaction(Transaction transaction, String ip) {
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
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpClient httpclient = new DefaultHttpClient(httpParams);
            System.out.println("Using SERVER IP address: " + ip);
            String url = ("" + ip) + this.context.getResources().getString(R.string.api_dev_make_payment);
            System.out.println("Request URL: " + url);
            url = ((((((((((url + "" + shiftID + "/") + "" + username + "/") + "" + terminalID + "/") + "" + precinctID + "/") + "" + receiptNo + "/") + "" + vehicleReg + "/") + "" + payment_mode_id + "/") + "" + amount + "/") + "" + paymentDateTime + "/") + "" + requestDateTime + "/") + "" + fineRefNo + "/";
            System.out.println("URL Being Processed  " + url);
            System.out.println("\nMaking a Vehicle LOGIN request using GET: \n/{ShiftID}=" + shiftID + "/\n{Username}=" + username + "/\n{TerminalID}=" + terminalID + "/\n{PrecinctID}=" + precinctID + "/\n{RecieptNo}=" + receiptNo + "/\n{VehicleReg}=" + vehicleReg + "/\n{payment_mode_id}=" + payment_mode_id + "/\n{amount}=" + amount + "/\n{paymentDateTime}=" + paymentDateTime + "/\n{RequestDateTime}=" + requestDateTime);
            InputStream inputStream = httpclient.execute(new HttpGet(url)).getEntity().getContent();
            if (inputStream != null) {
                return Converter.convertInputStreamToString(inputStream);
            }
            return "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            return "errormsg : No Internet Connection";
        }
    }

    private String pushPayArrearsTransaction(Transaction transaction, String ip) {
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
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpClient httpclient = new DefaultHttpClient(httpParams);
            System.out.println("Using SERVER IP address: " + ip);
            String url = ("" + ip) + this.context.getResources().getString(R.string.api_pay_arrears);
            System.out.println("Request URL: " + url);
            url = (((((((((url + "" + shiftID + "/") + "" + username + "/") + "" + terminalID + "/") + "" + precinctID + "/") + "" + receiptNo + "/") + "" + vehicleReg + "/") + "" + payment_mode_id + "/") + "" + amount + "/") + "" + paymentDateTime + "/") + "" + requestDateTime + "/";
            System.out.println("URL Being Processed for PayArrears: " + url);
            System.out.println("\nMaking a PayArrears request using GET: \n/{ShiftID}=" + shiftID + "/\n{Username}=" + username + "/\n{TerminalID}=" + terminalID + "/\n{PrecinctID}=" + precinctID + "/\n{RecieptNo}=" + receiptNo + "/\n{VehicleReg}=" + vehicleReg + "/\n{payment_mode_id}=" + payment_mode_id + "/\n{amount}=" + amount + "/\n{paymentDateTime}=" + paymentDateTime + "/\n{RequestDateTime}=" + requestDateTime);
            InputStream inputStream = httpclient.execute(new HttpGet(url)).getEntity().getContent();
            if (inputStream != null) {
                return Converter.convertInputStreamToString(inputStream);
            }
            return "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            return "errormsg : No Internet Connection";
        }
    }

    private String attemptStartNewShift(String ip, Shift shift) {
        String result = "";
        try {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpClient httpclient = new DefaultHttpClient(httpParams);
            System.out.println("Using SERVER IP address: " + ip);
            String url = ((((((("" + ip) + this.context.getResources().getString(R.string.api_start_shift)) + "" + shift.getUsername() + "/") + "" + shift.getTerminalID() + "/") + "" + shift.getPrecinctID() + "/") + "" + shift.getShiftId() + "/") + "" + shift.getStart_time() + "/") + "" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + "/";
            System.out.println("Making a request using GET: " + shift.getUsername() + "/" + shift.getTerminalID() + "/" + shift.getPrecinctID() + "/" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
            InputStream inputStream = httpclient.execute(new HttpGet(url)).getEntity().getContent();
            if (inputStream != null) {
                return Converter.convertInputStreamToString(inputStream);
            }
            return "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            return "errormsg : No Internet Connection!";
        }
    }
}
