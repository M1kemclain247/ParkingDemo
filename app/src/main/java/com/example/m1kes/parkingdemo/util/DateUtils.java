package com.example.m1kes.parkingdemo.util;

import java.util.Calendar;
import java.util.Date;

import static com.example.m1kes.parkingdemo.util.DBUtils.getDateParts;


public class DateUtils {


    public static Date getDateFromLong(long timeToConvert){
        String strTime = timeToConvert+"";
        Calendar cal = Calendar.getInstance();
        String[] parts= getDateParts(strTime);
        cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])-1, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
        return cal.getTime();
    }



}
