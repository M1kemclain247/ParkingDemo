package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.models.Bay;
import com.example.m1kes.parkingdemo.models.Site;
import com.example.m1kes.parkingdemo.models.Tarriff;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.BaysTable;
import com.example.m1kes.parkingdemo.sqlite.tables.SiteTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TarriffsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.ZonesTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.m1kes.parkingdemo.util.DBUtils.convertIntToBool;
import static com.example.m1kes.parkingdemo.util.DBUtils.getDateFromDateTime;
import static com.example.m1kes.parkingdemo.util.DBUtils.parseDate;

public class SiteAdapter {

    /**
     * used to return a List of all the Sites in the Database
     *
     * @param context
     * @return List<Site> from the database
     */

    public static List<Site> getAllSites(Context context){

        String dateTimeParseFormat = "dd-MM-yyyy HH:mm:ss";
        List<Site> sites = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.SITE_CONTENT_URI , null, null, null, null);

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
                Site site = new Site();

                int id = cursor.getInt(cursor.getColumnIndex(SiteTable.COLUMN_ID));
                String siteCode = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_SITE_CODE));
                String name = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_NAME));
                String description = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_DESCRIPTION));
                String address = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_ADDRESS));
                String city = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_CITY));
                String tel = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_TEL));
                String cell = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_CELL));
                String fax = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_FAX));
                String email = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_EMAIL));
                String motto = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_MOTTO));
                String comment = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_COMMENT));
                String audp = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_AUDP));
                String lu_audp = cursor.getString (cursor.getColumnIndex (SiteTable.COLUMN_LU_AUDP));


                site.setId(id);
                site.setSiteCode(siteCode);
                site.setName(name);
                site.setDescripiton(description);
                site.setAddress(address);
                site.setCity(city);
                site.setTel(tel);
                site.setCell(cell);
                site.setFax(fax);
                site.setEmail(email);
                site.setMotto(motto);
                site.setComment(comment);
                site.setAudp(audp);
                site.setLu_audp(lu_audp);

                // Show phone number in Logcat
                Log.i("SiteAdapter ", site.toString());
                // end of while loop


                sites.add(site);
            }

        }

        return sites;
    }

    public static void addSite(Site site, Context context) {
        String format = "yyyy-MM-dd HH:mm:ss";

        ContentValues initialValues = new ContentValues();
        initialValues.put(SiteTable.COLUMN_ID,site.getId());
        initialValues.put(SiteTable.COLUMN_SITE_CODE, site.getSiteCode());
        initialValues.put(SiteTable.COLUMN_NAME, site.getName());
        initialValues.put(SiteTable.COLUMN_DESCRIPTION, site.getDescripiton());
        initialValues.put(SiteTable.COLUMN_ADDRESS, site.getAddress());
        initialValues.put(SiteTable.COLUMN_CITY, site.getCity());
        initialValues.put(SiteTable.COLUMN_TEL, site.getTel());
        initialValues.put(SiteTable.COLUMN_CELL, site.getCell());
        initialValues.put(SiteTable.COLUMN_FAX, site.getFax());
        initialValues.put(SiteTable.COLUMN_EMAIL, site.getEmail());
        initialValues.put(SiteTable.COLUMN_MOTTO, site.getMotto());
        initialValues.put(SiteTable.COLUMN_COMMENT, site.getComment());
        initialValues.put(SiteTable.COLUMN_AUDP, site.getAudp());
        initialValues.put(SiteTable.COLUMN_LU_AUDP, site.getLu_audp());

        System.out.println("Adding Site : "+site.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, SiteTable.TABLE_SITE);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added A Site Successfully");

    }
    //Not tested
    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, SiteTable.TABLE_SITE);
        int result = context.getContentResolver().delete(contentUri,null,null);

        System.out.println("Deleted status: "+result);


    }

    public static void refillSites(List<Site> sites, Context context) {

        deleteAll(context);
        for(Site site : sites){
            addSite(site,context);
        }


    }

}
