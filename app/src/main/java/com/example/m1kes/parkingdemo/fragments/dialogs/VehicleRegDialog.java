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
import com.example.m1kes.parkingdemo.util.ShiftDataManager;



public class VehicleRegDialog extends DialogFragment {


    public interface VehicleRegDialogListener {
        public void vehicleRegPositiveClick(DialogFragment dialog, EditText editText,int position);
        public void vehicleRegNegativeClick(DialogFragment dialog);
    }

    VehicleRegDialogListener mListener;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (VehicleRegDialogListener) activity;
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
        return inflater.inflate(R.layout.fragment_dialog_enter_vehiclereg, container, false);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alert = null;
        Dialog d = null;

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final EditText vehicleReg ;


        View view = layoutInflater.inflate(R.layout.fragment_dialog_enter_vehiclereg, null);
        vehicleReg = (EditText)view.findViewById(R.id.editVehicleRegDialog);

        builder.setView(view);
        builder.setIcon(R.drawable.ic_car_black_18dp);
        builder.setTitle("Enter Vehicle Reg No");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

        int i = 101;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
             i = bundle.getInt("position", 101);
        }

        final int position = i;

        if(d!=null){

            Button positiveButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EditText vehicleReg = (EditText) d.findViewById(R.id.editVehicleRegDialog);

                    if(vehicleReg.getText().toString().length()>0){
                        mListener.vehicleRegPositiveClick(VehicleRegDialog.this,vehicleReg,position);
                    }else{
                        vehicleReg.setError("Invalid Reg Number!");
                    }
                }
            });

            Button negativeButton = d.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.vehicleRegNegativeClick(VehicleRegDialog.this);
                }
            });


        }


    }
}
