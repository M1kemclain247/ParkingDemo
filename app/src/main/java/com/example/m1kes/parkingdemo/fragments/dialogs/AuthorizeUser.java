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
import com.example.m1kes.parkingdemo.sqlite.adapters.UserAdapter;


public class AuthorizeUser extends DialogFragment {


    public static final String MODE_CASHUP = "Cashup";
    public static final String MODE_SPOT_CHECK = "SpotCheck";


    private String MODE;

    public interface AuthorizeUserDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog,EditText editPassword,EditText editUsername,String MODE);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    AuthorizeUserDialogListener mListener;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AuthorizeUserDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement AuthorizeUserDialogListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MODE = getArguments().getString("mode");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialog_authorize_user, container, false);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder alert = null;
        Dialog d = null;

        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final EditText  editUsernameAuthorizeUser;
        TextView txtCurrentLoggedInUser,txtSupervisorInfo;

            View view = layoutInflater.inflate(R.layout.fragment_dialog_authorize_user, null);
            txtCurrentLoggedInUser = (TextView)view.findViewById(R.id.txtCurrentUserAuthentication);

            txtSupervisorInfo = (TextView)view.findViewById(R.id.txtSupervisorInfo);
            editUsernameAuthorizeUser = (EditText)view.findViewById(R.id.editUsernameAuthorizeUser);

            String username = UserAdapter.getLoggedInUser(getActivity());
            if(username!=null){
                txtCurrentLoggedInUser.setText(username);
            }else{
                txtCurrentLoggedInUser.setText("No-One");
            }

            if(MODE.equals(AuthorizeUser.MODE_CASHUP)){
                txtSupervisorInfo.setVisibility(View.GONE);
                editUsernameAuthorizeUser.setVisibility(View.GONE);
            }

            builder.setView(view);
            builder.setIcon(R.drawable.ic_security_black_24dp);
            builder.setTitle("Enter Password");
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
        if(d!=null){

            Button positiveButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(MODE.equals(MODE_SPOT_CHECK)){
                        EditText editUsername = (EditText) d.findViewById(R.id.editUsernameAuthorizeUser);
                        EditText editPas = (EditText) d.findViewById(R.id.editPasswordAuthorizeUser);
                        mListener.onDialogPositiveClick(AuthorizeUser.this,editPas,editUsername,MODE);
                    }else if(MODE.equals(MODE_CASHUP)){
                        EditText editUsername = (EditText) d.findViewById(R.id.editUsernameAuthorizeUser);
                        EditText editPas = (EditText) d.findViewById(R.id.editPasswordAuthorizeUser);
                        //TODO Fix the way this is working am just sending it in but not sure if it will work
                        mListener.onDialogPositiveClick(AuthorizeUser.this,editPas,editUsername,MODE);
                    }

                }
            });

            Button negativeButton = d.getButton(AlertDialog.BUTTON_NEGATIVE);
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onDialogNegativeClick(AuthorizeUser.this);
                }
            });


        }


    }
}
