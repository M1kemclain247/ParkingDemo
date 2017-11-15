package com.example.m1kes.parkingdemo.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.callbacks.RegisterPrinterResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.example.m1kes.parkingdemo.networking.Converter.convertInputStreamToString;
import static com.example.m1kes.parkingdemo.settings.manager.ApiManager.getAvailableIps;


/**
 * Created by clive on 24/11/2016.
 * This will register the printer to the database
 */

public class PrinterRegisterTask extends AsyncTask<String,Integer,Void> {

    private Handler mHandler;
    private Context context;
    private String printerMac;
    RegisterPrinterResponse registerPrinterResponse;
 //   ProgressDialog dialog = new ProgressDialog(context);



    public PrinterRegisterTask(Context context, String printerMac, RegisterPrinterResponse registerPrinterResponse) {
        System.out.println("Printer Reg Constructor");
        this.registerPrinterResponse = registerPrinterResponse;
        this.context = context;
        this.mHandler = new Handler();
        this.printerMac = printerMac;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //dialog.setMessage("Loading...");
    }



    @Override
    protected Void doInBackground(String... params) {

        String username = UserAdapter.getLoggedInUser(context);
        int terminalID = ShiftDataManager.getDefaultTerminalID(context);// get from database

        printerMac = printerMac.replaceAll("[^a-z A-Z 0-9]", "");


        List<String> failedRequests = new ArrayList<>();
        List<String> succesfullResponse = new ArrayList<>();

        String[] availableApis = getAvailableIps(context);
        boolean worked = false;
        for (int i = 0; i < availableApis.length; i++) {

            System.out.println("Attempt: " + (i + 1) + "/" + availableApis.length);

            final String tempResponse = attemptToPairPrinter(availableApis[i], username, String.valueOf(terminalID), printerMac);
            Pattern pattern = Pattern.compile("errormsg|HTTP|Tunnel|Did not work");
            if (pattern.matcher(tempResponse).find()) {
                //Failed to connect with this IP
                failedRequests.add(tempResponse);
            } else {
                //Worked with this IP
                succesfullResponse.add(tempResponse);
                worked = true;
                break;
            }
        }


        String tempReponse = "";
        if (worked) {
            tempReponse = succesfullResponse.get(0);
        } else {
            tempReponse = failedRequests.get(0);
        }

        final String strResponse = tempReponse;


        System.out.println("Getting response: " + strResponse);

        if (strResponse.contains("errormsg") || strResponse.contains("HTTP") || strResponse.contains("Tunnel") ||
                strResponse.contains("incorrect") || strResponse.contains("Runtime") || strResponse.contains("errormessage") ||
                strResponse.contains("not") || strResponse.contains("No Connection")) {

            System.out.println("Error: " + strResponse);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Failed
                    System.out.println("Print Reg fail");
                    registerPrinterResponse.onRegisterFail(strResponse);
                }
            });

        } else {
            System.out.println("Success: " + strResponse);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Successful
                    System.out.println("Print Done:" + strResponse);
                    System.out.println("Print Reg Success");
                    registerPrinterResponse.onRegisterSuccess(strResponse);
                }
            });

        }

        return null;
    }



    public String attemptToPairPrinter(String ip,String username,String terminalID,String printermac){

        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            //Set the timeout to 6 Seconds
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpConnectionParams.setSoTimeout(httpParams, 6000);

            HttpClient httpclient = new DefaultHttpClient(httpParams);

            String url = "";
            //String ipAddress = ApiManager.getServerIP(context);
            //System.out.println("Using SERVER IP Address: "+ipAddress);
            // url += ipAddress;
            System.out.println("Using SERVER IP address: "+ip);
            url += ip;
            url += context.getResources().getString(R.string.api_register_printer);

            System.out.println("Request URL: "+url);
            // 2. make POST request to the given URL

            url +=""+username+"/";
            url +=""+terminalID+"/";
            url +=""+printermac+"/";

            System.out.println(" Registering a printer request using GET:  " + "/"+username+"/"+terminalID+"/"+printermac);

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
            result = "errormsg: No Connection";
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
}

