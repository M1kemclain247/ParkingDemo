package com.example.m1kes.parkingdemo.adapters.spinners;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.R;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context context;
    private ArrayList<String> content;

    public CustomSpinnerAdapter(Context context, ArrayList<String> content){
        this.context = context;
        this.content =  content;
    }


    @Override
    public int getCount() {
        return content.size();
    }

    @Override
    public Object getItem(int position) {
        return content.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (long)position;
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setPadding(48, 48, 48, 48);
        txt.setTextSize(18);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(content.get(position));
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(16);
        txt.setText(content.get(position));
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }
}
