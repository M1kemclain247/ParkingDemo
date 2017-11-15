package com.example.m1kes.parkingdemo.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.callbacks.StartShiftResponse;
import com.example.m1kes.parkingdemo.models.Shift;
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
import java.util.List;
import java.util.regex.Pattern;

import static com.example.m1kes.parkingdemo.networking.Converter.convertInputStreamToString;
import static com.example.m1kes.parkingdemo.settings.manager.ApiManager.getAvailableIps;


public class SyncShiftTask extends AsyncTask<Shift,Void,Void> {

    private Context context;
    private Handler mHandler;


    public SyncShiftTask(Context context){
        this.context = context;
        mHandler = new Handler();
    }


    @Override
    protected Void doInBackground(Shift... params) {

        sleep(1000);

        final Shift shift = params[0];


        List<String> failedRequests = new ArrayList<>();
        List<String> succesfullResponse = new ArrayList<>();

        String[] availableApis = getAvailableIps(context);
        boolean worked = false;
        for (int i = 0; i < availableApis.length; i++) {

            System.out.println("Attempt: " + (i + 1) + "/" + availableApis.length);

            final String tempResponse = attemptStartNewShift(availableApis[i],shift);

            if (tempResponse.contains("DeviceShiftCode")) {
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

        final String strResponse = tempReponse;


        System.out.println("Response is :" + strResponse);


         if(strResponse.contains("DeviceShiftCode") || strResponse.contains("already exist")){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //SUCCESSFULL
                    System.out.println("SUCCESSFUL WITH RESPONSE: \n");
                    System.out.println(strResponse);
                    ShiftsAdapter.setShiftStartSynced(shift.getShiftId(),context);
                }
            });

        } else {

            mHandler.post(new Runnable() {
            @Override
            public void run() {
                System.out.println(strResponse);
            }
        });

        }

        return null;
    }




    private String attemptStartNewShift(String ip,Shift shift){
        InputStream inputStream = null;
        String result = "";
        try {


            HttpClient httpclient;

            //Set the timeout to 6 Seconds
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpConnectionParams.setSoTimeout(httpParams, 6000);

            httpclient = new DefaultHttpClient(httpParams);
            String url = "";
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

        // 11. return result
        return result;


    }





    private void sleep(long sleepTime){
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
