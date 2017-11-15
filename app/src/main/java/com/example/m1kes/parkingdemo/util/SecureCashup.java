package com.example.m1kes.parkingdemo.util;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.widget.EditText;

import com.example.m1kes.parkingdemo.R;

public class SecureCashup {



    public static void validateUserCashup(Context context){

        AlertDialog.Builder alert = null;
            //normal
        //new AlertDialog.Builder(context);
            //Themed
        alert = new AlertDialog.Builder(context, R.style.MyDialogTheme);

        alert.setIcon(R.drawable.ic_security_white_36dp);

        final EditText edittext = new EditText(context);

        alert.setMessage("Secure Cashup");
        alert.setTitle("Enter Password");
        alert.setView(edittext);
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String YouEditTextValue = edittext.getText().toString();
                //Validate User

            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.


            }
        });

        alert.show();


    }





}
