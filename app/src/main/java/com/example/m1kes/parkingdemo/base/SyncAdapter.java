package com.example.m1kes.parkingdemo.base;

//This is used for either hard removing all contents of SQLite DB and refilling from web service
//Or we can see what is only needed to be updated

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.models.Bay;
import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.models.Site;
import com.example.m1kes.parkingdemo.models.SystemSettings;
import com.example.m1kes.parkingdemo.models.Tarriff;
import com.example.m1kes.parkingdemo.models.User;
import com.example.m1kes.parkingdemo.models.Zone;
import com.example.m1kes.parkingdemo.sqlite.adapters.BayAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrecinctAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrinterAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.SiteAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.SystemSettingsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TarriffAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ZonesAdapter;
import com.example.m1kes.parkingdemo.sqlite.tables.PrinterTable;

import java.util.ArrayList;
import java.util.List;

public class SyncAdapter {

    private Handler handler;
    private TextView txtUpdateStatus;
    private Context context;

    public SyncAdapter(Handler handler, TextView txtUpdateStatus,Context context){
        this.handler = handler;
        this.txtUpdateStatus = txtUpdateStatus;
        this.context = context;
    }



    public void syncZones(List<Zone> onlineZones){
        showUpdateProgress("Syncing Zones....");
        sleep(1000);

        //Get a list of Local Users From the local SQLITE DB
        List<Zone> localZones = ZonesAdapter.getAllZones(context);

        List<Zone> zonesToAdd = checkIfNeedtoUpdate(localZones,onlineZones);
        if(zonesToAdd!=null&&!zonesToAdd.isEmpty()) {
            int zoneCount = 1;
            for (Zone zone : onlineZones) {
                //Add zone to db
                ZonesAdapter.addZone(zone, context);
                showUpdateProgress("Updating Zones: " + zoneCount + "/" + onlineZones.size() +" "+ zone.getName());
                zoneCount++;
                sleep(20);
            }
            System.out.println("Logging Zones: " + onlineZones.toString());

        }else{
            showUpdateProgress("Zones Already Up-To-Date");
            sleep(800);
        }


    }


    public void syncPrinters(List<Printer> onlinePrinters){
        showUpdateProgress("Syncing Printers....");
        sleep(1000);

        //Get a list of Local Users From the local SQLITE DB
        List<Printer> localPrinters = PrinterAdapter.getAllPrinters(context);

        List<Printer> printersToAdd = checkIfNeedtoUpdate(localPrinters,onlinePrinters);
        if(printersToAdd!=null&&!printersToAdd.isEmpty()) {
            int printerCount = 1;
            for (Printer printer : onlinePrinters) {
                //Add zone to db
                PrinterAdapter.addPrinter(printer, context);
                showUpdateProgress("Updating Printers: " + printerCount + "/" + onlinePrinters.size() +" ");
                printerCount++;
                sleep(500);
            }
            System.out.println("Logging Printers: " + onlinePrinters.toString());

        }else{
            showUpdateProgress("Printers Already Up-To-Date");
            sleep(800);
        }


    }

    public void syncSystemSettings(List<SystemSettings> onlineSystemSettings){
        showUpdateProgress("Syncing SystemSettings....");
        sleep(1000);

        //Get a list of Local Users From the local SQLITE DB
        List<SystemSettings> localSystemSettings = SystemSettingsAdapter.getAllSystemSettings(context);

        List<SystemSettings> systemSettingsToAdd = checkIfNeedtoUpdate(localSystemSettings,onlineSystemSettings);
        if(systemSettingsToAdd!=null&&!systemSettingsToAdd.isEmpty()) {
            int count = 1;
            for (SystemSettings settings : onlineSystemSettings) {
                //Add zone to db
                SystemSettingsAdapter.addSystemSettings(settings, context);
                showUpdateProgress("Updating SystemSettings: " + count + "/" + onlineSystemSettings.size() +" ");
                count++;
                sleep(500);
            }
            System.out.println("Logging SystemSettings: " + onlineSystemSettings.toString());

        }else{
            showUpdateProgress("SystemSettings Already Up-To-Date");
            sleep(800);
        }


    }


    public void syncBays(List<Bay> onlineBays){
        showUpdateProgress("Syncing Bays....");
        sleep(1000);

        //Get a list of Local Users From the local SQLITE DB
        List<Bay> localBays = BayAdapter.getAllBays(context);
        System.out.println("Local Bays: "+localBays.toString());

        List<Bay> baysToAdd = checkIfNeedtoUpdate(localBays,onlineBays);
        if(baysToAdd!=null&&!baysToAdd.isEmpty()) {
            int bayCount = 1;
            for (Bay bay : onlineBays) {
                //Add bay to db
                BayAdapter.addBay(bay, context);
                showUpdateProgress("Updating Bays: " + bayCount + "/" + onlineBays.size() +" ");
                bayCount++;
                sleep(800);
            }
            System.out.println("Logging Bays: " + onlineBays.toString());

        }else{
            showUpdateProgress("Bays Already Up-To-Date");
            sleep(800);
        }


    }


    public void syncPrecincts(List<Precinct> onlinePrecincts){
        showUpdateProgress("Syncing Precincts....");
        sleep(1000);

        //Get a list of Local Users From the local SQLITE DB
        List<Precinct> localPrecincts = PrecinctAdapter.getAllPrecincts(context);
        System.out.println("Local Precincts: "+localPrecincts.toString());

        List<Precinct> precinctsToAdd = checkIfNeedtoUpdate(localPrecincts,onlinePrecincts);
        if(precinctsToAdd!=null&&!precinctsToAdd.isEmpty()) {
            int precinctCount = 1;
            for (Precinct precinct : onlinePrecincts) {
                //Add bay to db
                PrecinctAdapter.addPrecinct(precinct, context);
                showUpdateProgress("Updating Precincts: " + precinctCount + "/" + onlinePrecincts.size() +" "+precinct.getName());
                precinctCount++;
                sleep(800);
            }
            System.out.println("Logging Precincts: " + onlinePrecincts.toString());

        }else{
            showUpdateProgress("Precincts Already Up-To-Date");
            sleep(800);
        }


    }



    public void syncSites(List<Site> onlineSites){
        showUpdateProgress("Syncing Sites....");
        sleep(1000);

        //Get a list of Local Users From the local SQLITE DB
        List<Site> localSites = SiteAdapter.getAllSites(context);
        System.out.println("Local Sites: "+localSites.toString());

        List<Site> sitesToAdd = checkIfNeedtoUpdate(localSites,onlineSites);
        if(sitesToAdd!=null&&!sitesToAdd.isEmpty()) {
            int siteCount = 1;
            for (Site site : onlineSites) {
                //Add bay to db
                SiteAdapter.addSite(site, context);
                showUpdateProgress("Updating Sites: " + siteCount + "/" + onlineSites.size() +" " +site.getName());
                siteCount++;
                sleep(800);
            }
            System.out.println("Logging Bays: " + onlineSites.toString());

        }else{
            showUpdateProgress("Sites Already Up-To-Date");
            sleep(800);
        }


    }


    public void syncTarriffs(List<Tarriff> onlineTarriffs){
        showUpdateProgress("Syncing Tarriffs....");
        sleep(1000);

        //Get a list of Local Users From the local SQLITE DB
        List<Tarriff> localTarriffs = TarriffAdapter.getAllTarriffs(context);
        System.out.println("Local Tarriffs: "+localTarriffs.toString());

        List<Tarriff> tarriffsToAdd = checkIfNeedtoUpdate(localTarriffs,onlineTarriffs);
        if(tarriffsToAdd!=null&&!tarriffsToAdd.isEmpty()) {
            int tarriffCount = 1;
            for (Tarriff tarriff : onlineTarriffs) {
                //Add bay to db
                TarriffAdapter.addTarriff(tarriff, context);
                showUpdateProgress("Updating Tarriffs: " + tarriffCount + "/" + onlineTarriffs.size() +" " +tarriff.getName());
                tarriffCount++;
                sleep(800);
            }
            System.out.println("Logging Tarriffs: " + onlineTarriffs.toString());

        }else{
            showUpdateProgress("Tarriffs Already Up-To-Date");
            sleep(800);
        }


    }



    public void syncUsers(List<User> onlineUsers){

        showUpdateProgress("Syncing Users...");
        sleep(1000);

        //Get a list of Local Users From the local SQLITE DB
        List<User> localUsers = UserAdapter.getAllUsers(context);

        List<User> usersToAddToDb = checkIfNeedtoUpdate(localUsers,onlineUsers);
        if(usersToAddToDb!=null&&!usersToAddToDb.isEmpty()){
            //then we add the users to the database

            showUpdateProgress("Updating Local User Database...");
            sleep(200);
            int i = 0;
            for(User user : usersToAddToDb){

                UserAdapter.addUser(user,context);
                showUpdateProgress("Updating Users: "+(i+1)+"/"+usersToAddToDb.size()+"  "+user.getUsername());
                sleep(300);
                i++;
            }
        }else {
            showUpdateProgress("Users Already Up-To-Date!");
            sleep(300);
        }
    }


    private <T>List<T> checkIfNeedtoUpdate(List<T> localResources, List<T> onlineResources){

        List<T> resourcesToUpdate = new ArrayList<>();

        for(T t : onlineResources){
            if(!localResources.contains(t)){
                resourcesToUpdate.add(t);
                System.out.println("Doesn't Contain : "+t.toString());
            }
        }

        System.out.println("To be Updated: "+resourcesToUpdate);
        return resourcesToUpdate;
    }


    /**
     * Responsible for Making the current update Thread to sleep
     *
     * **/

    private void sleep(long sleepTime){

        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    /**
     * Used for updating the text view with user updates
     * */
    private void showUpdateProgress(final String txt){
        handler.post(new Runnable() {
            @Override
            public void run() {
                txtUpdateStatus.setText(txt);
            }
        });
    }

}
