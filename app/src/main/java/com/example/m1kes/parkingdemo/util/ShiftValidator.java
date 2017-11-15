package com.example.m1kes.parkingdemo.util;

import android.content.Context;

import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.sqlite.adapters.ShiftsAdapter;

import java.nio.*;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class ShiftValidator {


    public static boolean checkifShiftIsAlreadyCreated(Context context,String username) {

        boolean isAlreadyOpen = false;
        Date currentTime = new Date();

        List<Shift> shifts  = ShiftsAdapter.getAllLocalShifts(context);
        for(Shift shift: shifts){

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            //String tymStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(Calendar.getInstance().getTime());
            Date shiftCreated =  DateUtils.getDateFromLong(shift.getStart_time());
            cal1.setTime(currentTime);
            cal2.setTime(shiftCreated);
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            if(sameDay){
                if(shift.getUsername().equalsIgnoreCase(username)){
                    isAlreadyOpen = true;
                    break;
                }
            }
        }

        return  isAlreadyOpen;

    }
}
