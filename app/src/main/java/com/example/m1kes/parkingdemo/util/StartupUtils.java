package com.example.m1kes.parkingdemo.util;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class StartupUtils {



    public static void setupActionBar(String title, AppCompatActivity appCompatActivity) {
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(title);
        }
    }

    public static void setupTitleOnlyActionBar(String title, AppCompatActivity appCompatActivity) {
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle(title);
        }
    }



    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
    }

   public static boolean isNowBetweenDateTime(final Date s, final Date e)
   {

       SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");

       Calendar calendar = Calendar.getInstance();
       Date now =  calendar.getTime();

       System.out.println("LastLogged: "+s.toString() +" Current: "+new SimpleDateFormat("yyyyMMddHHmmss").format(now) + "Expiry: "+e.toString());
       System.out.println("Now Object: "+now);

       String formattedStr = sf.format(now);
       Date newDate = null;
       try {
          newDate =  sf.parse(formattedStr);

       } catch (ParseException e1) {
           e1.printStackTrace();
       }

       return newDate.after(s) && newDate.before(e);
    }


}
