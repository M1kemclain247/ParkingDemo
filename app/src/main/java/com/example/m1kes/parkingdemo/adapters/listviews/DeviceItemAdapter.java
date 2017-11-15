package com.example.m1kes.parkingdemo.adapters.listviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.adapters.listviews.callbacks.OnDeviceItemClicked;
import com.example.m1kes.parkingdemo.callbacks.OnPrinterClicked;
import com.example.m1kes.parkingdemo.models.Printer;
import com.example.m1kes.parkingdemo.printer.bluetooth.DeviceItem;

import java.util.List;



public class DeviceItemAdapter extends ArrayAdapter<DeviceItem> {


    private OnDeviceItemClicked onDeviceItemClicked;

    public DeviceItemAdapter(Context context, int resource) {
        super(context, resource);
    }
    public DeviceItemAdapter(Context context, int resource, List<DeviceItem> deviceItems, OnDeviceItemClicked onDeviceItemClicked) {
        super(context, resource,deviceItems);
        this.onDeviceItemClicked = onDeviceItemClicked;
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.bluetooth_device_item, null);
        }

        final DeviceItem deviceItem = getItem(position);

        if (deviceItem != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.txtDeviceName);
            TextView tt3 = (TextView) v.findViewById(R.id.txtDeviceBMac);

            if (tt1 != null) {
                tt1.setText(deviceItem.getDeviceName());
            }
            if (tt3 != null) {
                tt3.setText(deviceItem.getAddress());
            }
        }
        v.setClickable(true);
        v.setFocusable(true);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeviceItemClicked.onClicked(deviceItem);
            }
        });

        return v;
    }



}
