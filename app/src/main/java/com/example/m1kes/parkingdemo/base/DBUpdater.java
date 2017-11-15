package com.example.m1kes.parkingdemo.base;


import android.content.Context;

import com.example.m1kes.parkingdemo.models.Bay;
import com.example.m1kes.parkingdemo.models.PaymentMode;
import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.models.Site;
import com.example.m1kes.parkingdemo.models.SystemSettings;
import com.example.m1kes.parkingdemo.models.Tarriff;
import com.example.m1kes.parkingdemo.models.TransactionType;
import com.example.m1kes.parkingdemo.models.User;
import com.example.m1kes.parkingdemo.models.Zone;
import com.example.m1kes.parkingdemo.sqlite.adapters.BayAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PaymentModeAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrecinctAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.PrinterAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.SiteAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.SystemSettingsAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TarriffAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionTypeAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.ZonesAdapter;

import java.lang.reflect.Field;
import java.util.List;

public class DBUpdater {





    public static<T> boolean replaceItems(List<T> itemsToRefill, Context context){

        if(itemsToRefill!=null&&!itemsToRefill.isEmpty()){

            Object item = itemsToRefill.get(0);

            if(item instanceof User){
                //This method clears the table in the DB and the adds a list of new items to the DB
                UserAdapter.refillUsers((List<User>)(Object)itemsToRefill,context);
            }else if(item instanceof Zone){
                ZonesAdapter.refillZones((List<Zone>)(Object)itemsToRefill,context);
            }else if(item instanceof Bay){
                BayAdapter.refillBays((List<Bay>)(Object)itemsToRefill,context);
            }else if(item instanceof Site){
                SiteAdapter.refillSites((List<Site>)(Object)itemsToRefill,context);
            }else if(item instanceof Tarriff){
                TarriffAdapter.refillTarriffs((List<Tarriff>)(Object)itemsToRefill,context);
            }else if(item instanceof Precinct){
                PrecinctAdapter.refillPrecincts((List<Precinct>)(Object)itemsToRefill,context);
            }else if(item instanceof Printer){
                PrinterAdapter.refillPrinters((List<Printer>)(Object)itemsToRefill,context);
            }else if(item instanceof SystemSettings){
                SystemSettingsAdapter.refillSystemSettings((List<SystemSettings>)(Object)itemsToRefill,context);
            }else if(item instanceof TransactionType){
                TransactionTypeAdapter.refillTransactionTypes((List<TransactionType>)(Object)itemsToRefill,context);
            }else if(item instanceof PaymentMode){
                PaymentModeAdapter.refillPaymentMethods((List<PaymentMode>)(Object)itemsToRefill,context);
            }



        }






        return false;
    }




}
