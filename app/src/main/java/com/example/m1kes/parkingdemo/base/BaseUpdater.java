package com.example.m1kes.parkingdemo.base;


import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.m1kes.parkingdemo.models.Bay;
import com.example.m1kes.parkingdemo.models.PaymentMode;
import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.models.Roles;
import com.example.m1kes.parkingdemo.models.Site;
import com.example.m1kes.parkingdemo.models.SystemSettings;
import com.example.m1kes.parkingdemo.models.Tarriff;
import com.example.m1kes.parkingdemo.models.TransactionType;
import com.example.m1kes.parkingdemo.models.User;
import com.example.m1kes.parkingdemo.models.Zone;
import com.example.m1kes.parkingdemo.util.DBUtils;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static com.example.m1kes.parkingdemo.util.DBUtils.*;

public class BaseUpdater {

    public final static int FAILED_UPDATING = 1010;
    private Context context;
    private Map<String,JSONArray> downloadedContent;
    private List<User> onlineUsers;
    private List<Zone> onlineZones;
    private List<Bay> onlineBays;
    private List<Site> onlineSites;
    private List<Tarriff> onlineTarriffs;
    private List<Precinct> onlinePrecincts;
    private List<Printer> onlinePrinters;
    private List<SystemSettings> onlineSystemSettings;
    private List<PaymentMode> onlinePaymentModes;
    private List<TransactionType> onlineTransactionTypes;

    public BaseUpdater(Context context){
        this.context = context;
        downloadedContent = new HashMap<>();
        onlineUsers = new ArrayList<>();
        onlineZones = new ArrayList<>();
        onlineBays = new ArrayList<>();
        onlineSites = new ArrayList<>();
        onlineTarriffs = new ArrayList<>();
        onlinePrecincts = new ArrayList<>();
        onlinePrinters = new ArrayList<>();
        onlineSystemSettings = new ArrayList<>();
        onlinePaymentModes = new ArrayList<>();
        onlineTransactionTypes = new ArrayList<>();

    }

    private static JSONArray doJsonArrayUpdate(Context context,String url) throws InterruptedException, ExecutionException, TimeoutException {
        RequestQueue queue = Volley.newRequestQueue(context);
        RequestFuture<JSONArray> future = RequestFuture.newFuture();
        JsonArrayRequest request = new JsonArrayRequest(url,future,future);
        queue.add(request);

        return future.get(2, TimeUnit.SECONDS);// Blocks for at most 10 seconds.
    }



    public Map<String,JSONArray> getUpdates(String[]apiKeys,Context context) throws InterruptedException, ExecutionException, TimeoutException {

        Map<String,JSONArray> downloadedApis = new HashMap<>();

        for(int i = 0;i<apiKeys.length;i++){
                downloadedApis.put(apiKeys[i], BaseUpdater.doJsonArrayUpdate(context,apiKeys[i]));
        }

        this.downloadedContent = downloadedApis;
        return downloadedApis;
    }


    public Map<String, JSONArray> getDownloadedContent() {
        return downloadedContent;
    }

    public List<User> getOnlineUsers(String key) {
        return BaseUpdater.JsonParser.getUsers(key,getDownloadedContent());
    }

    public List<Zone> getOnlineZones(String key) {
        return BaseUpdater.JsonParser.getZones(key,getDownloadedContent());
    }

    public List<Bay> getOnlineBays(String key) {
        return BaseUpdater.JsonParser.getBays(key,getDownloadedContent());
    }

    public List<Site> getOnlineSites(String key) {
        return BaseUpdater.JsonParser.getSites(key,getDownloadedContent());
    }

    public List<Tarriff> getOnlineTarriffs(String key) {
        return BaseUpdater.JsonParser.getTarriffs(key,getDownloadedContent());
    }

    public List<Precinct> getOnlinePrecincts(String key) {
        return BaseUpdater.JsonParser.getPrecincts(key,getDownloadedContent());
    }

    public List<Printer> getOnlinePrinters(String key) {
        return BaseUpdater.JsonParser.getPrinters(key,getDownloadedContent());
    }

    public List<SystemSettings> getOnlineSystemSettings(String key) {
        return BaseUpdater.JsonParser.getSystemSettings(key,getDownloadedContent());
    }

    public List<PaymentMode> getOnlinePaymentModes(String key) {
        return BaseUpdater.JsonParser.getPaymentModes(key,getDownloadedContent());
    }

    public List<TransactionType> getOnlineTransactionTypes(String key) {
        return BaseUpdater.JsonParser.getTransactionTypes(key,getDownloadedContent());
    }


    //Inner Static class For Parsing all Json values to List models
    private static class JsonParser{


        private static List<User> getUsers(String keyUsers, Map<String,JSONArray> downloadedContent){

            List<User> onlineUsers = new ArrayList<>();
            JSONArray jsonArrayUsers = downloadedContent.get(keyUsers);

            try {

                System.out.println("Looking at the Json Object: " + jsonArrayUsers.toString());

                for (int i = 0; i < jsonArrayUsers.length(); i++) {
                    JSONObject jresponse = null;
                    try {

                        System.out.println("Parsing User Object from server");
                        jresponse = jsonArrayUsers.getJSONObject(i);
                        String id = jresponse.getString("Id");
                        String username = jresponse.getString("UserName");
                        String password = jresponse.getString("Passwd");
                        int empNo = jresponse.getInt("EmployeeNumber");
                        String firstname = jresponse.getString("Firstname");
                        String surname = jresponse.getString("Surname");

                        //TODO ADD ROLES
                        List<String> rawRoles = new ArrayList<>();
                        JSONArray rolesArray = jresponse.getJSONArray("roles");

                        if (rolesArray != null) {
                            int len = rolesArray.length();
                            for (int j = 0; j < len; j++) {
                                String role = (String) rolesArray.get(j);
                                rawRoles.add(role);
                            }
                        }

                        System.out.println("Got a list of roles for this user: " + rawRoles);

                        //Download list of users and cache incase needed.
                        onlineUsers.add(new User(id, username, password, empNo, firstname, surname, rawRoles));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }catch(NullPointerException e){
                e.printStackTrace();
            }
            System.out.println("All users about to be added to DB : "+onlineUsers);
            return onlineUsers;
        }

        private static List<TransactionType> getTransactionTypes(String keyTransactionTypes, Map<String,JSONArray> downloadedContent){

            List<TransactionType> onlineTransactionTypes = new ArrayList<>();
            JSONArray jsonArrayUsers = downloadedContent.get(keyTransactionTypes);

            try{

            for(int i = 0; i < jsonArrayUsers.length(); i++){
                JSONObject jresponse = null;
                try {
                    jresponse = jsonArrayUsers.getJSONObject(i);
                    int id =  jresponse.getInt("id");
                    String name = jresponse.getString("Name");
                    //Download list of users and cache incase needed.
                    onlineTransactionTypes.add(new TransactionType(id,name));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }catch(NullPointerException e){
            e.printStackTrace();
        }

            return onlineTransactionTypes;
        }


        private static List<PaymentMode> getPaymentModes(String keyPaymentModes, Map<String,JSONArray> downloadedContent){

            List<PaymentMode> paymentModes = new ArrayList<>();
            JSONArray jsonArrayUsers = downloadedContent.get(keyPaymentModes);

            try {
                for (int i = 0; i < jsonArrayUsers.length(); i++) {
                    JSONObject jresponse = null;
                    try {
                        jresponse = jsonArrayUsers.getJSONObject(i);
                        int id = jresponse.getInt("id");
                        String name = jresponse.getString("Name");
                        //Download list of users and cache incase needed.
                        paymentModes.add(new PaymentMode(id, name));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }catch(NullPointerException e){
                e.printStackTrace();
            }

            return paymentModes;
        }


        private static List<Printer> getPrinters(String keyPrinters, Map<String,JSONArray> downloadedContent){

            List<Printer> onlinePrinters = new ArrayList<>();
            JSONArray jsonArrayUsers = downloadedContent.get(keyPrinters);

            try{
            for(int i = 0; i < jsonArrayUsers.length(); i++){
                JSONObject jresponse = null;
                try {
                    jresponse = jsonArrayUsers.getJSONObject(i);

                    int id = jresponse.getInt("Id");
                    String printerCode = jresponse.getString("PrinterCode");
                    String printerModel = jresponse.getString("PrinterModel");
                    String printerIMEI = jresponse.getString("PrinterIMEI");
                    //Download list of users and cache incase needed.
                    onlinePrinters.add(new Printer(id,printerCode,printerIMEI,printerModel));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            }catch(NullPointerException e){
                e.printStackTrace();
            }

            return onlinePrinters;
        }


        private static List<SystemSettings> getSystemSettings(String keySystemSettings, Map<String,JSONArray> downloadedContent){

            String dateTimeParseFormat = "yyyy-MM-dd";
            List<SystemSettings> onlineSystemSettings = new ArrayList<>();
            JSONArray jsonArrayUsers = downloadedContent.get(keySystemSettings);

            try{
            for(int i = 0; i < jsonArrayUsers.length(); i++){
                JSONObject jresponse = null;
                try {
                    jresponse = jsonArrayUsers.getJSONObject(i);

                    int id = jresponse.getInt("id");
                    String version = jresponse.getString("Version");
                    Date versionDate = parseDate(jresponse.getString("VersionDate"),dateTimeParseFormat);
                    String serverName = jresponse.getString("ServerName");
                    String serverIp = jresponse.getString("ServerIP");
                    String localAddress = jresponse.getString("LocalAddress");
                    String deviceServiceAddress = jresponse.getString("DeviceServiceAddress");

                    //Download list of users and cache incase needed.
                    onlineSystemSettings.add(new SystemSettings(id,version,versionDate,serverName,serverIp,localAddress,deviceServiceAddress));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            }catch(NullPointerException e){
                e.printStackTrace();
            }

            return onlineSystemSettings;
        }



        private static List<Precinct> getPrecincts(String keyPrecincts, Map<String,JSONArray> downloadedContent){

            List<Precinct> onlinePrecincts = new ArrayList<>();
            JSONArray jsonArrayUsers = downloadedContent.get(keyPrecincts);

            try{
            for(int i = 0; i < jsonArrayUsers.length(); i++){
                JSONObject jresponse = null;
                try {
                    jresponse = jsonArrayUsers.getJSONObject(i);

                    int id = jresponse.getInt("id");
                    String name = jresponse.getString("Name");
                    String zonename = jresponse.getString("Zonename");
                    double target = jresponse.getDouble("Target");
                    String lunch = jresponse.getString("Lunch");
                    double amount = jresponse.getDouble("Amount");

                    //Download list of users and cache incase needed.
                    onlinePrecincts.add(new Precinct(id,name,zonename,target,lunch,amount));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            }catch(NullPointerException e){
                e.printStackTrace();
            }

            return onlinePrecincts;
        }


        public static List<Zone> getZones(String keyZones, Map<String,JSONArray> downloadedContent){

            String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
            List<Zone> onlineZones = new ArrayList<>();
            JSONArray jsonArrayUsers = downloadedContent.get(keyZones);

            try{
            for(int i = 0; i < jsonArrayUsers.length(); i++){
                JSONObject jresponse = null;
                try {
                    jresponse = jsonArrayUsers.getJSONObject(i);
                    int id = jresponse.getInt("id");
                    String name = jresponse.getString("Name");
                    int amount = jresponse.getInt("Amount");


                    //Download list of users and cache incase needed.
                    onlineZones.add(new Zone(id,name,amount));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            }catch(NullPointerException e){
                e.printStackTrace();
            }

            return onlineZones;
        }


        public static List<Site> getSites(String keySites, Map<String,JSONArray> downloadedContent){

            String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
            List<Site> onlineSites = new ArrayList<>();
            JSONArray jsonArraySites = downloadedContent.get(keySites);

            try{
            for(int i = 0; i < jsonArraySites.length(); i++){
                JSONObject jresponse = null;
                try {
                    jresponse = jsonArraySites.getJSONObject(i);
                    int id = jresponse.getInt("Id");
                    String SiteCode = jresponse.getString("SiteCode");
                    String Name = jresponse.getString("Name");
                    String Description = jresponse.getString("Description");
                    String Address = jresponse.getString("Address");
                    String City = jresponse.getString("City");
                    String Tel = jresponse.getString("Tel");
                    String Cell = jresponse.getString("Cell");
                    String Fax = jresponse.getString("Fax");
                    String Email = jresponse.getString("Email");
                    String Motto = jresponse.getString("Motto");
                    String Comment = jresponse.getString("Comment");


                    //Download list of users and cache incase needed.
                    onlineSites.add(new Site(id,SiteCode,Name,Description,Address,City,Tel,Cell,Fax,Email,Motto,Comment));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            }catch(NullPointerException e){
                e.printStackTrace();
            }

            return onlineSites;
        }


        public static List<Tarriff> getTarriffs(String keyTarriffs, Map<String,JSONArray> downloadedContent){

            String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
            List<Tarriff> onlineTarriffs = new ArrayList<>();
            JSONArray jsonArrayTarriffs = downloadedContent.get(keyTarriffs);

            try{
            for(int i = 0; i < jsonArrayTarriffs.length(); i++){
                JSONObject jresponse = null;
                try {
                    jresponse = jsonArrayTarriffs.getJSONObject(i);

                    int id = jresponse.getInt("id");
                    String Name = jresponse.getString("Name");
                    int Amount = jresponse.getInt("Amount");

                    //Download list of users and cache incase needed.
                    onlineTarriffs.add(new Tarriff(id,Name,Amount));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            }catch(NullPointerException e){
                e.printStackTrace();
            }
            return onlineTarriffs;
        }



        public static List<Bay> getBays(String keyBays, Map<String,JSONArray> downloadedContent){

            String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
            List<Bay> onlineBays = new ArrayList<>();
            JSONArray jsonArrayUsers = downloadedContent.get(keyBays);

            try {

                for (int i = 0; i < jsonArrayUsers.length(); i++) {
                    JSONObject jresponse = null;
                    try {
                        jresponse = jsonArrayUsers.getJSONObject(i);
                        int id = jresponse.getInt("id");
                        int PrecinctId = jresponse.getInt("PrecinctId");
                        String BayNumber = jresponse.getString("BayNumber");

                        //Download list of users and cache incase needed.
                        onlineBays.add(new Bay(id, PrecinctId, BayNumber));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }catch(NullPointerException e){
                e.printStackTrace();
            }

            return onlineBays;
        }


    }













}
