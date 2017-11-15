package com.example.m1kes.parkingdemo.textwatchers;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;


public class EmptyFieldsText implements TextWatcher {

    private Context context;
    private final EditText et;

    public EmptyFieldsText(Context context,EditText editText){
        this.context = context;
        this.et = editText;
    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(count>0){
            et.setError(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        et.removeTextChangedListener(this);

        if (s != null && !s.toString().isEmpty()) {

            int countLength = et.getText().length();
            if(countLength>0){
                et.setError(null);
            }

        }
    }
}
