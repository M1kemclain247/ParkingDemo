package com.example.m1kes.parkingdemo.tasks;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.base.BaseUpdater;
import com.example.m1kes.parkingdemo.base.DBUpdater;
import com.example.m1kes.parkingdemo.callbacks.UpdateResourceResponse;
import com.example.m1kes.parkingdemo.settings.developer.DeveloperSettings;
import com.example.m1kes.parkingdemo.settings.manager.ApiManager;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;


public class UpdateTask extends AsyncTask<String, Void, Boolean> {

    private Context context;
    private Handler mHandler;
    private UpdateResourceResponse resourceResponse;
    private BaseUpdater updater;


    public UpdateTask(Context context,UpdateResourceResponse resourceResponse) {
        this.context = context;
        mHandler = new Handler();
        updater = new BaseUpdater(context);
        this.resourceResponse = resourceResponse;
    }



    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPreExecute() {

    }


    private Boolean updateResources(Context context,String connectionAddress,int numAttempts) throws InterruptedException, ExecutionException, TimeoutException {

       //update status
        Boolean successfull = false;
        String localIP;
        String hostedIp;

        if(DeveloperSettings.isDeveloperModeEnabled(context)){
            localIP = context.getResources().getString(R.string.dev_localIP);
            hostedIp = context.getResources().getString(R.string.dev_hostedIP);
        }else{
            localIP = context.getResources().getString(R.string.localIP);
            hostedIp = context.getResources().getString(R.string.hostedIP);
        }




        String users ="";
        String bays = "";
        String sites = "";
        String tarriffs = "";
        String zones = "";
        String precincts = "";
        String printers = "";
        String systemSettings = "";
        String paymentModes = "";
        String transactionTypes = "";

        /*
        if(connectionAddress!=null){
            if(connectionAddress.equalsIgnoreCase("Local Server")){
                users += localIP;
                bays += localIP;
                sites += localIP;
                tarriffs += localIP;
                zones += localIP;
                precincts += localIP;
                printers += localIP;
                systemSettings += localIP;
                paymentModes += localIP;
                transactionTypes += localIP;

                //Get all of the Api's for online resources
                users += context.getResources().getString(R.string.users_uri);
                bays += context.getResources().getString(R.string.api_bays);
                sites += context.getResources().getString(R.string.api_site);
                tarriffs += context.getResources().getString(R.string.tarriffs_uri);
                zones += context.getResources().getString(R.string.zones_uri);
                precincts += context.getResources().getString(R.string.precincts_uri);
                printers += context.getResources().getString(R.string.api_printers);
                systemSettings += context.getResources().getString(R.string.api_system_settings);
                paymentModes +=context.getResources().getString(R.string.payment_modes_uri);
                transactionTypes += context.getResources().getString(R.string.transaction_types_uri);

            }else if(connectionAddress.equalsIgnoreCase("Remote Server")){
                users += remoteIP;
                bays += remoteIP;
                sites += remoteIP;
                tarriffs += remoteIP;
                zones += remoteIP;
                precincts += remoteIP;
                printers += remoteIP;
                systemSettings += remoteIP;
                paymentModes += remoteIP;
                transactionTypes += remoteIP;

                //Get all of the Api's for online resources
                users += context.getResources().getString(R.string.api_users);
                bays += context.getResources().getString(R.string.api_bays);
                sites += context.getResources().getString(R.string.api_site);
                tarriffs += context.getResources().getString(R.string.api_tarriffs);
                zones += context.getResources().getString(R.string.api_zones);
                precincts += context.getResources().getString(R.string.api_precincts);
                printers += context.getResources().getString(R.string.api_printers);
                systemSettings += context.getResources().getString(R.string.api_system_settings);
                paymentModes +=context.getResources().getString(R.string.payment_modes_uri);
                transactionTypes += context.getResources().getString(R.string.transaction_types_uri);

            } else if(connectionAddress.equalsIgnoreCase("Hosted Server")){
                users += hostedIp;
                bays += hostedIp;
                sites += hostedIp;
                tarriffs += hostedIp;
                zones += hostedIp;
                precincts += hostedIp;
                printers += hostedIp;
                systemSettings += hostedIp;
                paymentModes += hostedIp;

                transactionTypes += hostedIp;

                //Get all of the Api's for online resources
                users += context.getResources().getString(R.string.users_uri);
                //bays += context.getResources().getString(R.string.bays_uri);
               // sites += context.getResources().getString(R.string.site_uri);
                tarriffs += context.getResources().getString(R.string.tarriffs_uri);
                zones += context.getResources().getString(R.string.zones_uri);
                precincts += context.getResources().getString(R.string.precincts_uri);
                //printers += context.getResources().getString(R.string.printers_uri);
                systemSettings += context.getResources().getString(R.string.system_settings_uri);
                paymentModes +=context.getResources().getString(R.string.payment_modes_uri);
                transactionTypes += context.getResources().getString(R.string.transaction_types_uri);
            }

        }
        */

        if(connectionAddress!=null){
            if(numAttempts==1){
                System.out.println("Using Ip: "+localIP);
                users += localIP;
                bays += localIP;
                sites += localIP;
                tarriffs += localIP;
                zones += localIP;
                precincts += localIP;
                printers += localIP;
                systemSettings += localIP;
                paymentModes += localIP;
                transactionTypes += localIP;

                //Get all of the Api's for online resources
                users += context.getResources().getString(R.string.users_uri);
                bays += context.getResources().getString(R.string.api_bays);
                sites += context.getResources().getString(R.string.api_site);
                tarriffs += context.getResources().getString(R.string.tarriffs_uri);
                zones += context.getResources().getString(R.string.zones_uri);
                precincts += context.getResources().getString(R.string.precincts_uri);
                printers += context.getResources().getString(R.string.api_printers);
                systemSettings += context.getResources().getString(R.string.api_system_settings);
                paymentModes +=context.getResources().getString(R.string.payment_modes_uri);
                transactionTypes += context.getResources().getString(R.string.transaction_types_uri);

            } else if(numAttempts==2){
                System.out.println("Using Ip: "+hostedIp);
                users += hostedIp;
                bays += hostedIp;
                sites += hostedIp;
                tarriffs += hostedIp;
                zones += hostedIp;
                precincts += hostedIp;
                printers += hostedIp;
                systemSettings += hostedIp;
                paymentModes += hostedIp;

                transactionTypes += hostedIp;

                //Get all of the Api's for online resources
                users += context.getResources().getString(R.string.users_uri);
                //bays += context.getResources().getString(R.string.bays_uri);
                // sites += context.getResources().getString(R.string.site_uri);
                tarriffs += context.getResources().getString(R.string.tarriffs_uri);
                zones += context.getResources().getString(R.string.zones_uri);
                precincts += context.getResources().getString(R.string.precincts_uri);
                //printers += context.getResources().getString(R.string.printers_uri);
                systemSettings += context.getResources().getString(R.string.system_settings_uri);
                paymentModes +=context.getResources().getString(R.string.payment_modes_uri);
                transactionTypes += context.getResources().getString(R.string.transaction_types_uri);
            }



        }


        String [] apis = new String[]{users,zones,tarriffs,precincts,transactionTypes,paymentModes};



            updater.getUpdates(apis,context);

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    resourceResponse.onProgressUpdated("Updating Resources....");
                }
            });

            DBUpdater.replaceItems(updater.getOnlineUsers(users),context);
            DBUpdater.replaceItems(updater.getOnlineZones(zones),context);
            DBUpdater.replaceItems(updater.getOnlineTarriffs(tarriffs),context);
            DBUpdater.replaceItems(updater.getOnlinePrecincts(precincts),context);
            DBUpdater.replaceItems(updater.getOnlineTransactionTypes(transactionTypes),context);
            DBUpdater.replaceItems(updater.getOnlinePaymentModes(paymentModes),context);

            successfull = true;


            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    resourceResponse.onProgressUpdated("Resources Updated!");
                }
            });

            ApiManager.setServerIp(connectionAddress,context);

        return successfull;
    }


    private void sleep(long sleepTime){

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected Boolean doInBackground(String... params) {
        //my stuff is here
        Boolean isUpdated = false;

            if(isNetworkAvailable(context)){
                //Connection is Available

                try {

                    for (int i = 1; i <= 2; i++) {
                        try {

                            if (updateResources(context, params[0], i)) {
                                isUpdated = true;
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        resourceResponse.updateComplete();
                                    }
                                });
                            } else {
                                //Error Occured while trying to update
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        resourceResponse.updateFailed();
                                    }
                                });
                            }

                            if (isUpdated)
                                break;

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                            if(i==2)
                                throw new Exception();
                        }catch (Exception e){
                            e.printStackTrace();
                            if(i==2)
                                throw new Exception();
                        }

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            resourceResponse.updateFailed();
                        }
                    });
                }



            }else{
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        resourceResponse.onNoConnection();
                    }
                });
            }
        return isUpdated;
    }


}
