package com.example.m1kes.parkingdemo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DBUtils {


    /**
     * converts an intger value to a boolean value 1 = true  0 = false
     * @param intValue
     * @return
     */
    public static boolean convertIntToBool(int intValue){
        Boolean flag = (intValue == 1)? true : false;
        return flag;
    }

    /**
     * converts an boolean value to a int value 1 = true  0 = false
     * @param value
     * @return
     */

    public static int convertBoolToInt(boolean value) {
        // Convert true to 1 and false to 0.
        return value ? 1 : 0;
    }

    /**
     * Parses String datetime into a Date object using given format eg: dd-MM-yyyy HH:mm:ss
     *
     * @param dateTime
     * @param format
     * @return
     */
    public static Date parseDate(String dateTime, String format){

        SimpleDateFormat formatter = new SimpleDateFormat(format);
        System.out.println(dateTime+" and the format: "+format);
        Date d = null;
        try {
            d = formatter.parse(dateTime);
            return d;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String[]getDateParts(String longDate)
    {
        String tempArr[]= new String[6];

        tempArr[0]=longDate.substring(0, 4);
        tempArr[1]=longDate.substring(4, 6);
        tempArr[2]=longDate.substring(6, 8);
        tempArr[3]=longDate.substring(8, 10);
        //tempArr[3]="14";
        tempArr[4]=longDate.substring(10, 12);
        tempArr[5]=longDate.substring(12, 14);
        return tempArr;
    }

    public static String getDateFromDateTime(Date date,String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if(date==null){
            return null;
        }else{
            return sdf.format(date);
        }


    }


}
