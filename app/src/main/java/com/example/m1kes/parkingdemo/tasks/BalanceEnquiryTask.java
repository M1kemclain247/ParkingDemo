package com.example.m1kes.parkingdemo.tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.callbacks.VehicleFoundResponse;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
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
import java.util.List;
import java.util.regex.Pattern;

import static com.example.m1kes.parkingdemo.networking.Converter.convertInputStreamToString;
import static com.example.m1kes.parkingdemo.settings.manager.ApiManager.getAvailableIps;

public class BalanceEnquiryTask extends AsyncTask<String,Void,Void > {


    private Context context;
    private Handler mHandler;
    private VehicleFoundResponse response;



    public BalanceEnquiryTask(Context context, VehicleFoundResponse response){
        this.context = context;
        mHandler = new Handler();
        this.response = response;
    }


    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Void doInBackground(final String... params) {

        Boolean foundVehicle = false;


        String username = UserAdapter.getLoggedInUser(context);
        final String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        String terminalID = String.valueOf(ShiftDataManager.getDefaultTerminalID(context));
        long requestDateTime =  Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        String vehicleReg = params[0];


        List<String> failedRequests = new ArrayList<>();
        List<String> succesfullResponse = new ArrayList<>();

        String[] availableApis = getAvailableIps(context);
        boolean worked = false;
        for(int i = 0 ;i<availableApis.length;i++) {

            System.out.println("Attempt: "+(i+1) +"/"+availableApis.length);

            final String tempResponse = searchForVehicle(availableApis[i],shiftID,username,terminalID,requestDateTime,vehicleReg);

            if (tempResponse.contains("lastlogged")) {
                //Worked with this IP
                succesfullResponse.add(tempResponse);
                worked = true;
                break;

            } else {
                //Failed to connect with this IP
                failedRequests.add(tempResponse);
            }
        }


        String tempReponse ="";
        if(worked){
            tempReponse =succesfullResponse.get(0);
        }else{
            tempReponse = failedRequests.get(0);
        }

        final String strResponse = tempReponse;



            if(strResponse.contains("lastlogged")) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(strResponse);
                        response.vehicleFound(strResponse);

                    }
                });
            }else{
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(strResponse);
                        response.notFound(strResponse);
                    }
                });
            }


        return null;

    }

    private String searchForVehicle(String ip,String shiftID, String username, String terminalID,long requestDateTime,
                                    String vehicleReg){
        InputStream inputStream = null;
        String result = "";
        try {

            //Set the timeout to 6 Seconds
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpConnectionParams.setSoTimeout(httpParams, 6000);

            HttpClient httpclient = new DefaultHttpClient(httpParams);

            // 1. create HttpClient
           // = new DefaultHttpClient();



            String url = "";
            //String ipAddress = ApiManager.getServerIP(context);
            //System.out.println("Using SERVER IP Address: "+ipAddress);
            // url += ipAddress;
            System.out.println("Using SERVER IP address: "+ip);
            url += ip;
            url += context.getResources().getString(R.string.api_vehicle_balance_enquiry);
            // 2. make POST request to the given URL
            url += ""+shiftID+"/";
            url +=""+username+"/";
            url +=""+terminalID+"/";
            url +=""+requestDateTime+"/";
            url +=""+vehicleReg+"/";

            System.out.println("Search Vehicle request using GET: "+shiftID +"/"+username+"/"+terminalID+"/"+requestDateTime+"/"+vehicleReg);
            HttpGet request = new HttpGet(url);

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(request);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "errormsg";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
            result = "errormsg : No Internet Connection!";
        }
        // 11. return result
        return result;
    }



}
