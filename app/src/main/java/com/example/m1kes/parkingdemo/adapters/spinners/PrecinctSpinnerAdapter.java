package com.example.m1kes.parkingdemo.adapters.spinners;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.models.Precinct;
import com.example.m1kes.parkingdemo.models.Zone;

import java.util.List;

public class PrecinctSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context context;
    private List<Precinct> precincts;
    public PrecinctSpinnerAdapter(Context context, List<Precinct> precincts){
        this.context = context;
        this.precincts =  precincts;
    }

    @Override
    public int getCount() {
        return precincts.size();
    }

    @Override
    public Object getItem(int position) {
        return precincts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  (long)position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setPadding(28, 28, 28, 28);
        txt.setTextSize(16);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(precincts.get(position).getName());
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(16, 16, 16, 16);
        txt.setTextSize(14);
        txt.setText(precincts.get(position).getName());
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }


}
