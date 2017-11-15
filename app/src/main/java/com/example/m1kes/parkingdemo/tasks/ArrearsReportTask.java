package com.example.m1kes.parkingdemo.tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.callbacks.ArrearsResponse;
import com.example.m1kes.parkingdemo.printer.models.Arrear;
import com.example.m1kes.parkingdemo.printer.models.ArrearsReport;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static com.example.m1kes.parkingdemo.networking.Converter.convertInputStreamToString;
import static com.example.m1kes.parkingdemo.settings.manager.ApiManager.getAvailableIps;

public class ArrearsReportTask  extends AsyncTask<String,Void,Void>{

    private Context context;
    private Handler mHandler;
    private ArrearsResponse response;

    public ArrearsReportTask(Context context,ArrearsResponse response){
        this.context = context;
        this.response = response;
        mHandler = new Handler();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(final String... params) {

        final String username = UserAdapter.getLoggedInUser(context);
        String shiftID = ShiftDataManager.getCurrentActiveShiftID(context);
        String terminalID = String.valueOf(ShiftDataManager.getDefaultTerminalID(context));
        long requestDateTime =  Long.parseLong(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        final String vehicleReg = params[0];




        List<String> failedRequests = new ArrayList<>();
        List<String> succesfullResponse = new ArrayList<>();

        String[] availableApis = getAvailableIps(context);
        boolean worked = false;
        for(int i = 0 ;i<availableApis.length;i++) {

            System.out.println("Attempt: "+(i+1) +"/"+availableApis.length);

            final String tempResponse = getVehicleArrearsReport(availableApis[i],shiftID,username,terminalID,requestDateTime,vehicleReg);

            if (tempResponse.contains("balance")) {
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





        if(strResponse.contains("balance")){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Should have worked the response was: "+strResponse);
                    //Sends a receipt to be printer
                    ArrearsParser parser = new ArrearsParser(strResponse);
                    Receipt receipt = new ArrearsReport(parser.getArrearList(),vehicleReg,username);
                    response.getArrearsSuccessfull(receipt);
                }
            });

        }else{
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("Did not work with ERROR Response: "+strResponse);
                    //sends the error response to be displayed
                    response.getArrearsFailed(strResponse);
                }
            });
        }





        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }



    private String getVehicleArrearsReport(String ip,String shiftID, String username, String terminalID,long requestDateTime,
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
           // HttpClient httpclient = new DefaultHttpClient();
            String url = "";
            //String ipAddress = ApiManager.getServerIP(context);
            //System.out.println("Using SERVER IP Address: "+ipAddress);
            // url += ipAddress;
            System.out.println("Using SERVER IP address: "+ip);
            url += ip;
            url += context.getResources().getString(R.string.api_vehicle_arrears);
            // 2. make POST request to the given URL
            url += ""+shiftID+"/";
            url +=""+username+"/";
            url +=""+terminalID+"/";
            url +=""+requestDateTime+"/";
            url +=""+vehicleReg+"/";

            System.out.println("Search Vehicle request using GET: "+shiftID +"/"+username+"/"+terminalID+"/"+requestDateTime+"/"+vehicleReg+"/");
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


    public class ArrearsParser {


        private List<Arrear> arrearList;

        public ArrearsParser(String rawResponse){
            List<Arrear> arrearsList = new ArrayList<>();

            try {
                JSONArray array = new JSONArray(rawResponse);

                for(int i = 0;i<array.length();i++){

                    JSONObject jsonObject = array.getJSONObject(i);
                    String vehicleReg = jsonObject.getString("VehicleReg");
                    String Zone = jsonObject.getString("Zone");
                    String precinctName = jsonObject.getString("PrecinctName");
                    String marshall = jsonObject.getString("marshall");
                    String receiptNo = jsonObject.getString("ReceiptNo");
                    String in_datetime = jsonObject.getString("in_datetime");
                    int duration = jsonObject.getInt("Duration");
                    double amount = jsonObject.getDouble("Amount");
                    double tarrifAmount = jsonObject.getDouble("TarriffAmount");

                    Arrear arrear = new Arrear();
                    arrear.setVehicleReg(vehicleReg);
                    arrear.setZoneName(Zone);
                    arrear.setPrecinctName(precinctName);
                    arrear.setMarshall(marshall);
                    arrear.setReceiptNo(receiptNo);
                    arrear.setIn_datetime(in_datetime);
                    arrear.setDuration(duration);
                    arrear.setAmount(amount);
                    arrear.setTarrif(tarrifAmount);

                    //Format the in_Date time and out_datetime

                    arrearsList.add(arrear);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                System.out.println("Error while parsing Server Response for Mini Statement: "+e.toString());
                this.arrearList = arrearsList;
            }
            this.arrearList = arrearsList;
        }

        public List<Arrear> getArrearList() {
            return arrearList;
        }
    }





}
