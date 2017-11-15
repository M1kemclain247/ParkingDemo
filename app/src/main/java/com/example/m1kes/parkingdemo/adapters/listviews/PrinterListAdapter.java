package com.example.m1kes.parkingdemo.adapters.listviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.callbacks.OnPrinterClicked;
import com.example.m1kes.parkingdemo.models.Printer;

import java.util.List;

public class PrinterListAdapter extends ArrayAdapter<Printer> {


    private OnPrinterClicked onPrinterClicked;

    public PrinterListAdapter(Context context, int resource) {
        super(context, resource);
    }
    public PrinterListAdapter(Context context, int resource,List<Printer> printers,OnPrinterClicked onPrinterClicked) {
        super(context, resource,printers);
        this.onPrinterClicked = onPrinterClicked;
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.printer_row, null);
        }

        Printer p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.id);
            TextView tt3 = (TextView) v.findViewById(R.id.description);

            if (tt1 != null) {
                tt1.setText(p.getPrinterCode());
            }
            if (tt3 != null) {
                tt3.setText(p.getPrinterIMEI());
            }
        }
        v.setClickable(true);
        v.setFocusable(true);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onPrinterClicked.onClicked(position);
            }
        });


        return v;
    }



}
