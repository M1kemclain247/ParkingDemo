package com.example.m1kes.parkingdemo.networking.parsers;


import android.content.Context;
import android.sax.RootElement;

import com.example.m1kes.parkingdemo.printer.models.BalanceEnquiry;
import com.example.m1kes.parkingdemo.printer.models.Receipt;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;
import com.example.m1kes.parkingdemo.util.ShiftDataManager;

import org.json.JSONException;
import org.json.JSONObject;

public class BalanceEnquiryParser {

    public static BalanceEnquiry getBalanceEnquiry(String response, Context context){

        System.out.println("Parsing the Response : "+ response);
        JSONObject jsonObject = null;



        try {
            jsonObject = new JSONObject(response);
           // String vehicleID = jsonObject.getString("Id");
            String shiftID = jsonObject.getString("shiftid");
            String vehicleReg = jsonObject.getString("VehicleReg");
            String owner = jsonObject.getString("Owner");
            String balance = jsonObject.getString("Balance");
            String lastlogged = jsonObject.getString("lastlogged");
            String reponsedatetime = jsonObject.getString("responsedatetime");
            String currentUser = UserAdapter.getLoggedInUser(context);
            System.out.println("Succesfully Parsed the Response");
                                                          //last loggged
            return new BalanceEnquiry(shiftID,vehicleReg,owner,Double.valueOf(balance),lastlogged,reponsedatetime,currentUser);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }





    }


}
