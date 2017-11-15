package com.example.m1kes.parkingdemo.util.web;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.settings.developer.DeveloperSettings;
import com.example.m1kes.parkingdemo.util.DebugLogger;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import static com.example.m1kes.parkingdemo.networking.Converter.convertInputStreamToString;

public class URLResolver {



    public static String MakePost(List<String> items,Context context)
    {
        String[] availableApis ;

        if(DeveloperSettings.isDeveloperModeEnabled(context)){
            availableApis =  new String[] { context.getResources().getString(R.string.localIP),context.getResources().getString(R.string.hostedIP)};
        }else{
            availableApis =  new String[] { context.getResources().getString(R.string.dev_localIP),context.getResources().getString(R.string.dev_hostedIP) };
        }

        String jsonString = null;
        String result = "";

        for (int i = 0; i < availableApis.length; i++) {
            InputStream inputStream = null;

            try {


                HttpClient httpclient;
                final HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
                httpclient = new DefaultHttpClient(httpParams);
                // 1. create HttpClient
                // HttpClient httpclient = new DefaultHttpClient();
                //Start Loop
                String tempURI = availableApis[i];

                //Append items to Url
                for (String s : items) {
                    tempURI += s;
                }

                System.out.println("Request is: " + tempURI);

                HttpGet request = new HttpGet(tempURI);

                // 8. Execute POST request to the given URL
                HttpResponse httpResponse = httpclient.execute(request);

                // 9. receive response as inputStream
                inputStream = httpResponse.getEntity().getContent();

                // 10. convert inputstream to string
                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
                result = "errormsg : unable to connect to server";
            }

            Pattern pattern = Pattern.compile("Success|successfully|DeviceShiftCode|lastlogged|balance|already registered|Activated|already exist|End_Time");
            if (pattern.matcher(result).find()) {
                break;
            }
        }

            // 11. return result
            return result;
    }



    public JSONArray getContentFromUri(Context context, String url){

        RequestQueue queue = Volley.newRequestQueue(context);
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(url,future,future);
        queue.add(request);
        JSONArray response = null;
        try {
              response =  future.get(4, TimeUnit.SECONDS);// Blocks for at most 4 seconds.
              return response;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (TimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }




}
