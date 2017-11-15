package com.example.m1kes.parkingdemo.firsttimeterminalreg;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.SplashScreen;
import com.example.m1kes.parkingdemo.settings.manager.AppSettingsManager;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import static com.example.m1kes.parkingdemo.networking.Converter.convertInputStreamToString;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivateTerminal extends Fragment implements View.OnClickListener {


    EditText txtAccessCode;
    Button btnGo;
    Handler h;
    String rsponce;
    ProgressDialog progressDialog;
    OnTerminalRegResponse termi2;

    public ActivateTerminal() {
        // Required empty public constructor
        h = new Handler();
    }


    {


        termi2 = new OnTerminalRegResponse() {
            @Override
            public void onDone() {

                Toast.makeText(getActivity(), "Activation Successful", Toast.LENGTH_SHORT).show();
                PrefManager prefManager = new PrefManager(getContext());
                prefManager.setFirstTimeLaunch(false);
                startActivity(new Intent(getActivity(),SplashScreen.class));
                getActivity().finish();
            }

            @Override
            public void onUndone() {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Activation Error");
                builder.setMessage("Incorrect Activation Code, Please Retry");
                builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                Dialog d = builder.create();
                d.show();




            }

            public void unableToConnect(){
                Toast.makeText(getActivity(), "Unable to connect to Server", Toast.LENGTH_SHORT).show();

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Network Error");
                builder.setMessage("Network Unavailable Please Try Again Later.");
                builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String accessCode = txtAccessCode.getText().toString();
                        new ActivateTerminalTask(accessCode,termi2).execute();
                    }
                });
                Dialog d = builder.create();
                d.show();



            }
        };
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activate_terminal, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        txtAccessCode = (EditText) getActivity().findViewById(R.id.editTextActivationCode);
        btnGo =(Button) getActivity().findViewById(R.id.btnActivateTerminal);
        btnGo.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnActivateTerminal:{


                String accessCode = txtAccessCode.getText().toString();
                if(accessCode.equalsIgnoreCase("")||accessCode.equalsIgnoreCase(" ")){
                    txtAccessCode.setError("Please Enter The Access Code First");
                }else {
                    Toast.makeText(getActivity(),"Activating....",Toast.LENGTH_SHORT).show();
                    new ActivateTerminalTask(accessCode,termi2).execute();
                }
            }
        }
    }


    public class ActivateTerminalTask extends AsyncTask<Void,Void,Void>{

        String accessCode;
        OnTerminalRegResponse terminalRegResponse;
        private final ProgressDialog dialog = new ProgressDialog(getActivity());

        public ActivateTerminalTask(String accessCode,OnTerminalRegResponse terminalRegResponse) {
            this.accessCode = accessCode;
            this.terminalRegResponse = terminalRegResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Activating...");
            this.dialog.show();
            this.dialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            boolean isSuccessFul = false;
            int[] ipAddressOfStart ={R.string.hostedIP,R.string.localIP};

            for(int i = 0 ;i<=1;i++){


                System.out.println("trying this IP: "+getActivity().getResources().getString(ipAddressOfStart[i]));

                rsponce = sendActivation(accessCode,ipAddressOfStart[i],getActivity());
                System.out.println("Responce is >"+rsponce);

                if(rsponce.contains("errormessage")) {

                    isSuccessFul = true;
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            terminalRegResponse.onUndone();
                        }
                    });
                    break;
                }else if(rsponce.contains("Success")){

                    try {
                        JSONObject jsonObject = new JSONObject(rsponce);
                        int terminalID  = jsonObject.getInt("Id");
                        //TODO CHECK IF THIS WORKS
                        ShiftDataManager.setDefaultTerminalID(terminalID,getContext());
                        AppSettingsManager.setActivationStatus(getContext(),1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    isSuccessFul = true;
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            terminalRegResponse.onDone();
                        }
                    });
                    break;
                }


            }

            if(!isSuccessFul){
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        terminalRegResponse.unableToConnect();
                    }
                });
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
        }
    }




























    public String getIMEI(Context context){

        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        return imei;

    }


    public String sendActivation(String accessCode,int ipAddInt ,Context context){

        String imei = getIMEI(getActivity());
        String username = "darell";
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
            String ipAddress = context.getResources().getString(ipAddInt);
            System.out.println("Using SERVER IP Address: "+ipAddress);
            url += ipAddress;
            url += context.getResources().getString(R.string.api_terminal_registering_pt2);

            System.out.println("Request URL: "+url);
            // 2. make POST request to the given URL

            url +=""+username+"/";
            url +=""+imei+"/";
            url +=""+accessCode+"/";

            System.out.println("Making a Vehicle LOGIN request using GET:  " + "/"+username+"/"+imei+"/"+accessCode);

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
        }

        // 11. return result
        return result;

    }

}
