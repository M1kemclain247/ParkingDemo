package com.example.m1kes.parkingdemo.adapters.recyclerviews;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.m1kes.parkingdemo.R;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnHistoryItemSelected;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnJsonExportsItemSelected;
import com.example.m1kes.parkingdemo.models.SearchHistory;
import com.example.m1kes.parkingdemo.modules.supervisor.models.JsonExports;

import java.util.List;

public class JsonExportsRecyclerAdapter extends RecyclerView.Adapter<JsonExportsRecyclerAdapter.RecyclerViewHolder>{

    private List<JsonExports> jsonExports;
    private Context context;
    private RecyclerViewHolder viewHolder;
    private OnJsonExportsItemSelected clickCallback;

    public JsonExportsRecyclerAdapter(Context context,List<JsonExports> jsonExports,OnJsonExportsItemSelected clickCallback){
        this.context = context;
        this.jsonExports = jsonExports;
        this.clickCallback = clickCallback;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.json_exports_item_row,parent,false);
        viewHolder = new RecyclerViewHolder(view,context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {


        JsonExports jsonExport = jsonExports.get(position);

        if(jsonExport!=null){

            holder.txtFilePath.setText(jsonExport.getFilePath());
        }



    }

    @Override
    public int getItemCount() {
        return jsonExports.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder{


        TextView txtFilePath;

        RecyclerViewHolder(View view, Context context){
            super(view);


            txtFilePath = (TextView) view.findViewById(R.id.txtFilePath);



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickCallback!=null) {
                        if(jsonExports.get(getAdapterPosition())!=null) {
                            clickCallback.onClick(jsonExports.get(getAdapterPosition()));
                            System.out.println(jsonExports.get(getAdapterPosition()));
                        }
                    }
                }
            });

        }
    }




}
