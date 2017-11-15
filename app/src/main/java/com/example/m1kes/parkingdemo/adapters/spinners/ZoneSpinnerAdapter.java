package com.example.m1kes.parkingdemo.adapters.spinners;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.models.Zone;

import java.util.ArrayList;
import java.util.List;

public class ZoneSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private Context context;
    private List<Zone> zones;

    public ZoneSpinnerAdapter(Context context, List<Zone> zones){
        this.context = context;
        this.zones =  zones;
    }

    @Override
    public int getCount() {
        return zones.size();
    }

    @Override
    public Object getItem(int position) {
        return zones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  (long)position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setPadding(28, 28, 28, 28);
        txt.setTextSize(18);
        txt.setGravity(Gravity.CENTER_VERTICAL);
        txt.setGravity(Gravity.CENTER_HORIZONTAL);
        txt.setText(zones.get(position).getName());
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView txt = new TextView(context);
        txt.setGravity(Gravity.LEFT);
        txt.setPadding(12, 12, 12, 12);
        txt.setTextSize(16);
        txt.setText(zones.get(position).getName());
        txt.setTextColor(Color.parseColor("#000000"));
        return  txt;
    }
}
