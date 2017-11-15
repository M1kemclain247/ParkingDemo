package com.example.m1kes.parkingdemo.services;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.m1kes.parkingdemo.LoginScreen;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MyWebService extends IntentService {

    private static final String LOG_TAG = "MyWebService";
    public static final String REQUEST_STRING = "/AppUpdater";


    public static final String RESPONSE_STRING = "myResponse";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";

    private String URL = null;
    private static final int REGISTRATION_TIMEOUT = 3 * 1000;
    private static final int WAIT_TIMEOUT = 30 * 1000;


    int noAttemps = 0;
    public static final int maxAttemps = 2;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * Used to name the worker thread, important only for debugging.
     */

    public MyWebService() {
        super("MyWebService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        boolean worked = false;

        String [] apis = {"http://192.168.1.6:8080","http://192.168.100.6:8080"};


        for(int i = 0 ;noAttemps<maxAttemps;noAttemps++,i++ ){

            String requestString = apis[i]+""+REQUEST_STRING;
            Log.v(LOG_TAG, requestString);
            String responseMessage = "";

            try {

                System.out.println("Requesting with URL: "+requestString);

                URL = requestString;
                HttpClient httpclient = new DefaultHttpClient();
                HttpParams params = httpclient.getParams();

                HttpConnectionParams.setConnectionTimeout(params, REGISTRATION_TIMEOUT);
                HttpConnectionParams.setSoTimeout(params, WAIT_TIMEOUT);
                ConnManagerParams.setTimeout(params, WAIT_TIMEOUT);

                HttpGet httpGet = new HttpGet(URL);
                HttpResponse response = httpclient.execute(httpGet);

                StatusLine statusLine = response.getStatusLine();

                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseMessage = out.toString();
                    System.out.println("Response Message: "+responseMessage);
                    worked = true;
                }

                else{
                    Log.w("HTTP1:",statusLine.getReasonPhrase());
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }



            } catch (ClientProtocolException e) {
                Log.w("HTTP2:",e );
                responseMessage = e.getMessage();
            } catch (IOException e) {
                Log.w("HTTP3:",e );
                responseMessage = e.getMessage();
            }catch (Exception e) {
                Log.w("HTTP4:",e );
                responseMessage = e.getMessage();
            }


            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(LoginScreen.MyWebReceiver.PROCESS_RESPONSE);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(RESPONSE_MESSAGE, responseMessage);
            sendBroadcast(broadcastIntent);

            if(worked)
                break;

        }



    }
}
