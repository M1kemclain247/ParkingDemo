package com.example.m1kes.parkingdemo.base;


import android.content.Context;
import android.util.Log;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;

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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import static com.example.m1kes.parkingdemo.networking.Converter.convertInputStreamToString;
import static com.example.m1kes.parkingdemo.settings.manager.ApiManager.getAvailableIps;

public class ShiftSyncManager {

    private Context context;
    private List<Shift> shifts;


    public ShiftSyncManager(Context context){
        this.context = context;
        this.shifts =  ShiftsAdapter.getAllLocalShifts(context);
    }


    public boolean attemptSyncOpenShift(Shift shift){


        List<String> failedRequests = new ArrayList<>();
        List<String> succesfullResponse = new ArrayList<>();

        String[] availableApis = getAvailableIps(context);
        boolean worked = false;
        for (int i = 0; i < availableApis.length; i++) {

            System.out.println("Attempt: " + (i + 1) + "/" + availableApis.length);

            final String tempResponse = attemptStartNewShift(availableApis[i],shift);

            if(tempResponse.contains("DeviceShiftCode") || tempResponse.contains("already exist")){
                succesfullResponse.add(tempResponse);
                worked = true;
                break;
            }else {
                //Worked with this IP
                failedRequests.add(tempResponse);
            }
        }


        String tempReponse = "";
        if (worked) {
            tempReponse = succesfullResponse.get(0);
        } else {
            tempReponse = failedRequests.get(0);
        }


        if(tempReponse.contains("DeviceShiftCode") || tempReponse.contains("already exist")){

            //Successfull
            return true;
        }else{
            //Failed
            return false;
        }
    }


    public boolean startSyncing(){

        Map<String ,Shift> shiftMap = new HashMap<>();


        boolean syncSuccessfull = false;


        boolean failedSyncingTransactionsForShift = false;
        boolean shiftOpened = false;
        boolean shiftClosed = false;

        Random r = new Random();
        int Low = 10;
        int High = 999;

        for (Shift s: shifts){
            int Result = r.nextInt(High-Low) + Low;
            System.out.println("Trying to set shift open synced with server");
           if(s.isStartShiftSynced()||attemptSyncOpenShift(s)){
               System.out.println("Shift Start Synced with server");
               if(s.isStartShiftSynced())
                   System.out.println("This Shift has already been synced with the server");
               shiftOpened = true;
               //Set in Local DB to synced
               System.out.println("Setting shift Synced in DB");
               ShiftsAdapter.setShiftStartSynced(s.getShiftId(),context);
               //Shift is opened lets sync all of the transactions for that shift
               //Get all unsynced transactions for that shift
               System.out.println("Getting all unsynced transactions for this shift");
               List<Transaction>  transactions = com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter.getAllUnSyncedTransactionsForShift(
                       s.getShiftId(),context) ;
               TransactionAdapter adapter = new TransactionAdapter(transactions,context);
               System.out.println("Pushing all unsynced transactions to server");
               List<Transaction> failedTransactions =  adapter.PushAllTransactions();
               //If any transactions Failed to Sync then stop everything
               if(!failedTransactions.isEmpty()){
                   System.out.println("Some transactions havent been synced Exiting loop");
                   failedSyncingTransactionsForShift =true;
                   break;
               }
           }

            System.out.println("Trying to set shift close synced with server");
            //Try Closing the shift will only get here if the transactions Synced
            if(!s.isActive()) {
                //Only if the shift has been closed locally do we try to close the shift online
                if (attemptSyncCloseShift(s)) {
                    shiftClosed = true;
                    System.out.println("Shift Close Synced with server");
                    //Set in Local DB to synced
                    ShiftsAdapter.setShiftCloseSynced(s.getShiftId(), context);
                }
            }
            if(shiftOpened&&shiftClosed&&!s.isActive()){
                System.out.println("Everything should have worked the shift should be opened and closed and all transactions associated synced too!");
                shiftMap.put("Synced"+Result,s);
                //Remove that shift from DB
                ShiftsAdapter.deleteShift(s.getShiftId(),context);
                //Then remove all transactions for that shift too
                com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter.deleteAllTransactionsForShift(s.getShiftId(),context);
            }else{
                System.out.println("The shift wasn't synced open and closed");
            }
        }

        System.out.println("Checking the number of completed Transactions");
        int numSuccess = 0;
        for(String key: shiftMap.keySet()){
            numSuccess++;
        }

        System.out.println("Comparing num Synced: "+numSuccess +" Number of Shifts "+shifts.size());

        if(numSuccess==shifts.size()){
            //All of the shifts Synced Succesfully
            System.out.println("All of the shifts Synced Successfully");
            syncSuccessfull = true;
        }


        return syncSuccessfull;

    }


    public boolean attemptSyncCloseShift(Shift shift){


        List<String> failedRequests = new ArrayList<>();
        List<String> succesfullResponse = new ArrayList<>();

        String[] availableApis = getAvailableIps(context);
        boolean worked = false;
        for (int i = 0; i < availableApis.length; i++) {

            System.out.println("Attempt: " + (i + 1) + "/" + availableApis.length);

            final String tempResponse = attemptCloseExistingShift(availableApis[i],shift);

           if(tempResponse.contains("End_Time")|tempResponse.contains("could not be found")){
                //Worked with this IP
                succesfullResponse.add(tempResponse);
                worked = true;
                break;
            } else {
                failedRequests.add(tempResponse);

            }
        }


        String tempReponse = "";
        if (worked) {
            tempReponse = succesfullResponse.get(0);
        } else {
            tempReponse = failedRequests.get(0);
        }


        if(tempReponse.contains("End_Time")|tempReponse.contains("could not be found")){
            //Successfull
            return true;
        }else{
            //Failed
            return false;
        }
    }


    private String attemptCloseExistingShift(String ip,Shift shift){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient;
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpConnectionParams.setSoTimeout(httpParams, 6000);
            httpclient = new DefaultHttpClient(httpParams);

            String requestDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String url = "";
            System.out.println("Using SERVER IP address: "+ip);
            url += ip;
            url += context.getResources().getString(R.string.api_close_shift);
            // 2. make POST request to the given URL
            //TODO ONCE RUNNING THE NEW SERVER SET THE NEW PARAMETERS
            url +=""+shift.getUsername()+"/";
            url +=""+shift.getTerminalID()+"/";
            url +=""+shift.getPrecinctID()+"/";
            url += ""+shift.getShiftId()+"/";
            url += ""+shift.getTotal_declaredAmount()+"/";
            url +=""+shift.getTotal_collected()+"/";
            url += ""+shift.getEnd_time()+"/";
            url +=""+requestDateTime+"/";

            System.out.println("Making a Close SHIFT request using GET: "+url);
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
            System.out.println("Using SERVER IP address: "+ip);
            url += ip;
            url += context.getResources().getString(R.string.api_start_shift);
            url +=""+shift.getUsername()+"/";
            url +=""+shift.getTerminalID()+"/";
            url +=""+shift.getPrecinctID()+"/";
            url +=""+shift.getShiftId()+"/";
            url +=""+shift.getStart_time()+"/";
            url +=""+new SimpleDateFormat("yyyyMMddhhmmss").format(new Date())+"/";

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

        System.out.println("Result: "+result);

        // 11. return result
        return result;


    }

}
