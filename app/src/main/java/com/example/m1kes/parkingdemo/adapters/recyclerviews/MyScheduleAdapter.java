package com.example.m1kes.parkingdemo.adapters.recyclerviews;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.models.Schedule;

import java.util.List;

public class MyScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<Object> allData;


    public MyScheduleAdapter(List<Object> allData) {
      this.allData = allData;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_schedule_item_row,parent,false);
            return new VHItem(view);
        } else if (viewType == TYPE_HEADER) {
            //inflate your layout and pass it to view holder
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_schedule_item_header,parent,false);
            return new VHHeader(view);
        }

        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof VHItem) {
            Schedule schedule = (Schedule)getItem(position);
            //cast holder to VHItem and set data
            ((VHItem) holder).lunchTime.setText(schedule.getLunchTime());
            ((VHItem) holder).zone.setText(schedule.getZone());
            ((VHItem) holder).precinct.setText(schedule.getPrecinct());
            ((VHItem) holder).target.setText("$"+String.valueOf(schedule.getTarget())+"0");

        } else if (holder instanceof VHHeader) {
            //cast holder to VHHeader and set data for header.
            String dayOfWeekTitle = (String)getItem(position);
            ((VHHeader) holder).dayOfWeek.setText(dayOfWeekTitle);
        }
    }

    @Override
    public int getItemCount() {
        return allData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(allData.get(position) instanceof String){
            return TYPE_HEADER;
        }

        return TYPE_ITEM;
    }

    private Object getItem(int position) {
      return allData.get(position);
    }

    class VHItem extends RecyclerView.ViewHolder {
        TextView lunchTime,zone,precinct,target;

        public VHItem(View itemView) {
            super(itemView);


            lunchTime = (TextView)itemView.findViewById(R.id.txtScheduleLunchTime);
            zone = (TextView)itemView.findViewById(R.id.txtScheduleZone);
            precinct = (TextView)itemView.findViewById(R.id.txtSchedulePrecinct);
            target = (TextView)itemView.findViewById(R.id.txtScheduleTarget);
        }



    }

    class VHHeader extends RecyclerView.ViewHolder {
        TextView dayOfWeek;

        public VHHeader(View itemView) {
            super(itemView);

            dayOfWeek = (TextView)itemView.findViewById(R.id.txtScheduleHeader);

        }
    }





}
