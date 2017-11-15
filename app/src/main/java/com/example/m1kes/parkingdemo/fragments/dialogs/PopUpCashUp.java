package com.example.m1kes.parkingdemo.fragments.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.printer.models.Vehicle;
import com.example.m1kes.parkingdemo.sqlite.adapters.TransactionAdapter;
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;

import java.util.List;


public class PopUpCashUp extends android.app.DialogFragment {


    public interface PopUpCashUpDailogListener {
        public void onCashupClick(DialogFragment dialog, EditText editTextTotalAmount,EditText editConfirmTotalAmount);
        public void onCancelCashup(DialogFragment dialog);
    }

    PopUpCashUpDailogListener mListener;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (PopUpCashUpDailogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AuthorizeUserDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pop_up_cash_up, container, false);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alert = null;
        Dialog d = null;

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final EditText editCashupAmount ;
        TextView txtCurrentLoggedInUser,marshalUp_txtVehiclesIn;

        View view = layoutInflater.inflate(R.layout.fragment_pop_up_cash_up, null);
        txtCurrentLoggedInUser = (TextView)view.findViewById(R.id.txtCurrentUserCashup);
        marshalUp_txtVehiclesIn = (TextView)view.findViewById(R.id.marshalUp_txtVehiclesIn);
        editCashupAmount = (EditText)view.findViewById(R.id.marshalUp_txtShiftTotal);

        String username = UserAdapter.getLoggedInUser(getActivity());
        if(username!=null){
            txtCurrentLoggedInUser.setText(username);
        }else{
            txtCurrentLoggedInUser.setText("No-One");
        }

        //Get All currently logged in vehicles
        List<Vehicle> loggedVehicleTransactions = TransactionAdapter.getAllLoginPrintoutTransactions(getActivity());
        int numLoggedVehicles  = loggedVehicleTransactions.size();
        //Set num of logged Vehicles to textview
        marshalUp_txtVehiclesIn.setText(String.valueOf(numLoggedVehicles));




        builder.setView(view);
        builder.setIcon(R.drawable.ic_coin_black_24dp);
        builder.setTitle("Cash Up");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("CASH UP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(false);

        d = builder.create();
        return d;
    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog d = (AlertDialog) getDialog();
        if(d!=null){

            Button positiveButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText marshalUp_txtShiftTotal = (EditText) d.findViewById(R.id.marshalUp_txtShiftTotal);
                    EditText marshalUp_txtShiftTotalConfirm = (EditText)d.findViewById(R.id.marshalUp_txtShiftTotalConfirm);
                    mListener.onCashupClick(PopUpCashUp.this,marshalUp_txtShiftTotal,marshalUp_txtShiftTotalConfirm);

                }
            });

            Button negativeButton = d.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onCancelCashup(PopUpCashUp.this);
                }
            });


        }


    }






}
