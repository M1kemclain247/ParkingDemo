package com.example.m1kes.parkingdemo.adapters.spinners;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.models.PaymentMode;
import com.example.m1kes.parkingdemo.models.Zone;

import java.util.List;

public class PaymentModesSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context context;
    private List<PaymentMode> paymentModes;

    public PaymentModesSpinnerAdapter(Context context,List<PaymentMode> paymentModes){
        this.context =  context;
        this.paymentModes = paymentModes;
    }



    @Override
    public int getCount() {
        return paymentModes.size();
    }

    @Override
    public Object getItem(int position) {
        return paymentModes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(16);
        txt.setText(paymentModes.get(position).getName());
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setPadding(48, 48, 48, 48);
        txt.setTextSize(18);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(paymentModes.get(position).getName());
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }

}
