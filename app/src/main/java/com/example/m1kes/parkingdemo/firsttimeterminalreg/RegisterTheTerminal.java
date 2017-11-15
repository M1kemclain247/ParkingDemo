package com.example.m1kes.parkingdemo.firsttimeterminalreg;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.InputStream;

import static com.example.m1kes.parkingdemo.networking.Converter.convertInputStreamToString;
import static com.example.m1kes.parkingdemo.settings.developer.DeveloperSettings.setDeveloperModeEnabled;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterTheTerminal extends Fragment implements View.OnClickListener{

   // ProgressBar bar;

    private Button btnRetry;
    ImageView img;
    TextView txtR;
    String rsponce;
    OnTerminalRegResponse termi;
    Handler h;
    ImageButton retryload;


    public RegisterTheTerminal() {
        // Required empty public constructor
       h = new Handler();
    }




    {

        termi = new OnTerminalRegResponse() {
            @Override
            public void onDone() {

                Toast.makeText(getActivity(), "Register Successful", Toast.LENGTH_SHORT).show();


                img.setVisibility(View.VISIBLE);
                // txtR.setVisibility(View.VISIBLE);
                //    bar.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
                txtR.setText("Device Has Been Successfully Registered");
                retryload.setVisibility(View.GONE);

                btnRetry = (Button) getActivity().findViewById(R.id.btn_retry);
                //   btnRetry.setVisibility(View.GONE);
                WelcomeActivity terminalRegistration = new WelcomeActivity();

                terminalRegistration.changeVisibility(btnRetry, "");
            }

            @Override
            public void onUndone() {
                Toast.makeText(getActivity(), "Device is already Registered", Toast.LENGTH_SHORT).show();
                img.setVisibility(View.VISIBLE);
                // txtR.setVisibility(View.VISIBLE);
                //    bar.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
                txtR.setText("Device is Already Registered");
                retryload.setVisibility(View.GONE);

                btnRetry = (Button) getActivity().findViewById(R.id.btn_retry);
                //   btnRetry.setVisibility(View.GONE);
                WelcomeActivity terminalRegistration = new WelcomeActivity();
                terminalRegistration.changeVisibility(btnRetry, "");
            }

            public void unableToConnect(){
                Toast.makeText(getActivity(), "Unable to connect to Server", Toast.LENGTH_SHORT).show();


                retryload.setVisibility(View.VISIBLE);

                txtR.setText("Unable to connect to Server: Press Icon To Retry");
                retryload.setOnClickListener(RegisterTheTerminal.this);




            }
        };
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register_the_terminal, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        retryload = (ImageButton) getActivity().findViewById(R.id.progressBar);

        img = (ImageView) getActivity().findViewById(R.id.imageDone);
        txtR = (TextView) getActivity().findViewById(R.id.txtDoneReg);
        img.setVisibility(View.GONE);
       // retryload.setVisibility(View.GONE);
       // txtR.setVisibility(View.GONE);

        //TODO Check if this works
        //Sets Developer Mode enabled
        setDeveloperModeEnabled(false,getActivity());

        try {


            System.out.println("Before Task in RegisterTheTerminal OnAcitity Created");

            RegisteringTask tr = new RegisteringTask(termi);
            tr.execute();

            System.out.println("After Task in RegisterTheTerminal OnAcitity Created");


        }catch (Exception e){

            System.out.println("Error occured at Regiseter task call");
        }








    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.progressBar:{
               // Toast.makeText(getActivity(),"Retrying",Toast.LENGTH_SHORT).show();
                new RegisteringTask(termi).execute();
            }
        }
    }


    public class RegisteringTask extends AsyncTask<Void,Void,Void> {

        OnTerminalRegResponse terminalRegResponse;
        private final ProgressDialog dialog = new ProgressDialog(getActivity());
        public RegisteringTask(OnTerminalRegResponse terminalRegResponse) {
            this.terminalRegResponse = terminalRegResponse;
        }

        protected void onPreExecute(){
            //bar.setVisibility(View.VISIBLE);
            this.dialog.setMessage("Registering Device Please Wait...");
            this.dialog.show();
            this.dialog.setCancelable(false);
        }

        @Override
        protected Void doInBackground(Void... arg0) {

                //TODO Get Terminal IME
                String imei = getIMEI(getActivity());
                //TODO Get Current UserName

                //TODO Get Device Model

                //TODO ADD IS_REGISTERED AND IS_ACTIVE

                String modell = getDeviceName();
                //TODO Send Everything to DB & Server

              //  TODO Check Connection First

            boolean isSuccessFul = false;
                int[] ipAddressOfStart ={R.string.hostedIP,R.string.localIP};

                    for(int i = 0 ;i<=1;i++){


                        System.out.println("trying this IP: "+getActivity().getResources().getString(ipAddressOfStart[i]));

                        rsponce = registerThisTerminal(imei,modell,ipAddressOfStart[i],getActivity());
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



                //TODO The isActive becomes 1 and Get Success Message

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
        }
    }



    public String registerThisTerminal(String terminalIMEI, String deviceModel,int ipAddInt, Context context){

        String username = "darell";
        InputStream inputStream = null;
        String result = "";
        try {

            //Set the timeout to 6 Seconds
            final HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
            HttpConnectionParams.setSoTimeout(httpParams, 6000);

            HttpClient httpclient = new DefaultHttpClient(httpParams);

            String url = "";


            String ipAddress = context.getResources().getString(ipAddInt);
            System.out.println("Using SERVER IP Address: "+ipAddress);
            url += ipAddress;
            url += context.getResources().getString(R.string.api_terminal_registering);

            System.out.println("Request URL: "+url);
            // 2. make POST request to the given URL

            url +=""+username+"/";
            url +=""+terminalIMEI+"/";
            url +=""+deviceModel+"/";

            System.out.println("Making a Vehicle LOGIN request using GET:  " + "/"+username+"/"+terminalIMEI+"/"+deviceModel);

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
            result = "error : message no internet connect";
        }

        // 11. return result
        return result;
    }


    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + model;
        }
    }
    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public String getIMEI(Context context){

        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        String imei = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            imei = mngr.getDeviceId(0);
        }else{
            imei = mngr.getDeviceId();
        }
        return imei;

    }


}
