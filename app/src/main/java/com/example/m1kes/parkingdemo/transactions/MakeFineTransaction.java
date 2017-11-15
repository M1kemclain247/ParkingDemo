package com.example.m1kes.parkingdemo.transactions;



import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.MainActivity;
import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.SyncTransactions;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.modules.supervisor.util.TransactionDumper;
import com.example.m1kes.parkingdemo.settings.developer.DeveloperSettings;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.transactions.callbacks.OnMakeFineResponse;

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

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.app.NotificationManager;
import android.support.v4.app.TaskStackBuilder;

import static com.example.m1kes.parkingdemo.networking.Converter.convertInputStreamToString;
import static com.example.m1kes.parkingdemo.settings.manager.ApiManager.getAvailableIps;


public class MakeFineTransaction extends AsyncTask<Transaction,Integer,Void>{

    private Context context;
    private Handler mHandler;
    private NotificationManager mNotifyManager;
    private Builder mBuilder;
    private OnMakeFineResponse response;

    boolean synced = false;
    int id = 1;


    public MakeFineTransaction(Context context){
        this.context =context;
        mHandler = new Handler();

    }

    public MakeFineTransaction(Context context, OnMakeFineResponse response){
        this.context =context;
        mHandler = new Handler();
        this.response = response;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mNotifyManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("Syncing Transaction")
                .setContentText("Uploading Transaction")
                .setSmallIcon(R.drawable.ic_car_black_24dp);

        // Displays the progress bar for the first time.
        mBuilder.setProgress(100, 0, false);
        mNotifyManager.notify(id, mBuilder.build());
    }

    @Override
    protected Void doInBackground(Transaction... params) {

            // Sets the progress indicator completion percentage
            publishProgress(50);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        final Transaction transaction = params[0];

        System.out.println("This is the transaction about to be sent: "+transaction);



        List<String> failedRequests = new ArrayList<>();
        List<String> succesfullResponse = new ArrayList<>();

        String[] availableApis = getAvailableIps(context);
        boolean worked = false;
        for(int i = 0 ;i<availableApis.length;i++) {

            System.out.println("Attempt: "+(i+1) +"/"+availableApis.length);

            final String tempResponse =  attemptLoginVehicle(availableApis[i],transaction.getShiftId(),transaction.getUsername(),
                    String.valueOf(transaction.getTerminalId()),transaction.getRecieptNumber(), transaction.getPrecinctID(),
                    String.valueOf(transaction.getBayNumber()), String.valueOf(transaction.getRequestDateTime()),
                    transaction.getVehicleregNumber(),String.valueOf(transaction.getDuration()),String.valueOf(transaction.getIn_datetime()),transaction.is_Paid(),transaction.getPaymentRefNo());


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


        String tempReponse ="";
        if(worked){
            tempReponse =succesfullResponse.get(0);
        }else{
            tempReponse = failedRequests.get(0);
        }

        final String strResponse = tempReponse;


        System.out.println("Received Response : "+strResponse);





        publishProgress(100);

        if(strResponse.contains("errormsg")||strResponse.contains("HTTP")||strResponse.contains("Tunnel")||
                strResponse.contains("incorrect")){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Failed
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(context, SyncTransactions.class);

                    // The stack builder object will contain an artificial back stack for
                    // the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(MainActivity.class);
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    mBuilder.setContentTitle("Failed to Upload Transaction")
                    .setContentText("Click to retry");
                    mBuilder.setProgress(0, 0, false);
                    mNotifyManager.notify(id, mBuilder.build());
                    Toast.makeText(context,"Failed to Upload Transaction, Will try again just now..",Toast.LENGTH_SHORT).show();
                    System.out.println("Received Response : "+strResponse);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mNotifyManager.cancel(id);
                        }
                    }, 15000);

                }
            });
        } else if(strResponse.contains("Success")||strResponse.contains("already processed")){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Successful
                    synced = true;
                    mBuilder.setContentTitle("Complete");
                    mBuilder.setContentText("Upload Transaction Complete");
                    // Removes the progress bar
                    mBuilder.setProgress(0, 0, false);
                    mNotifyManager.notify(id, mBuilder.build());
                    Toast.makeText(context,"Successfully Uploaded",Toast.LENGTH_SHORT).show();
                    System.out.println("Received Response : "+strResponse);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mNotifyManager.cancel(id);
                        }
                    }, 4000);

                    transaction.setIs_synced(true);
                    TransactionAdapter.setSynced(transaction,context);



                }
            });
        }else if(strResponse.contains("unable to connect")){

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    //Failed
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(context, SyncTransactions.class);

                    // The stack builder object will contain an artificial back stack for
                    // the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(MainActivity.class);
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    mBuilder.setContentTitle("Failed to Upload Transaction")
                            .setContentText("Click to retry");
                    mBuilder.setProgress(0, 0, false);
                    mNotifyManager.notify(id, mBuilder.build());
                    Toast.makeText(context, "No internet Access or unable to connect!", Toast.LENGTH_SHORT).show();
                    System.out.println("No internet Access or unable to connect: " + strResponse);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mNotifyManager.cancel(id);
                        }
                    }, 15000);

                }
            });



        }else{

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    //Failed
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(context, SyncTransactions.class);

                    // The stack builder object will contain an artificial back stack for
                    // the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(MainActivity.class);
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    mBuilder.setContentTitle("Failed to Upload Transaction")
                            .setContentText("Click to retry");
                    mBuilder.setProgress(0, 0, false);
                    mNotifyManager.notify(id, mBuilder.build());
                    System.out.println("Unknown Server Response : " + strResponse);
                    Toast.makeText(context, "Unknown Server Response", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mNotifyManager.cancel(id);
                        }
                    }, 15000);
                     }
                });

        }


        if(response!=null) {
            if (synced) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        response.onSuccess();
                    }
                });

            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        response.onFailed();
                    }
                });
            }
        }


        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {

        // Update progress
        mBuilder.setProgress(100, values[0], false);
        mNotifyManager.notify(id, mBuilder.build());
        super.onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    private String attemptLoginVehicle(String ip,String shiftID, String username, String terminalID, String receiptNo, String precinctID,
                                       String bayNumber ,String requestDateTime, String vehicleReg, String duration, String in_date_time,boolean isPaid,String paymentRefNo){
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
            result = "errormsg : unable to connect to server";
        }

        // 11. return result
        return result;


    }



}
