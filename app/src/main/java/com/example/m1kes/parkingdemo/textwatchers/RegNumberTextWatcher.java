package com.example.m1kes.parkingdemo.textwatchers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


public class RegNumberTextWatcher implements  TextWatcher {


    private final EditText et;
    private int maxLength = 8;

    public RegNumberTextWatcher(EditText editText){

        this.et = editText;

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        et.removeTextChangedListener(this);

        if (s != null && !s.toString().isEmpty()) {

            int countLength = et.getText().length();
            if(countLength>maxLength){

            }

        }



    }
}
