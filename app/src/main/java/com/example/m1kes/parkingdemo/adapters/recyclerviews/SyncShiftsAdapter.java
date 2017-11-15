package com.example.m1kes.parkingdemo.adapters.recyclerviews;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnShiftItemSelected;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnTransactionSelected;
import com.example.m1kes.parkingdemo.models.Shift;
import com.example.m1kes.parkingdemo.models.Transaction;
import com.example.m1kes.parkingdemo.util.GeneralUtils;

import java.text.SimpleDateFormat;
import java.util.List;

public class SyncShiftsAdapter extends RecyclerView.Adapter<SyncShiftsAdapter.RecyclerViewHolder> {


    private List<Shift> shifts;
    private Context context;
    private RecyclerViewHolder viewHolder;
    private OnShiftItemSelected clickCallback;

    public SyncShiftsAdapter(Context context,List<Shift> shifts,OnShiftItemSelected clickCallback){
        this.context = context;
        this.shifts = shifts;
        this.clickCallback = clickCallback;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shift_item_row,parent,false);
        viewHolder = new RecyclerViewHolder(view,context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {


        Shift shift = shifts.get(position);
        if(shift!=null){

            holder.txtShiftNum.setText(""+shift.getId());
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            String shiftCreationTime = GeneralUtils.getFormattedDate(shift.getStart_time());
            holder.txtShiftDateCreated.setText(shiftCreationTime);
            if(shift.isActive()) {
                holder.txtShiftActiveStatus.setText("True");
            }else{
                holder.txtShiftActiveStatus.setText("False");
            }
        }


    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder{


        TextView txtShiftNum,txtShiftDateCreated,txtShiftActiveStatus;

        RecyclerViewHolder(View view, Context context){
            super(view);


            txtShiftNum = (TextView)view.findViewById(R.id.txtShiftNum);
            txtShiftDateCreated = (TextView)view.findViewById(R.id.txtShiftDateCreated);
            txtShiftActiveStatus = (TextView)view.findViewById(R.id.txtShiftActiveStatus);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickCallback!=null) {
                            clickCallback.onClicked(shifts.get(getAdapterPosition()));
                            System.out.println(shifts.get(getAdapterPosition()));
                    }
                }
            });

        }
    }
}
