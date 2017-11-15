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
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnHistoryItemSelected;
import com.example.m1kes.parkingdemo.adapters.recyclerviews.onclicks.OnTransactionSelected;
import com.example.m1kes.parkingdemo.models.SearchHistory;
import com.example.m1kes.parkingdemo.models.Transaction;

import org.w3c.dom.Text;

import java.util.List;

public class SearchHistoryRecylerAdapter extends RecyclerView.Adapter<SearchHistoryRecylerAdapter.RecyclerViewHolder>{



    private List<SearchHistory> searchHistories;
    private Context context;
    private RecyclerViewHolder viewHolder;
    private OnHistoryItemSelected clickCallback;

    public SearchHistoryRecylerAdapter(Context context,List<SearchHistory> searchHistories,OnHistoryItemSelected clickCallback){
        this.context = context;
        this.searchHistories = searchHistories;
        this.clickCallback = clickCallback;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_item_row,parent,false);
        viewHolder = new RecyclerViewHolder(view,context);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {


        SearchHistory searchHistory = searchHistories.get(position);

        if(searchHistory!=null){

            holder.txtRegHistory.setText(searchHistory.getVehicleReg());

        }



    }

    @Override
    public int getItemCount() {
        return searchHistories.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder{


        TextView txtRegHistory;

        RecyclerViewHolder(View view, Context context){
            super(view);


            txtRegHistory = (TextView) view.findViewById(R.id.txtRegHistory);



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(clickCallback!=null) {
                        if(searchHistories.get(getAdapterPosition())!=null) {
                            clickCallback.onClick(searchHistories.get(getAdapterPosition()));
                            System.out.println(searchHistories.get(getAdapterPosition()));
                        }
                    }
                }
            });

        }
    }

}
