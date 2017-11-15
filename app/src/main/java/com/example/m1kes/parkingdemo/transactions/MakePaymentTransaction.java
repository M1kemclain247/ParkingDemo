package com.example.m1kes.parkingdemo.transactions;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.modules.supervisor.util.TransactionDumper;
import com.example.m1kes.parkingdemo.settings.developer.DeveloperSettings;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.transactions.callbacks.OnMakePaymentResponse;

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


public class MakePaymentTransaction extends AsyncTask<Transaction,Void,Boolean> {

    private Context context;
    private Handler mHandler;
    private OnMakePaymentResponse response;
    private boolean success = false;


    public MakePaymentTransaction(Context context){
        this.context  = context;
        mHandler = new Handler();
    }

    public MakePaymentTransaction(Context context,OnMakePaymentResponse response){
        this.context  = context;
        this.response = response;
        mHandler = new Handler();
    }



    @Override
    protected Boolean doInBackground(Transaction... params) {

        final Transaction transaction =  params[0];

        System.out.println("This is the transaction about to be sent: "+transaction);



        List<String> failedRequests = new ArrayList<>();
        List<String> succesfullResponse = new ArrayList<>();

        String[] availableApis = getAvailableIps(context);
        boolean worked = false;
        for(int i = 0 ;i<availableApis.length;i++) {

            System.out.println("Attempt: "+(i+1) +"/"+availableApis.length);

            final String tempResponse =  attemptLoginVehicle(availableApis[i],transaction.getShiftId(),transaction.getUsername(),String.valueOf(transaction.getTerminalId()), transaction.getPrecinctID(),
                    transaction.getRecieptNumber(),transaction.getVehicleregNumber(),String.valueOf(transaction.getPayment_mode_id()),String.valueOf(transaction.getAmountPaid()),
                    String.valueOf(transaction.getTransactionDateTime()),String.valueOf(transaction.getRequestDateTime()),transaction.getFineRefNo());
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


        if(strResponse.contains("errormsg")||strResponse.contains("HTTP")||strResponse.contains("Tunnel")||
                strResponse.contains("incorrect")){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Failed
               System.out.println("Failed with response: "+strResponse);
                    Toast.makeText(context, "Failed to Upload Transaction", Toast.LENGTH_SHORT).show();

                }
            });
        }else if(strResponse.contains("Success")||strResponse.contains("already processed")){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //Successful
                    success = true;
                    System.out.println("Successful with response: "+strResponse);
                    Toast.makeText(context, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                    transaction.setIs_synced(true);
                    TransactionAdapter.setSynced(transaction,context);

                }
            });
        }else if(strResponse.contains("unable to connect")) {

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    //Failed
                    System.out.println("Failed with No Connection: "+strResponse);
                    Toast.makeText(context, "No internet Access or unable to connect", Toast.LENGTH_SHORT).show();
                }
            });

        }


        if(response!=null){
            if(success){
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        response.onSuccess();
                    }
                });
            }else{
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


    private String attemptLoginVehicle(String ip,String shiftID, String username, String terminalID,String precinctID, String receiptNo,
                                       String vehicleReg, String payment_mode_id,String amount,String paymentDateTime,String requestDateTime,String fineRefNo){
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
            result = "errormsg : unable to connect to server";
        }

        // 11. return result
        return result;


    }


}
