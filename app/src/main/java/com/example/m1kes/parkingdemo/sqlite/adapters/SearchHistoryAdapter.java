package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.m1kes.parkingdemo.models.SearchHistory;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.SearchHistoryTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SearchHistoryAdapter {

    public static List<SearchHistory> getAllSearchHistory(Context context){

        List<SearchHistory> searchHistories = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SEARCH_HISTORY_CONTENT_URI , null, null, null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.

            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.
                SearchHistory searchHistory = new SearchHistory();

                int id = cursor.getInt(cursor.getColumnIndex(SearchHistoryTable.COLUMN_ID));
                String vehicleReg = cursor.getString (cursor.getColumnIndex (SearchHistoryTable.COLUMN_VEHICLE_REG));
                String searchTime = cursor.getString (cursor.getColumnIndex (SearchHistoryTable.COLUMN_SEARCH_TIME));

                searchHistory.setId(id);
                searchHistory.setVehicleReg(vehicleReg);
                searchHistory.setSearchDateTime(searchTime);


                // Show phone number in Logcat
                System.out.println("Loading from DB: "+searchHistory.toString());
                // end of while loop

                searchHistories.add(searchHistory);
            }

        }

        // Sorting
        Collections.sort(searchHistories, new Comparator<SearchHistory>() {
            @Override
            public int compare(SearchHistory searchHistory2, SearchHistory searchHistory1)
            {
                return  searchHistory1.getSearchDateTime().compareTo(searchHistory2.getSearchDateTime());
            }
        });


        return searchHistories;
    }


    public static void addNewSearchHistory(String regNumber,String searchDateTime,Context context){

        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setVehicleReg(regNumber);
        searchHistory.setSearchDateTime(searchDateTime);
        SearchHistoryAdapter.addSearchHistory(searchHistory,context);
    }


    public static void addSearchHistory(SearchHistory searchHistory, Context context) {
        String format = "yyyy-MM-dd HH:mm:ss";

        ContentValues initialValues = new ContentValues();
        initialValues.put(SearchHistoryTable.COLUMN_VEHICLE_REG,searchHistory.getVehicleReg());
        initialValues.put(SearchHistoryTable.COLUMN_SEARCH_TIME, searchHistory.getSearchDateTime());


        System.out.println("Adding Search History : "+searchHistory.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, SearchHistoryTable.TABLE_SEARCH_HISTORY);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added Search History successfully Successfully");

    }

    //Not tested
    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, SearchHistoryTable.TABLE_SEARCH_HISTORY);
        int result = context.getContentResolver().delete(contentUri,null,null);

        System.out.println("Deleted status: "+result);


    }

    public static void refillSearchHistory(List<SearchHistory> searchHistories, Context context) {
        deleteAll(context);
        for(SearchHistory searchHistory : searchHistories){
            addSearchHistory(searchHistory,context);
        }
    }







}
