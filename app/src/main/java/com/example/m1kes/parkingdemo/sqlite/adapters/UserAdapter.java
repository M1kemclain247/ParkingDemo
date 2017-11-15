package com.example.m1kes.parkingdemo.sqlite.adapters;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.models.Roles;
import com.example.m1kes.parkingdemo.models.User;
import com.example.m1kes.parkingdemo.sqlite.provider.DatabaseContentProvider;
import com.example.m1kes.parkingdemo.sqlite.tables.ShiftsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.TransactionsTable;
import com.example.m1kes.parkingdemo.sqlite.tables.UsersTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.example.m1kes.parkingdemo.util.DBUtils.*;

public class UserAdapter {


    /**
     * used to return a List of all the users in the Database
     *
     * @param context
     * @return List<Users> from the database
     */

    public static List<User> getAllUsers(Context context) {

        List<User> users = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(DatabaseContentProvider.USERS_CONTENT_URI, null, null, null, null);

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
                User user = new User();

                String id = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_ID));
                String username = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_USERNAME));
                String password = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_PASSWORD));
                int empNo = cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_EMP_NO));
                String firstname = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_FIRSTNAME));
                String surname = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_SURNAME));
                boolean isLoggedIn = convertIntToBool(cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_IS_LOGGED_IN)));
                List<String> roles = convertStringRolesToList(cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_ROLES)));


                user.setId(id);
                user.setUsername(username);
                user.setPassword(password);
                user.setEmpNo(empNo);
                user.setFirstname(firstname);
                user.setSurname(surname);
                user.setLoggedIn(isLoggedIn);
                user.setRoles(roles);

                // Show phone number in Logcat
                Log.i("UserAdapter ", user.toString());
                // end of while loop

                users.add(user);
            }

        }

        return users;
    }

    private static List<String> convertStringRolesToList(String rolesString) {
        return Arrays.asList(rolesString.split("\\s*,\\s*"));
    }

    private static String convertRolesListToString(List<String> roles){
        return android.text.TextUtils.join(",", roles);
    }

    public static List<String> getAllUserNames(Context context) {

        List<String> usernames = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(DatabaseContentProvider.USERS_CONTENT_URI, null, null, null, null);

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
                String username = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_USERNAME));
                usernames.add(username);
            }

        }

        return usernames;
    }




    public static void refillUsers(List<User> users, Context context) {

        deleteAll(context);

        for(User user : users){
           addUser(user,context);
        }


    }


    public static void addUser(User user, Context context) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(UsersTable.COLUMN_ID, user.getId());
        initialValues.put(UsersTable.COLUMN_USERNAME, user.getUsername());
        initialValues.put(UsersTable.COLUMN_PASSWORD, user.getPassword());
        initialValues.put(UsersTable.COLUMN_EMP_NO, user.getEmpNo());
        initialValues.put(UsersTable.COLUMN_FIRSTNAME, user.getFirstname());
        initialValues.put(UsersTable.COLUMN_SURNAME, user.getSurname());
        initialValues.put(UsersTable.COLUMN_IS_LOGGED_IN, convertBoolToInt(user.isLoggedIn()));
        initialValues.put(UsersTable.COLUMN_ROLES,convertRolesListToString(user.getRoles()));
        System.out.println(user.toString());
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, UsersTable.TABLE_USERS);
        Uri resultUri = context.getContentResolver().insert(contentUri, initialValues);
        //show that it is inserted successfully
        System.out.println("Added A user");
        //Toast.makeText(context, "Added!", Toast.LENGTH_SHORT).show();
    }

    private static List<User> findUsers(User user, Context context) {

        List<User> users = new ArrayList<>();
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(DatabaseContentProvider.USERS_CONTENT_URI, null, UsersTable.COLUMN_USERNAME + " = '" + user.getUsername() + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            System.out.println("Cursor is Null");
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.

            System.out.println("Not found anything");
        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.
                User tempUser = new User();

                String contactId = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_ID));
                String contactName = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_USERNAME));
                String contactNumber = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_PASSWORD));
                int empNo = cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_EMP_NO));
                String firstname = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_FIRSTNAME));
                String surname = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_SURNAME));
                boolean isLoggedIn = convertIntToBool(cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_IS_LOGGED_IN)));
                List<String> roles = convertStringRolesToList(cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_ROLES)));
                // Show phone number in Logcat

                // end of while loop
                tempUser.setId(contactId);
                tempUser.setUsername(contactName);
                tempUser.setPassword(contactNumber);

                tempUser.setEmpNo(empNo);
                tempUser.setFirstname(firstname);
                tempUser.setSurname(surname);

                tempUser.setLoggedIn(isLoggedIn);
                tempUser.setRoles(roles);

                System.out.println("Found user: "+tempUser);

                //Get each user with the same name if only 1 then get only 1
                users.add(tempUser);

            }
        }
        return users;
    }

    public static User findUserWithName(String username, Context context) {

        User user = null;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(DatabaseContentProvider.USERS_CONTENT_URI, null, UsersTable.COLUMN_USERNAME + " = '" + username + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return null;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.

            return null;
        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.
               user =  new User();

                String contactId = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_ID));
                String contactName = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_USERNAME));
                String contactNumber = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_PASSWORD));
                int empNo = cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_EMP_NO));
                String firstname = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_FIRSTNAME));
                String surname = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_SURNAME));
                boolean isLoggedIn = convertIntToBool(cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_IS_LOGGED_IN)));
                List<String> roles = convertStringRolesToList(cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_ROLES)));
                // Show phone number in Logcat
                Log.i("Phone", " Numbers : " + contactNumber);
                // end of while loop
                user.setId(contactId);
                user.setUsername(contactName);
                user.setPassword(contactNumber);

                user.setEmpNo(empNo);
                user.setFirstname(firstname);
                user.setSurname(surname);

                user.setLoggedIn(isLoggedIn);
                user.setRoles(roles);


            }
        }
        return user;
    }



    private static boolean verifyUser(List<User> users,User user,Context context){
        System.out.println("In Verify Method!");
        boolean isverified = false;

        for(User check : users){
            System.out.println("Trying to Login User: "+check);
            if(check.getPassword().equals(user.getPassword())){
                isverified = true;
                //TODO SET IN DB THAT USER IS NOW LOGGED IN
                System.out.println("User: "+user.getUsername()+" is verified");
            }
        }
        return isverified;
    }


    public static boolean loginUser(User user,Context context){
        System.out.println("In Login Method!");
       return verifyUser(findUsers(user,context),user,context);
    }



    private static boolean verifySupervisor(List<User> users,User user,Context context){

        boolean isverified = false;

        for(User check : users){
            if(check.getPassword().equals(user.getPassword())){
                //TODO AND IS IN ROLE SUPERVISOR
                if(check.getRoles().contains(Roles.ROLE_MANAGER)||check.getRoles().contains(Roles.ROLE_ADMIN)){
                    System.out.println("User: "+user.getUsername()+" is verified and Authorized");
                    isverified = true;
                    break;
                }
            }
        }
        return isverified;
    }

    public static boolean loginSupervisor(User user,Context context){
        return verifySupervisor(findUsers(user,context),user,context);
    }




    public static void setUserLoggedIn(String username,Context context){

        ContentValues updateValues = new ContentValues();
        updateValues.put(UsersTable.COLUMN_IS_LOGGED_IN, convertBoolToInt(true));
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, UsersTable.TABLE_USERS);
        System.out.println("Trying to login User:  "+username);
        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,UsersTable.COLUMN_USERNAME+"=?",new String[]{username});

        if(rowsAffected>0){
            System.out.println(" Username: "+username + " is now Logged IN");
            System.out.println(" ********************************");
            System.out.println(" Updated User Status Successfully");
            System.out.println(" ********************************");
        }else{
            System.out.println(" ***************************");
            System.out.println(" FAILED Updating User Status");
            System.out.println(" ***************************");
        }
    }

    public static boolean logoutUser(String username,Context context){

        boolean isLoggedOut = false;
        ContentValues updateValues = new ContentValues();
        updateValues.put(UsersTable.COLUMN_IS_LOGGED_IN, convertBoolToInt(false));
        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, UsersTable.TABLE_USERS);
        System.out.println("Trying to logout User: "+username);
        int rowsAffected =  context.getContentResolver().update(contentUri,updateValues,UsersTable.COLUMN_USERNAME+"=?",new String[]{username});
        //show that it is inserted successfully
        if(rowsAffected>0){
            System.out.println(" Username: "+username + " is now Logged out");
            System.out.println(" ********************************");
            System.out.println(" Updated User Status Successfully");
            System.out.println(" ********************************");
            isLoggedOut = true;
        }else{
            System.out.println(" ***************************");
            System.out.println(" FAILED Updating User Status");
            System.out.println(" ***************************");
        }



        return isLoggedOut;
    }

    public static boolean checkIfLoggedIn(String username, Context context){

        boolean isLoggedIn = false;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.USERS_CONTENT_URI , null,  UsersTable.COLUMN_USERNAME + " = '" + username + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return isLoggedIn;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return isLoggedIn;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.

                isLoggedIn  = convertIntToBool(cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_IS_LOGGED_IN)));

                if(isLoggedIn)
                    System.out.println("Authorized : User is logged in!");
                else
                    System.out.println("Error : User Not Logged In!");

            }

        }



        return isLoggedIn;
    }


    public static String getLoggedInUser(Context context){

        String username = null;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.USERS_CONTENT_URI , null,  UsersTable.COLUMN_IS_LOGGED_IN + " = '" + "1" + "'", null, null);
        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            return username;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            return username;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.

                username  = cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_USERNAME));


                if(username!=null)
                    System.out.println("Found currently logged User : "+username);
                else
                    System.out.println("Error : Currently No Logged Users!");

            }

        }



        return username;
    }



    public static int getCurrentLoggedUserID(String username, Context context){

        int userID = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.USERS_CONTENT_URI , null,  UsersTable.COLUMN_USERNAME + " = '" + username + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            System.out.println("Coulnt find userID with that usrname");
            return userID;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            System.out.println("Coulnt find userID with that usrname");
            return userID;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.

                userID  = cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_ID));

                System.out.println("Found user ID: "+userID);

            }

        }



        return userID;
    }

    public static int getUserEmpNo(String username, Context context){

        int empNo = 0;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query (DatabaseContentProvider.USERS_CONTENT_URI , null,  UsersTable.COLUMN_USERNAME + " = '" + username + "'", null, null);

        // Some providers return null if an error occurs, others throw an exception
        if (null == cursor) {
            // Insert code here to handle the error. Be sure not to use the cursor! You may want to
            // call android.util.Log.e() to log this error.
            System.out.println("Coulnt find userID with that usrname");
            return empNo;
            // If the Cursor is empty, the provider found no matches
        } else if (cursor.getCount() < 1) {
            // Insert code here to notify the user that the contact query was unsuccessful. This isn’t necessarily
            // an error. You may want to offer the user the option to insert a new row, or re-type the
            // search term.
            System.out.println("Coulnt find userID with that usrname");
            return empNo;

        } else {
            // Insert code here to do something with the results
            // Moves to the next row in the cursor. Before the first movement in the cursor, the
            // "row pointer" is -1, and if you try to retrieve data at that position you will get an
            // exception.

            while (cursor.moveToNext()) {
                // Gets the value from the column.

                empNo  = cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_EMP_NO));

                System.out.println("Found user with EmpNo : "+empNo);

            }

        }



        return empNo;
    }



    //Not tested
    public static void deleteAll(Context context){

        Uri contentUri = Uri.withAppendedPath(DatabaseContentProvider.CONTENT_URI, UsersTable.TABLE_USERS);
        int result = context.getContentResolver().delete(contentUri,null,null);

        System.out.println("Deleted status: "+result);


    }

}
